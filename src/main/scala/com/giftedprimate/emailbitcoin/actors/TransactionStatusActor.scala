package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.daos.EBTransactionDAO
import com.giftedprimate.emailbitcoin.entities.{
  UnrecognizedMsgException,
  WSRequest
}
import com.giftedprimate.emailbitcoin.websocket.WSConvertFlow
import javax.inject.Inject

object TransactionStatusActor {
  final case class GetTransactionStatus(sessionId: String)
  final case class Foo(msg: String)

  def props(transactionDAO: EBTransactionDAO): Props = Props(
    new TransactionStatusActor(transactionDAO)
  )
}

class TransactionStatusActor @Inject()(
    transactionDAO: EBTransactionDAO
) extends Actor
    with WSConvertFlow {
  import TransactionStatusActor._

  override def convert(wsRequest: WSRequest): Any = wsRequest match {
    case WSRequest(action) if action == "blast" => Foo(action)
    case WSRequest(action: String) =>
      UnrecognizedMsgException(s"not recognized $action")
  }

  def receive: Receive = receiveFlow orElse {
    case Foo(msg) => out ! s"YEA, you are so $msg"
    case _        => out ! "not something I understand"
  }
}
