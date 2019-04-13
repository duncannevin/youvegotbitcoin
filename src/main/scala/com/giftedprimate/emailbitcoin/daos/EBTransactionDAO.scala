package com.giftedprimate.emailbitcoin.daos
import com.giftedprimate.emailbitcoin.loggers.DAOLogger
import com.giftedprimate.emailbitcoin.entities.EBTransaction
import com.google.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class EBTransactionDAO @Inject()(
    mongoDatabase: MongoDatabase,
    logger: DAOLogger
) extends EmailbitcoinDAO[EBTransaction] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "transaction"
  override val indexKey: String = "transactionId"
  override val codecRegistry: CodecRegistry = EBTransaction.codecRegistry
  override val collection: MongoCollection[EBTransaction] = database
    .getCollection[EBTransaction](collectionName)
    .withCodecRegistry(codecRegistry)

  def save(transaction: EBTransaction): Future[EBTransaction] =
    for {
      _ <- collection.insertOne(transaction).toFuture()
    } yield transaction

  def find(transactionId: String): Future[Option[EBTransaction]] =
    for {
      maybeTransaction <- collection
        .find(equal("transactionId", transactionId))
        .first()
        .toFutureOption()
    } yield maybeTransaction

  def findAll(publicKey: String): Future[Seq[EBTransaction]] =
    for {
      transactions <- collection
        .find(equal("publicKey", publicKey))
        .toFuture()
    } yield transactions

  indexCollection(logger)
}
