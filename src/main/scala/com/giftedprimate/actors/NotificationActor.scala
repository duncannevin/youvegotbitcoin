package com.giftedprimate.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.daos.{RecipientWalletDAO, EmailBtcTransactionDAO}
import com.giftedprimate.loggers.NotificationLogger
import com.google.inject.Inject

object NotificationActor {
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

  override def receive: Receive = {
    case _ => logger.unrecognizedMessageSentToActor()
  }
}
