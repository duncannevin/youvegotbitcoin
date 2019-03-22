package com.giftedprimate.configuration
import org.mongodb.scala.{MongoClient, MongoDatabase}

class MongoConfig extends ConfigModule {
  lazy val mongoName: String = emailbitcoin.mongodb.name
  lazy val mongoLocation: String = emailbitcoin.mongodb.location

  val mongoDb: MongoDatabase = {
    val mongoClient: MongoClient = MongoClient(mongoLocation)
    mongoClient.getDatabase(mongoName)
  }
}
