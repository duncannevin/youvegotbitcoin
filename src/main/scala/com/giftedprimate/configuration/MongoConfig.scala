package com.giftedprimate.configuration

class MongoConfig extends ConfigModule {
  lazy val mongoName: String = emailbitcoin.mongodb.name
  lazy val mongoLocation: String = emailbitcoin.mongodb.location
}
