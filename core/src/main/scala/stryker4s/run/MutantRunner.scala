package stryker4s.run

import java.nio.file.{Files, Path, StandardCopyOption, StandardOpenOption}

import grizzled.slf4j.Logging
import stryker4s.config.Config
import stryker4s.extension.FileExtensions._
import stryker4s.extension.score.MutationScoreCalculator
import stryker4s.model._
import stryker4s.mutants.findmutants.SourceCollector
import stryker4s.report.Reporter

import scala.concurrent.duration.{Duration, MILLISECONDS}

abstract class MutantRunner(sourceCollector: SourceCollector, reporter: Reporter)(implicit config: Config)
    extends InitialTestRun
    with MutationScoreCalculator
    with Logging {

  val tmpDir: Path = {
    val targetFolder = config.baseDir / "target"
    Files.createDirectories(targetFolder)
    Files.createTempDirectory(targetFolder, "stryker4s-")
  }

  def apply(mutatedFiles: Iterable[MutatedFile]): MutantRunResults = {
    prepareEnv(mutatedFiles)

    initialTestRun(tmpDir)

    val startTime = System.currentTimeMillis()

    val runResults = runMutants(mutatedFiles)

    val duration = Duration(System.currentTimeMillis() - startTime, MILLISECONDS)
    val detected = runResults collect { case d: Detected => d }

    val result = MutantRunResults(runResults, calculateMutationScore(runResults.size, detected.size), duration)
    reporter.reportRunFinished(result)
    result
  }

  private def prepareEnv(mutatedFiles: Iterable[MutatedFile]): Unit = {
    val files = sourceCollector.filesToCopy

    debug("Using temp directory: " + tmpDir)

    files.foreach(copyFile)

    // Overwrite files to mutated files
    mutatedFiles.foreach(writeMutatedFile)
  }

  private def copyFile(file: Path): Unit = {
    val filePath = tmpDir / file.relativePath.toString

    Files.createDirectories(filePath)

    Files.copy(file, filePath, StandardCopyOption.REPLACE_EXISTING)
  }

  private def writeMutatedFile(mutatedFile: MutatedFile): Path = {
    val filePath = mutatedFile.fileOrigin.inSubDir(tmpDir)
    if (!Files.exists(filePath)) Files.createFile(filePath)
    Files.write(filePath, mutatedFile.tree.syntax.getBytes(), StandardOpenOption.TRUNCATE_EXISTING)
  }

  private def runMutants(mutatedFiles: Iterable[MutatedFile]): Iterable[MutantRunResult] = {
    val totalMutants = mutatedFiles.flatMap(_.mutants).size

    for {
      mutatedFile <- mutatedFiles
      subPath = mutatedFile.fileOrigin.relativePath
      mutant <- mutatedFile.mutants
    } yield {
      reporter.reportMutationStart(mutant)
      val result = runMutant(mutant, tmpDir)(subPath)
      reporter.reportMutationComplete(result, totalMutants)
      result
    }
  }

  def runMutant(mutant: Mutant, workingDir: Path): Path => MutantRunResult

}
