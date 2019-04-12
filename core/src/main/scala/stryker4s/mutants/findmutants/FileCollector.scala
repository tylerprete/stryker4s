package stryker4s.mutants.findmutants

import java.io.IOException
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

import grizzled.slf4j.Logging
import stryker4s.config.Config
import stryker4s.extension.FileExtensions._
import stryker4s.run.process.{Command, ProcessRunner}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

trait SourceCollector {
  protected val processRunner: ProcessRunner

  def collectFilesToMutate(): Iterable[Path]
  def filesToCopy: Iterable[Path]
}

class FileCollector(protected val processRunner: ProcessRunner)(implicit config: Config)
    extends SourceCollector
    with Logging {

  /**
    * Get path separator because windows and unix systems have different separators.
    */
  private[this] val pathSeparator = System.lineSeparator()

  /**
    *  Collect all files that are going to be mutated.
    * Files that are configured to be excluded, in the target folder, or directories are skipped
    */
  override def collectFilesToMutate(): Iterable[Path] = {
    filesToMutate
      .filterNot(filesToExcludeFromMutation.contains(_))
      .filterNot(isInTargetDirectory)
      .filterNot(_.toFile.isDirectory)
  }

  /**
    * Collect all files that are needed to be copied over to the Stryker4s-tmp folder.
    *
    * Option 1: Copy every file that is listed by the 'files' config setting
    * Option 2: Copy every file that is listed by git.
    * Option 3: Copy every file in the 'baseDir' excluding target folders.
    */
  override def filesToCopy: Iterable[Path] = {
    (listFilesBasedOnConfiguration() orElse
      listFilesBasedOnGit(processRunner) getOrElse
      listAllFiles())
      .filterNot(isInTargetDirectory)
      .filter(_.toFile.exists)
  }

  /**
    * List all files based on the 'files' configuration key from stryker4s.conf.
    */
  private[this] def listFilesBasedOnConfiguration(): Option[Iterable[Path]] = {
    config.files.map(glob)
  }

  /**
    * List all files based on `git ls-files` command.
    */
  private[this] def listFilesBasedOnGit(processRunner: ProcessRunner): Option[Iterable[Path]] = {
    processRunner(Command("git ls-files", "--others --exclude-standard --cached"), config.baseDir) match {
      case Success(files) => Option(files.map(config.baseDir / _).distinct)
      case Failure(_)     => None
    }
  }

  /**
    * List all files from the base directory specified in the Stryker4s basedir config key.
    */
  private[this] def listAllFiles(): Iterable[Path] = {
    import scala.collection.JavaConverters._
    warn("No 'files' specified and not a git repository.")
    warn("Falling back to copying everything except the 'target/' folder(s)")
    Files.walk(config.baseDir).iterator().asScala.toIterable.toList
  }

  private[this] def glob(list: Seq[String]): Seq[Path] = {
    list
      .flatMap(glob(_))
      .distinct

  }

  def glob(glob: String)(implicit config: Config): Seq[Path] = {
    val found = ListBuffer.empty[Path]
    val pathMatcher = FileSystems.getDefault.getPathMatcher(s"glob:$glob")
    Files.walkFileTree(
      config.baseDir,
      new SimpleFileVisitor[Path]() {
        override def visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult = {
          val relativePath = path.relativePath
          if (pathMatcher.matches(relativePath)) found += path
          FileVisitResult.CONTINUE

        }
        override def visitFileFailed(file: Path, exc: IOException) = FileVisitResult.CONTINUE
      }
    )
    found
  }

  private[this] val filesToMutate = glob(
    config.mutate.filterNot(file => file.startsWith("!"))
  )

  private[this] val filesToExcludeFromMutation: Seq[Path] = glob(
    config.mutate
      .filter(file => file.startsWith("!"))
      .map(file => file.stripPrefix("!"))
  )

  /**
    * Is the file in the target folder, and thus should not be copied over
    */
  private[this] def isInTargetDirectory(file: Path): Boolean = {
    val pathString = file.relativePath.toString

    val bool = pathString.isEmpty || (file.toFile.isDirectory && pathString == "target") ||
      pathString.startsWith(s"target$pathSeparator") ||
      pathString.contains(s"${pathSeparator}target$pathSeparator") ||
      pathString.endsWith(s"${pathSeparator}target")
    bool
  }
}
