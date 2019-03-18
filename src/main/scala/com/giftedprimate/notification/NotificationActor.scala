package com.giftedprimate.notification
import akka.actor.{Actor, Props}

object NotificationActor {
  def props: Props = Props[NotificationActor]
}

class NotificationActor extends Actor {
  override def receive: Receive = ???
}
