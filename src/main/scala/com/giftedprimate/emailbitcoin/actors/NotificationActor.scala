package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO
}
import com.giftedprimate.emailbitcoin.entities.{EBTransaction, Session}
import com.giftedprimate.emailbitcoin.loggers.NotificationLogger
import com.google.inject.Inject

object NotificationActor {
  final case class Notify(session: Session, transaction: EBTransaction)

  def props(logger: NotificationLogger,
            transactionDAO: EBTransactionDAO,
            recipientWalletDAO: RecipientWalletDAO): Props =
    Props(new NotificationActor(logger, transactionDAO, recipientWalletDAO))
}

class NotificationActor @Inject()(
    logger: NotificationLogger,
    transactionDAO: EBTransactionDAO,
    recipientWalletDAO: RecipientWalletDAO
) extends Actor {
  import NotificationActor._

  override def receive: Receive = {
    case Notify(session, transaction) =>
      println(
        s"NOTIFY: ${transaction.creationForm.recipientEmail} SESSIONID: ${session.sessionId}")
    case _ => logger.unrecognizedMessageSentToActor()
  }
}
