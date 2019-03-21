package com.giftedprimate.configuration

import com.typesafe.config.ConfigFactory

trait ConfigModule {

  private val config = ConfigFactory.load()

  object emailbitcoin {
    object server {
      val host: String = config.getString("emailbitcoin.server.host")
      val port: Int = config.getInt("emailbitcoin.server.port")
    }

    object bitcoin {
      val network: String = config.getString("emailbitcoin.bitcoin.network")
    }

    object mongodb {
      val name: String = config.getString("emailbitcoin.mongodb.name")
      val location: String = config.getString("emailbitcoin.mongodb.location")
    }
  }
}
