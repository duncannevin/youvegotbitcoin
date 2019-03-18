package com.giftedprimate.transaction
import akka.actor.{Actor, ActorSystem, Props}
import com.giftedprimate.configuration.ConfigModule

object TransactionActor {
  def props(config: ConfigModule,
            transactionControl: TransactionControl): Props = Props(
    new TransactionActor(config, transactionControl)
  )
}

class TransactionActor(config: ConfigModule,
                       transactionControl: TransactionControl)
    extends Actor {
  override def receive: Receive = {
    case str: String => sender ! s"Yo me, to you! $str"
  }
}
