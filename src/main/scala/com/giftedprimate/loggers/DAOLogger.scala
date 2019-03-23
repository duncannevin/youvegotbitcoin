package com.giftedprimate.loggers

class DAOLogger extends Logger {
  def indexCreated(collectionName: String, key: String): Unit =
    logger.info(s"[$collectionName] Created index on key: $key.")
  def indexFailure(collectionName: String, key: String): Unit =
    logger.info(s"[$collectionName] Failed to create index on key: $key.")
  override def info(msg: String): Unit = ???
  override def warn(msg: String): Unit = ???
  override def debug(msg: String): Unit = ???
}
