package stryker4s.config

import java.nio.file.{Files, Paths}

import stryker4s.files.File
import stryker4s.testutil.Stryker4sSuite

class ConfigTest extends Stryker4sSuite {
  describe("toHoconString") {
    it("should print toString with default values") {
      val sut = Config()

      val result = sut.toHoconString

      val expected =
        s"""base-dir="${File.currentWorkingDirectory.toString.replace("\\", "\\\\")}"
           |excluded-mutations=[]
           |mutate=[
           |    "**/main/scala/**/*.scala"
           |]
           |reporters=[
           |    console
           |]
           |test-runner {
           |    args=test
           |    command=sbt
           |    type=commandrunner
           |}
           |thresholds {
           |    break=0
           |    high=80
           |    low=60
           |}
           |""".stripMargin
      result.toString should equal(expected.toString)
    }

    it("should print toString with changed values") {
      val filePaths = List("**/main/scala/**/Foo.scala", "**/main/scala/**/Bar.scala")
      val sut = Config(filePaths,
                       Paths.get("tmp"),
                       testRunner = CommandRunner("mvn", "clean test"),
                       excludedMutations = ExcludedMutations(Set("BooleanLiteral")))

      val result = sut.toHoconString

      val expected =
        s"""base-dir=tmp
           |excluded-mutations=[
           |    BooleanLiteral
           |]
           |mutate=[
           |    "**/main/scala/**/Foo.scala",
           |    "**/main/scala/**/Bar.scala"
           |]
           |reporters=[
           |    console
           |]
           |test-runner {
           |    args="clean test"
           |    command=mvn
           |    type=commandrunner
           |}
           |thresholds {
           |    break=0
           |    high=80
           |    low=60
           |}
           |""".stripMargin
      result.toString should equal(expected.toString)
    }
  }
}
