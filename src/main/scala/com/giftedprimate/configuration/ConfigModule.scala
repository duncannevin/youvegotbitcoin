package com.giftedprimate.configuration

import com.typesafe.config.ConfigFactory

trait ConfigModule {

  private val config = ConfigFactory.load()

  lazy val host: String =
    config.getString("server.host")

  lazy val port: Int =
    config.getInt("server.port")
}
