package com.giftedprimate.transaction
import akka.actor.{Actor, ActorSystem, Props}
import com.giftedprimate.configuration.ConfigModule
import com.giftedprimate.loggers.TransactionLog

import scala.concurrent.ExecutionContext

object TransactionActor {
  final case class CreateWallet(creationForm: CreationForm)

  def props(
      config: ConfigModule,
      transactionControl: TransactionControl,
      transactionLog: TransactionLog,
      ec: ExecutionContext
  ): Props = Props(
    new TransactionActor(config, transactionControl, transactionLog)(ec)
  )
}

class TransactionActor(
    config: ConfigModule,
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
    case _ => transactionLog.unrecognizedMessageSentToActor()
  }
}
