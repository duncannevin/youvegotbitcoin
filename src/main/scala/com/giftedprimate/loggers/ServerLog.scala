package com.giftedprimate.loggers
import com.giftedprimate.configuration.ConfigModule

class ServerLog extends Logger with ConfigModule {
  def successfulStart: Unit = logger.info(s"Emailbitcoin listening on port: $port")
  def failedStart(msg: String): Unit = logger.warn(s"Emailbitcoin failed to start: $msg")
  override def info(msg: String): Unit = logger.info(msg)
  override def warn(msg: String): Unit = logger.info(msg)
  override def debug(msg: String): Unit = logger.info(msg)
}
