package com.giftedprimate.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.daos.{EmailBtcTransactionDAO, RecipientWalletDAO}
import com.giftedprimate.entities.{EmailBtcTransaction, Session}
import com.giftedprimate.loggers.NotificationLogger
import com.google.inject.Inject

object NotificationActor {
  final case class NotifyRecipient(session: Session,
                                   transaction: EmailBtcTransaction)

  def props(logger: NotificationLogger,
            transactionDAO: EmailBtcTransactionDAO,
            recipientWalletDAO: RecipientWalletDAO): Props =
    Props(new NotificationActor(logger, transactionDAO, recipientWalletDAO))
}

class NotificationActor @Inject()(
    logger: NotificationLogger,
    transactionDAO: EmailBtcTransactionDAO,
    recipientWalletDAO: RecipientWalletDAO
) extends Actor {
  import NotificationActor._

  override def receive: Receive = {
    case NotifyRecipient(session, transaction) =>
      println(
        s"NOTIFY: ${transaction.creationForm.recipientEmail} SESSIONID: ${session.sessionId}")
    case _ => logger.unrecognizedMessageSentToActor()
  }
}
