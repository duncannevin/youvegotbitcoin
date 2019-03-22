package com.giftedprimate.daos
import com.giftedprimate.loggers.DAOLogger
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoCollection, _}
import org.mongodb.scala.model.Indexes.ascending

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait EmailbitcoinDAO[T] {
  val database: MongoDatabase
  val collectionName: String
  val indexKey: String
  val codecRegistry: CodecRegistry
  val collection: MongoCollection[T]
  val logger: DAOLogger

  def indexCollection(): Unit = {
    collection.createIndex(ascending(indexKey)).toFuture().onComplete {
      case Success(_) => logger.indexCreated(indexKey)
      case Failure(_) => logger.indexFailure(indexKey)
    }
  }
}
