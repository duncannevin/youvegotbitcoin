package com.giftedprimate.emailbitcoin.daos

import com.giftedprimate.emailbitcoin.entities.Session
import com.giftedprimate.emailbitcoin.loggers.DAOLogger
import com.google.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{MongoCollection, MongoDatabase}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SessionDAO @Inject()(
    mongoDatabase: MongoDatabase,
    logger: DAOLogger
) extends EmailbitcoinDAO[Session] {
  override val database: MongoDatabase = mongoDatabase
  override val collectionName: String = "session"
  override val indexKey: String = "sessionId"
  override val codecRegistry: CodecRegistry = Session.codecRegistry
  override val collection: MongoCollection[Session] = database
    .getCollection[Session](collectionName)
    .withCodecRegistry(codecRegistry)

  def save(session: Session): Future[Session] =
    for {
      _ <- collection.insertOne(session).toFuture()
    } yield session

  def find(sessionId: String): Future[Option[Session]] =
    for {
      sessionOpt <- collection
        .find(equal("sessionId", sessionId))
        .first()
        .toFutureOption()
    } yield sessionOpt

  def findByPublicKey(publicKey: String): Future[Option[Session]] =
    for (sessionOpt <- collection
           .find(equal("publicKey", publicKey))
           .first()
           .toFutureOption()) yield sessionOpt

  indexCollection(logger)
}
