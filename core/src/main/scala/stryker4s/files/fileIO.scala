package stryker4s.files

import java.nio.file.Path

import scala.io.Source

trait FileIO {
  def readResource(resource: String): Source

  def createAndWrite(file: Path, content: Iterator[Char]): Unit

  def createAndWrite(file: Path, content: String): Unit
}

object DiskFileIO extends FileIO {
  override def readResource(resource: String): Source = {
    val stream = getClass.getClassLoader.getResourceAsStream(resource)
    Source.fromInputStream(stream)
  }

  override def createAndWrite(file: Path, content: Iterator[Char]): Unit = {
    ???
  }

  override def createAndWrite(file: Path, content: String): Unit = createAndWrite(file, content.iterator)
}
