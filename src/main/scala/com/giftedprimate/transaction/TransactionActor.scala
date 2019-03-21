package com.giftedprimate.transaction

import akka.actor.{Actor, Props}
import com.giftedprimate.loggers.TransactionLog

import scala.concurrent.ExecutionContext

object TransactionActor {
  final case class CreateWallet(creationForm: CreationForm)

  def props(
      transactionControl: TransactionControl,
      transactionLog: TransactionLog,
      ec: ExecutionContext
  ): Props = Props(
    new TransactionActor(transactionControl, transactionLog)(ec)
  )
}

class TransactionActor(
    transactionControl: TransactionControl,
    transactionLog: TransactionLog
)(implicit ec: ExecutionContext)
    extends Actor {
  import TransactionActor._

  override def receive: Receive = {
    case CreateWallet(creationForm) =>
      for {
        recipientWallet <- transactionControl.addWallet(creationForm)
      } yield sender ! recipientWallet.publicKeyAddress
    case _ =>
      transactionLog.unrecognizedMessageSentToActor()
  }
}
