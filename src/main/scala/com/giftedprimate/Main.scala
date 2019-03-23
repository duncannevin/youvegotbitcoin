package com.giftedprimate

import com.google.inject.Guice

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val injector = Guice.createInjector(new Module)
  injector.getInstance(classOf[EmailBitcoin])
}
