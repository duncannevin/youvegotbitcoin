package com.giftedprimate.transaction
import akka.actor.{Actor, ActorSystem, Props}
import com.giftedprimate.configuration.ConfigModule

object TransactionActor {
  def props(config: ConfigModule): Props = Props(
    new TransactionActor(config)
  )
}

class TransactionActor(config: ConfigModule) extends Actor {
  val transactionControl = new TransactionControl
  override def receive: Receive = {
    case str: String => sender ! s"Yo me, to you! $str"
  }
}
