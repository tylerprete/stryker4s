package stryker4s.run.process

import java.nio.file.Path

import scala.util.Try

class WindowsProcessRunner extends ProcessRunner {
  override def apply(command: Command, workingDir: Path): Try[Seq[String]] = {
    super.apply(Command(s"cmd /c ${command.command}", command.args), workingDir)
  }

  override def apply(command: Command, workingDir: Path, envVar: (String, String)): Try[Int] = {
    super.apply(Command(s"cmd /c ${command.command}", command.args), workingDir, envVar)
  }
}
