package stryker4s.scalatest

import java.io.FileNotFoundException
import java.nio.file.{Path, Paths}

object FileUtil {

  private lazy val classLoader = getClass.getClassLoader

  def getResource(name: String): Path =
    Option(classLoader.getResource(name))
      .map(_.toURI)
      .map(s => Paths.get(s))
      .getOrElse(throw new FileNotFoundException(s"File $name could not be found"))
}
