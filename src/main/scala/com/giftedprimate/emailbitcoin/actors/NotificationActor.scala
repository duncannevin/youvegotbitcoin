package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.configuration.{
  ServerConfig,
  SiteLocationConfig
}
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
            recipientWalletDAO: RecipientWalletDAO,
            siteLocationConfig: SiteLocationConfig): Props =
    Props(
      new NotificationActor(logger,
                            transactionDAO,
                            recipientWalletDAO,
                            siteLocationConfig))
}

class NotificationActor @Inject()(
    logger: NotificationLogger,
    transactionDAO: EBTransactionDAO,
    recipientWalletDAO: RecipientWalletDAO,
    siteLocationConfig: SiteLocationConfig
) extends Actor {
  import NotificationActor._

  private def createSessionUrl(kind: String, sessionId: String) = kind match {
    case "sender" => s"${siteLocationConfig.url}/sender?sessionid=$sessionId"
    case "recipient" =>
      s"${siteLocationConfig.url}/recipient?sessionid=$sessionId"
  }

  override def receive: Receive = {
    case Notify(session, transaction) =>
      logger.notifySender(transaction.creationForm.senderEmail,
                          createSessionUrl("sender", session.sessionId))
      logger.notifyRecipient(transaction.creationForm.recipientEmail,
                             createSessionUrl("recipient", session.sessionId))
    case _ => logger.unrecognizedMessageSentToActor()
  }
}
