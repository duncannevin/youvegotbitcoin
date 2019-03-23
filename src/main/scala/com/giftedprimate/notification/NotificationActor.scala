package com.giftedprimate.notification
import akka.actor.{Actor, Props}
import com.giftedprimate.daos.{RecipientWalletDAO, TransactionDAO}
import com.giftedprimate.loggers.NotificationLogger
import com.google.inject.Inject

object NotificationActor {
  def props(logger: NotificationLogger,
            transactionDAO: TransactionDAO,
            recipientWalletDAO: RecipientWalletDAO): Props =
    Props(new NotificationActor(logger, transactionDAO, recipientWalletDAO))
}

class NotificationActor @Inject()(
    logger: NotificationLogger,
    transactionDAO: TransactionDAO,
    recipientWalletDAO: RecipientWalletDAO
) extends Actor {

  override def receive: Receive = {
    case _ => logger.unrecognizedMessageSentToActor()
  }
}
