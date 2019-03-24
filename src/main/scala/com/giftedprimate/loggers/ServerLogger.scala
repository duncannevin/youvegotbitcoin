package com.giftedprimate.loggers
import com.giftedprimate.configuration.ServerConfig
import com.google.inject.Inject

class ServerLogger @Inject()(config: ServerConfig) extends Logger {
  def successfulStart: Unit =
    logger.info(s"Emailbitcoin listening on port: ${config.port}")
  def failedStart(msg: String): Unit =
    logger.warn(s"Emailbitcoin failed to start: $msg")
}
