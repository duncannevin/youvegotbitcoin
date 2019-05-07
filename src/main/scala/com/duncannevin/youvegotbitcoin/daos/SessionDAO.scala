package com.duncannevin.youvegotbitcoin.daos

import java.security.InvalidParameterException

import akka.http.scaladsl.model.DateTime
import com.duncannevin.youvegotbitcoin.entities.{RecipientWallet, Session}
import com.duncannevin.youvegotbitcoin.loggers.DAOLogger
import com.google.inject.Inject
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.{FindOneAndUpdateOptions, ReturnDocument}
import org.mongodb.scala.{MongoCollection, MongoDatabase}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionDAO @Inject()(
    mongoDatabase: MongoDatabase,
    logger: DAOLogger,
    recipientWalletDAO: RecipientWalletDAO
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

  def findWithWallet(
      sessionId: String): Future[(Option[Session], Option[RecipientWallet])] =
    for {
      session <- collection
        .find(equal("sessionId", sessionId))
        .first()
        .toFutureOption()
      wallet <- recipientWalletDAO.find(session.map(_.publicKey).getOrElse(""))
    } yield (session, wallet)

  def findByPublicKey(publicKey: String): Future[Option[Session]] =
    for {
      sessionOpt <- collection
        .find(equal("publicKey", publicKey))
        .first()
        .toFutureOption()
    } yield sessionOpt

  def updateStatus(publicKey: String, status: String): Future[Session] = {
    val validStatus = List("pending", "funded", "complete")
    if (!validStatus.contains(status)) {
      throw new InvalidParameterException(
        s"Valid parameters are: ${validStatus.mkString(" ")}")
    }
    for {
      session <- collection
        .findOneAndUpdate(
          equal("publicKey", publicKey),
          combine(set("status", status),
                  set("updatedAt", DateTime.now.toString)),
          FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
        .toFuture()
    } yield session
  }

  indexCollection(logger)
}
