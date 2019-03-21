package com.giftedprimate.loggers
import com.giftedprimate.configuration.ServerConfig

class ServerLog(config: ServerConfig) extends Logger {
  def successfulStart: Unit =
    logger.info(s"Emailbitcoin listening on port: ${config.port}")
  def failedStart(msg: String): Unit =
    logger.warn(s"Emailbitcoin failed to start: $msg")
  override def info(msg: String): Unit = logger.info(msg)
  override def warn(msg: String): Unit = logger.info(msg)
  override def debug(msg: String): Unit = logger.info(msg)
}
