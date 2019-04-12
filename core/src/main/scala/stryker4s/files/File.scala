package stryker4s.files

import java.nio.file.{Path, Paths}

object File {

  def currentWorkingDirectory: Path = Paths.get("").toAbsolutePath
}
