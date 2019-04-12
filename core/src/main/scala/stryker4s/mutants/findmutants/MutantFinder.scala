package stryker4s.mutants.findmutants

import java.nio.file.Path

import grizzled.slf4j.Logging
import stryker4s.config.Config
import stryker4s.extension.FileExtensions._
import stryker4s.model.{Mutant, MutationsInSource}

import scala.meta._
import scala.meta.parsers.Parsed

class MutantFinder(matcher: MutantMatcher)(implicit config: Config) extends Logging {

  def mutantsInFile(filePath: Path): MutationsInSource = {
    val parsedSource = parseFile(filePath)
    val (included, excluded) = findMutants(parsedSource)
    MutationsInSource(parsedSource, included, excluded)
  }

  def findMutants(source: Source): (Seq[Mutant], Int) = {
    val (included, excluded) = source.collect(matcher.allMatchers).flatten.partition(_.isDefined)
    (included.flatten, excluded.size)
  }

  def parseFile(file: Path): Source =
    file.parse[Source] match {
      case Parsed.Success(source) =>
        source
      case Parsed.Error(_, msg, ex) =>
        error(s"Error while parsing file '${file.relativePath}', $msg")
        throw ex
    }
}
