package com.giftedprimate.configuration

class ServerConfig extends ConfigModule {
  lazy val host: String = emailbitcoin.server.host
  lazy val port: Int = emailbitcoin.server.port
}
