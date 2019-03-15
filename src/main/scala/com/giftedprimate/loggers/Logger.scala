package com.giftedprimate.loggers
import org.apache.logging.log4j.scala.Logging

trait Logger extends Logging {
  def info(msg: String): Unit
  def warn(msg: String): Unit
  def debug(msg: String): Unit
}
