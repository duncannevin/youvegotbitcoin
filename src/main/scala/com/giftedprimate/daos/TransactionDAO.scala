package com.giftedprimate.daos
import com.giftedprimate.loggers.DAOLogger
import com.giftedprimate.models.Transaction
import com.google.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class TransactionDAO @Inject()(
    mongoDatabase: MongoDatabase,
    logger: DAOLogger
) extends EmailbitcoinDAO[Transaction] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "transaction"
  override val indexKey: String = "transactionId"
  override val codecRegistry: CodecRegistry = Transaction.codecRegistry
  override val collection: MongoCollection[Transaction] = database
    .getCollection[Transaction](collectionName)
    .withCodecRegistry(codecRegistry)

  def save(transaction: Transaction): Future[Transaction] =
    for {
      _ <- collection.insertOne(transaction).toFuture()
    } yield transaction

  def find(transactionId: String): Future[Option[Transaction]] =
    for {
      maybeTransaction <- collection
        .find(equal("transactionId", transactionId))
        .first()
        .toFutureOption()
    } yield maybeTransaction

  indexCollection(logger)
}
