package com.giftedprimate.loggers

class DAOLogger(collectionName: String) extends Logger {
  def indexCreated(key: String): Unit =
    logger.info(s"[$collectionName] Created index on key: $key.")
  def indexFailure(key: String): Unit =
    logger.info(s"[$collectionName] Failed to create index on key: $key.")
  override def info(msg: String): Unit = ???
  override def warn(msg: String): Unit = ???
  override def debug(msg: String): Unit = ???
}
