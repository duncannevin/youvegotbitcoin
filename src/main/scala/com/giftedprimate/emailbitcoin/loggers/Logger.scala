package com.giftedprimate.emailbitcoin.loggers
import org.apache.logging.log4j.scala.Logging

trait Logger extends Logging {
  def unrecognizedMessageSentToActor(): Unit =
    logger.info("unrecognized message sent to actor")
}
