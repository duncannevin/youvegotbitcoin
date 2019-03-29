package com.giftedprimate.emailbitcoin.actors
import akka.actor.{Actor, ActorRef, Props}
import com.giftedprimate.emailbitcoin.actors.NotificationActor.NotifyRecipient
import com.giftedprimate.emailbitcoin.actors.SessionActor.{
  CreateRecipientSession,
  SaveSession
}
import com.giftedprimate.emailbitcoin.daos.SessionDAO
import com.giftedprimate.emailbitcoin.entities.{EmailBtcTransaction, Session}
import com.giftedprimate.emailbitcoin.loggers.SessionLogger
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object SessionActor {
  final case class CreateRecipientSession(transaction: EmailBtcTransaction)
  final case class SaveSession(transaction: EmailBtcTransaction)

  def props(
      logger: SessionLogger,
      sessionDAO: SessionDAO,
      notificationActor: ActorRef
  ): Props = Props(
    new SessionActor(logger, sessionDAO, notificationActor)
  )
}

class SessionActor @Inject()(
    logger: SessionLogger,
    sessionDAO: SessionDAO,
    notificationActor: ActorRef
) extends Actor {
  override def receive: Receive = {
    case CreateRecipientSession(transaction) =>
      for {
        sessionOpt <- sessionDAO.findByPublicKey(transaction.publicKey)
      } yield
        sessionOpt match {
          case Some(_) => logger.transactionFundedAgain(transaction)
          case None    => self ! SaveSession(transaction)
        }
    case SaveSession(transaction) =>
      for {
        session <- sessionDAO.save(Session(transaction))
      } yield notificationActor ! NotifyRecipient(session, transaction)
    case _ => logger.unrecognizedMessageSentToActor()
  }
}
