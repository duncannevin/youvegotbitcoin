package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.DateTime
import com.giftedprimate.emailbitcoin.actors.SessionActor.CreateRecipientSession
import com.giftedprimate.emailbitcoin.daos.{
  EmailBtcTransactionDAO,
  RecipientWalletDAO
}
import com.giftedprimate.emailbitcoin.loggers.TransactionLogger
import com.giftedprimate.emailbitcoin.entities.{
  EmailBtcTransaction,
  IncomingTransaction
}
import com.google.inject.Inject
import org.joda.time.DateTimeZone

import scala.concurrent.ExecutionContext.Implicits.global

object TransactionActor {
  final case class ExistingTransaction(transactionId: String)
  final case class NewTransaction(publicKey: String,
                                  transactionId: String,
                                  value: Long)
  final case class SaveTransaction(transaction: EmailBtcTransaction)

  def props(
      logger: TransactionLogger,
      recipientWalletDAO: RecipientWalletDAO,
      transactionDAO: EmailBtcTransactionDAO,
      sessionActor: ActorRef
  ): Props = Props(
    new TransactionActor(logger,
                         recipientWalletDAO,
                         transactionDAO,
                         sessionActor)
  )
}

class TransactionActor @Inject()(
    logger: TransactionLogger,
    recipientWalletDAO: RecipientWalletDAO,
    transactionDAO: EmailBtcTransactionDAO,
    sessionActor: ActorRef
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
            val transaction = EmailBtcTransaction(
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
      } yield sessionActor ! CreateRecipientSession(transaction)
    case _ =>
      logger.unrecognizedMessageSentToActor()
  }
}
