package com.giftedprimate.daos
import com.giftedprimate.loggers.DAOLogger
import com.giftedprimate.models.RecipientWallet
import javax.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class RecipientWalletDAO @Inject()(
    mongoDatabase: MongoDatabase,
    DAOLogger: DAOLogger
) extends EmailbitcoinDAO[RecipientWallet] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "recipient-wallet"
  override val indexKey: String = "publicKeyAddress"
  override val codecRegistry: CodecRegistry = RecipientWallet.codecRegistry
  override val collection: MongoCollection[RecipientWallet] = database
    .getCollection[RecipientWallet](collectionName)
    .withCodecRegistry(codecRegistry)

  def save(recipientWallet: RecipientWallet): Future[RecipientWallet] =
    for {
      _ <- collection.insertOne(recipientWallet).toFuture()
    } yield recipientWallet

  indexCollection(DAOLogger)
}
