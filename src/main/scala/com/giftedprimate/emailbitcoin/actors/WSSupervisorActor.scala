package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.Message
import com.giftedprimate.emailbitcoin.actors.WSSupervisorActor.{
  CreateFlow,
  GetFlow
}
import com.giftedprimate.emailbitcoin.daos.EBTransactionDAO
import com.google.inject.Inject
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.giftedprimate.emailbitcoin.entities.GetActorFlow

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

object WSSupervisorActor {
  final case class GetFlow(actorName: String)
  final case class CreateFlow(actorRef: ActorRef)

  def props(actorSystem: ActorSystem, transactionDAO: EBTransactionDAO): Props =
    Props(
      new WSSupervisorActor(actorSystem, transactionDAO)
    )
}

class WSSupervisorActor @Inject()(actorSystem: ActorSystem,
                                  transactionDAO: EBTransactionDAO)
    extends Actor {
  implicit val timeout: Timeout = Timeout(FiniteDuration(3, "seconds"))

  override def receive: Receive = receive()

  def receive(s: ActorRef = ActorRef.noSender): Receive = {
    case GetFlow(actorName) if actorName == "transaction-status" =>
      context.become(receive(sender))
      self ! CreateFlow(
        actorSystem.actorOf(TransactionStatusActor.props(transactionDAO)))
    case CreateFlow(actorRef) =>
      for {
        flow <- (actorRef ? GetActorFlow)
          .mapTo[Flow[Message, Message, _]]
      } yield s ! flow
  }
}
