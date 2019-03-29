package com.giftedprimate.emailbitcoin.daos
import com.giftedprimate.emailbitcoin.loggers.DAOLogger
import com.giftedprimate.emailbitcoin.entities.RecipientWallet
import javax.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class RecipientWalletDAO @Inject()(
    mongoDatabase: MongoDatabase,
    DAOLogger: DAOLogger
) extends EmailbitcoinDAO[RecipientWallet] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "recipient_wallet"
  override val indexKey: String = "publicKey"
  override val codecRegistry: CodecRegistry = RecipientWallet.codecRegistry
  override val collection: MongoCollection[RecipientWallet] = database
    .getCollection[RecipientWallet](collectionName)
    .withCodecRegistry(codecRegistry)

  def save(recipientWallet: RecipientWallet): Future[RecipientWallet] =
    for {
      _ <- collection.insertOne(recipientWallet).toFuture()
    } yield recipientWallet

  def find(publicKey: String): Future[Option[RecipientWallet]] =
    for {
      wallet <- collection
        .find(equal("publicKey", publicKey))
        .first()
        .toFutureOption()
    } yield wallet

  def getAll: Future[Seq[RecipientWallet]] =
    for {
      wallets <- collection.find().toFuture()
    } yield wallets

  indexCollection(DAOLogger)
}
