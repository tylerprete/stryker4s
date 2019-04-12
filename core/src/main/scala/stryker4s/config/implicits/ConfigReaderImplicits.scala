package stryker4s.config.implicits

import pureconfig.ConfigReader
import stryker4s.config.{ConsoleReporterType, ExcludedMutations, HtmlReporterType, ReporterType}

trait ConfigReaderImplicits {

  implicit private[config] val toReporterList: ConfigReader[ReporterType] =
    ConfigReader[String] map {
      case ConsoleReporterType.name => ConsoleReporterType
      case HtmlReporterType.name    => HtmlReporterType
    }

  implicit private[config] val exclusions: ConfigReader[ExcludedMutations] =
    ConfigReader[List[String]] map (exclusions => ExcludedMutations(exclusions.toSet))
}
