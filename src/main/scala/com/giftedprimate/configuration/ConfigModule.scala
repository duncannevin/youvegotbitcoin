package com.giftedprimate.configuration

import com.typesafe.config.ConfigFactory

class ConfigModule {

  private val config = ConfigFactory.load()

  lazy val host: String =
    config.getString("emailbitcoin.server.host")

  lazy val port: Int =
    config.getInt("emailbitcoin.server.port")

  lazy val network: String =
    config.getString("emailbitcoin.network")

}
