package com.giftedprimate.daos
import com.giftedprimate.loggers.DAOLogger
import com.giftedprimate.models.RecipientWallet
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}

import scala.concurrent.ExecutionContext

class RecipientWalletDAO(
    mongoDatabase: MongoDatabase
)(
    implicit ec: ExecutionContext
) extends EmailbitcoinDAO[RecipientWallet] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "recipient-wallet"
  override val logger: DAOLogger = new DAOLogger(collectionName)
  override val indexKey: String = "publicKey"
  override val codecRegistry: CodecRegistry = RecipientWallet.codecRegistry
  override val collection: MongoCollection[RecipientWallet] = database
    .getCollection[RecipientWallet](collectionName)
    .withCodecRegistry(codecRegistry)

  indexCollection()
}
