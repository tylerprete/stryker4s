package stryker4s.config.implicits

import com.typesafe.config.ConfigRenderOptions
import pureconfig.ConfigWriter
import stryker4s.config.{ExcludedMutations, ReporterType}

object ConfigWriterImplicits {

  implicit private[config] val exclusionsWriter: ConfigWriter[ExcludedMutations] =
    ConfigWriter[List[String]] contramap (_.exclusions.toList)

  implicit private[config] val reportersWriter: ConfigWriter[ReporterType] =
    ConfigWriter[String] contramap (_.name)

  private[config] val options: ConfigRenderOptions = ConfigRenderOptions
    .defaults()
    .setOriginComments(false)
    .setJson(false)
}
