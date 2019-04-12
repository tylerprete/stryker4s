package stryker4s.extension

import java.nio.file.{Path, Paths}

import stryker4s.config.Config

object FileExtensions {

  implicit class RelativePathExtension(file: Path) {

    /** The path relative to the base-dir of the project.
      * <br>
      *   For example, with the file `projectRoot/src/main`, this function will return `src/main`
      */
    def relativePath(implicit config: Config): Path = config.baseDir.relativize(file)

    /** The directory for this file, using `subDir` param as the base-directory instead of the Config base-dir
      * <br>
      *   For example, with `this` file `projectRoot/src/main` folder, and the parameter file `projectRoot/target/tmp`,
      *   this function will return projectRoot/target/tmp/src/main
      */
    def inSubDir(subDir: Path)(implicit config: Config): Path = subDir / file.relativePath.toString
  }

  implicit class SlashExtension(thisPath: Path) {
    def /(otherPath: Path): Path = /(otherPath.toString)

    def /(otherPath: String): Path = Paths.get(thisPath.toString, otherPath)
  }
}
