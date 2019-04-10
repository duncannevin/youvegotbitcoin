package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, ActorRef, Props}
import com.giftedprimate.emailbitcoin.daos.EBTransactionDAO
import com.giftedprimate.emailbitcoin.entities.GetActorFlow
import com.giftedprimate.emailbitcoin.websocket.WSFlow
import javax.inject.Inject

object TransactionStatusActor {
  final case class GetTransactionStatus(sessionId: String)

  def props(transactionDAO: EBTransactionDAO): Props = Props(
    new TransactionStatusActor(transactionDAO)
  )
}

class TransactionStatusActor @Inject()(
    transactionDAO: EBTransactionDAO
) extends Actor {
  private val flow = new WSFlow(self, context)
  private val out: ActorRef = flow.out

  override def receive: Receive = {
    case GetActorFlow => sender ! flow.flow
    case s: String    => out ! s"I heard you, $s"
    case _            => out ! "not something I understand"
  }
}
