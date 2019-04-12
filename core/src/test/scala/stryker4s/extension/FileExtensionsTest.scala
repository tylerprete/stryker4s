package stryker4s.extension

import java.nio.file.Paths

import stryker4s.config.Config
import stryker4s.extension.FileExtensions._
import stryker4s.testutil.Stryker4sSuite

class FileExtensionsTest extends Stryker4sSuite {

  describe("relativePath") {
    implicit val config: Config = Config()

    it("should return the relative path on a file inside the base-dir") {
      val expectedRelativePath =
        Paths.get("core/src/test/scala/stryker4s/extension/FileExtensions.scala")
      val sut = expectedRelativePath.toAbsolutePath

      val result = sut.relativePath

      result should equal(expectedRelativePath)
    }

    it("should return just the file name when a file is in the base-dir") {
      val expectedRelativePath = Paths.get("build.sbt")
      val sut = expectedRelativePath.toAbsolutePath

      val result = sut.relativePath

      result should equal(expectedRelativePath)
    }
  }

  describe("inSubDir") {
    it("should calculate a path relative to the new subDir") {
      implicit val config: Config = Config()
      val baseDir = config.baseDir
      val sut = baseDir / "src" / "main"
      val subDir = baseDir / "target" / "tmp"

      val result = sut.inSubDir(subDir)

      result should equal(baseDir / "target/tmp/src/main")
    }
  }
}
