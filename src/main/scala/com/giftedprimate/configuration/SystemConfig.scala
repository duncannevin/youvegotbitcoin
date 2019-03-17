package com.giftedprimate.configuration
import akka.util.Timeout

import scala.concurrent.duration.FiniteDuration

trait SystemConfig {
  implicit lazy val timeout = Timeout(FiniteDuration(5, "seconds"))
}
