package com.giftedprimate.transaction

import akka.actor.{Actor, Props}
import com.giftedprimate.daos.{RecipientWalletDAO, TransactionDAO}
import com.giftedprimate.loggers.TransactionLog
import com.giftedprimate.models.{CreationForm, Transaction}
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object TransactionActor {
  final case class ExistingTransaction(transactionId: String)
  final case class NewTransaction(publicKey: String, transactionId: String)
  final case class SaveTransaction(transaction: Transaction)

  def props(
      logger: TransactionLog,
      recipientWalletDAO: RecipientWalletDAO,
      transactionDAO: TransactionDAO
  ): Props = Props(
    new TransactionActor(logger, recipientWalletDAO, transactionDAO)
  )
}

class TransactionActor @Inject()(
    logger: TransactionLog,
    recipientWalletDAO: RecipientWalletDAO,
    transactionDAO: TransactionDAO
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
            self ! NewTransaction(publicKey, transactionId)
        }
    case NewTransaction(publicKey, transactionId) =>
      for {
        recipientWalletOpt <- recipientWalletDAO.find(publicKey)
      } yield
        recipientWalletOpt match {
          case Some(recipientWallet) =>
            val transaction = Transaction(
              publicKey,
              transactionId,
              recipientWallet.createForm.senderEmail,
              recipientWallet.createForm.recipientEmail)
            self ! SaveTransaction(transaction)
          case None => logger.noWallet(publicKey)
        }
    case SaveTransaction(transaction) =>
      for {
        _ <- transactionDAO.save(transaction)
      } yield println(s"SAVED TRANSACTION: ${transaction.publicKey}")
    // todo
    case _ =>
      logger.unrecognizedMessageSentToActor()
  }
}
