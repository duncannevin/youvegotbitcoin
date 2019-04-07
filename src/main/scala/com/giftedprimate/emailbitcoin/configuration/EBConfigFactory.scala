package com.giftedprimate.emailbitcoin.configuration
import com.google.inject.Inject
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.FiniteDuration

class EBConfigFactory @Inject()() {
  private val config: Config = ConfigFactory.load()

  private def getConfig(path: String): String = {
    config.getString(path)
  }

  private def getConfig[T](path: String, converter: String => T): T = {
    converter(sys.env.getOrElse(path.replace(".", "_"), getConfig(path)))
  }

  val siteLocationConfig: SiteLocationConfig = SiteLocationConfig(
    // todo -> add the site url to environment for deployment
    url = getConfig("emailbitcoin.location.url")
  )

  val serverConfig: ServerConfig = ServerConfig(
    host = getConfig("emailbitcoin.server.host", _.toString),
    port = getConfig("emailbitcoin.server.port", _.toInt)
  )

  val bitcoinConfig: BitcoinConfig = BitcoinConfig(
    network = getConfig("emailbitcoin.bitcoin.network", _.toString)
  )

  val mongoConfig: MongoConfig = MongoConfig(
    name = getConfig("emailbitcoin.mongodb.name", _.toString),
    location = getConfig("emailbitcoin.mongodb.location", _.toString)
  )

  val systemConfig: SystemConfig = SystemConfig(
    timeout = FiniteDuration(getConfig("emailbitcoin.system.timeout", _.toInt),
                             "seconds")
  )
}
