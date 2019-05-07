package com.duncannevin.youvegotbitcoin.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.DateTime
import com.duncannevin.youvegotbitcoin.actors.NotificationActor.Notify
import com.duncannevin.youvegotbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO,
  SessionDAO
}
import com.duncannevin.youvegotbitcoin.entities.{
  EBTransaction,
  IncomingTransaction
}
import com.duncannevin.youvegotbitcoin.loggers.TransactionLogger
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object TransactionActor {
  final case class ExistingTransaction(transactionId: String)
  final case class NewTransaction(publicKey: String,
                                  transactionId: String,
                                  value: Long)
  final case class SaveTransaction(transaction: EBTransaction)

  def props(
      logger: TransactionLogger,
      recipientWalletDAO: RecipientWalletDAO,
      transactionDAO: EBTransactionDAO,
      sessionDAO: SessionDAO,
      notificationActor: ActorRef
  ): Props = Props(
    new TransactionActor(logger,
                         recipientWalletDAO,
                         transactionDAO,
                         sessionDAO,
                         notificationActor)
  )
}

class TransactionActor @Inject()(
    logger: TransactionLogger,
    recipientWalletDAO: RecipientWalletDAO,
    transactionDAO: EBTransactionDAO,
    sessionDAO: SessionDAO,
    notificationActor: ActorRef
) extends Actor {
  import TransactionActor._

  override def receive: Receive = {
    case IncomingTransaction(wallet, tx, _, _) =>
      val publicKey: String =
        wallet.getWatchingKey.serializePubB58(wallet.getParams)
      val transactionId: String = tx.getTxId.toString
      for {
        transactionOpt <- transactionDAO.find(transactionId)
      } yield
        transactionOpt match {
          case Some(_) => logger.existingTransaction(transactionId)
          case None =>
            logger.transactionPaid(transactionId)
            self ! NewTransaction(publicKey,
                                  transactionId,
                                  tx.getValue(wallet).value)
        }
    case NewTransaction(publicKey, transactionId, value) =>
      for {
        recipientWalletOpt <- recipientWalletDAO.find(publicKey)
      } yield
        recipientWalletOpt match {
          case Some(recipientWallet) =>
            val transaction = EBTransaction(
              DateTime.now.toString,
              publicKey,
              transactionId,
              recipientWallet.createForm,
              value
            )
            self ! SaveTransaction(transaction)
          case None => logger.noWallet(publicKey)
        }
    case SaveTransaction(transaction) =>
      for {
        _ <- transactionDAO.save(transaction)
        session <- sessionDAO.updateStatus(transaction.publicKey, "funded")
      } yield notificationActor ! Notify(session, transaction)
    case _ =>
      logger.unrecognizedMessageSentToActor()
  }
}
