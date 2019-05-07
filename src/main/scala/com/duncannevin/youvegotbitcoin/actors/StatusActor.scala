package com.duncannevin.youvegotbitcoin.actors

import akka.actor.Props
import com.duncannevin.youvegotbitcoin.bitcoin.BitcoinClient
import com.duncannevin.youvegotbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO,
  SessionDAO
}
import com.duncannevin.youvegotbitcoin.entities._
import com.duncannevin.youvegotbitcoin.websocket.WSConvertFlow
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
