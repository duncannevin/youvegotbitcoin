package com.giftedprimate.daos
import com.giftedprimate.loggers.DAOLogger
import com.giftedprimate.entities.EmailBtcTransaction
import com.google.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class EmailBtcTransactionDAO @Inject()(
    mongoDatabase: MongoDatabase,
    logger: DAOLogger
) extends EmailbitcoinDAO[EmailBtcTransaction] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "transaction"
  override val indexKey: String = "transactionId"
  override val codecRegistry: CodecRegistry = EmailBtcTransaction.codecRegistry
  override val collection: MongoCollection[EmailBtcTransaction] = database
    .getCollection[EmailBtcTransaction](collectionName)
    .withCodecRegistry(codecRegistry)

  def save(transaction: EmailBtcTransaction): Future[EmailBtcTransaction] =
    for {
      _ <- collection.insertOne(transaction).toFuture()
    } yield transaction

  def find(transactionId: String): Future[Option[EmailBtcTransaction]] =
    for {
      maybeTransaction <- collection
        .find(equal("transactionId", transactionId))
        .first()
        .toFutureOption()
    } yield maybeTransaction

  indexCollection(logger)
}
