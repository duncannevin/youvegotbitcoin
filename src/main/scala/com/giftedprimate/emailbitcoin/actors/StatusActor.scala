package com.giftedprimate.emailbitcoin.actors

import akka.actor.Props
import com.giftedprimate.emailbitcoin.bitcoin.BitcoinClient
import com.giftedprimate.emailbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO,
  SessionDAO
}
import com.giftedprimate.emailbitcoin.entities._
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
            recipientWalletDAO: RecipientWalletDAO,
            sessionDAO: SessionDAO): Props = Props(
    new StatusActor(session,
                    transactionDAO,
                    bitcoinClient,
                    recipientWalletDAO,
                    sessionDAO)
  )
}

class StatusActor @Inject()(
    session: Session,
    transactionDAO: EBTransactionDAO,
    bitcoinClient: BitcoinClient,
    recipientWalletDAO: RecipientWalletDAO,
    sessionDAO: SessionDAO
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
        currentStatus <- sessionDAO
          .find(session.sessionId)
          .map(_.map(_.status).getOrElse(session.status))
        transactions <- transactionDAO.findAll(session.publicKey)
        blocks = transactions.map { transaction =>
          val block = bitcoinClient.getRawTransaction(transaction.transactionId)
          block
            .getOrElse(EBBlock(transaction))
            .copy(time = transaction.createdAt)
        }
      } yield {
        out ! TransactionStatus(session.sessionId, currentStatus, blocks).asJson
          .toString()
      }
    case _ => out ! "not something I understand"
  }
}
