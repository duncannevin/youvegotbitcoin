package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.bitcoin.BitcoinClient
import com.giftedprimate.emailbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO
}
import com.giftedprimate.emailbitcoin.entities.{
  EBBlock,
  EBTransaction,
  Session,
  TransactionStatus,
  UnrecognizedMsgException,
  WSRequest
}
import com.giftedprimate.emailbitcoin.websocket.WSConvertFlow
import io.circe.syntax._
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object StatusActor {
  final case class GetTransactionStatus(sessionId: String)
  final case class StatusCheck()

  def props(session: Session,
            transactionDAO: EBTransactionDAO,
            bitcoinClient: BitcoinClient,
            recipientWalletDAO: RecipientWalletDAO): Props = Props(
    new StatusActor(session, transactionDAO, bitcoinClient, recipientWalletDAO)
  )
}

class StatusActor @Inject()(
    session: Session,
    transactionDAO: EBTransactionDAO,
    bitcoinClient: BitcoinClient,
    recipientWalletDAO: RecipientWalletDAO
) extends WSConvertFlow {
  import StatusActor._
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
        blocks = transactions.map { transaction =>
          val block = bitcoinClient.getRawTransaction(transaction.transactionId)
          block
            .getOrElse(EBBlock(transaction))
            .copy(time = transaction.createdAt)
        }
      } yield {
        out ! TransactionStatus(session.sessionId, session.status, blocks).asJson
          .toString()
      }
    case _ => out ! "not something I understand"
  }
}
