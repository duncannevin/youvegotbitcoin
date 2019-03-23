package com.giftedprimate.transaction

import akka.actor.{Actor, Props}
import com.giftedprimate.daos.RecipientWalletDAO
import com.giftedprimate.loggers.TransactionLog
import com.giftedprimate.models.CreationForm
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object TransactionActor {
  final case class CreateWallet(creationForm: CreationForm)

  def props(
      transactionControl: TransactionControl,
      transactionLog: TransactionLog,
      recipientWalletDAO: RecipientWalletDAO
  ): Props = Props(
    new TransactionActor(transactionControl, transactionLog, recipientWalletDAO)
  )
}

class TransactionActor @Inject()(
    transactionControl: TransactionControl,
    transactionLog: TransactionLog,
    recipientWalletDAO: RecipientWalletDAO
) extends Actor {
  import TransactionActor._

  override def receive: Receive = {
    case CreateWallet(creationForm) =>
      for {
        recipientWallet <- transactionControl.addWallet(creationForm)
        _ <- recipientWalletDAO.save(recipientWallet)
      } yield sender ! recipientWallet.publicKeyAddress
    case _ =>
      transactionLog.unrecognizedMessageSentToActor()
  }
}
