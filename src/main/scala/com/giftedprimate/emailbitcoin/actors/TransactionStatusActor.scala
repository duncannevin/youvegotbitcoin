package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.bitcoin.BitcoinClient
import com.giftedprimate.emailbitcoin.daos.EBTransactionDAO
import com.giftedprimate.emailbitcoin.entities.{
  Session,
  TransactionStatus,
  UnrecognizedMsgException,
  WSRequest
}
import com.giftedprimate.emailbitcoin.websocket.WSConvertFlow
import io.circe.syntax._
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object TransactionStatusActor {
  final case class GetTransactionStatus(sessionId: String)
  final case class StatusCheck()

  def props(session: Session,
            transactionDAO: EBTransactionDAO,
            bitcoinClient: BitcoinClient): Props = Props(
    new TransactionStatusActor(session, transactionDAO, bitcoinClient)
  )
}

class TransactionStatusActor @Inject()(
    session: Session,
    transactionDAO: EBTransactionDAO,
    bitcoinClient: BitcoinClient
) extends WSConvertFlow {
  import TransactionStatusActor._
  import io.circe.generic.auto._

  override def convert(wsRequest: WSRequest): Any = wsRequest match {
    case WSRequest(action) if action == "check-status" => StatusCheck()
    case WSRequest(action: String) =>
      UnrecognizedMsgException(s"not recognized $action")
  }

  override def websocketReceive: Receive = {
    case StatusCheck() =>
      for {
        transactions <- transactionDAO.findAll(session.publicKey)
        blocks = transactions.flatMap { transaction =>
          bitcoinClient.getRawTransaction(transaction.transactionId)
        }
      } yield {
        out ! TransactionStatus(session.sessionId, transactions, blocks).asJson
          .toString()
      }
    case _ => out ! "not something I understand"
  }
}
