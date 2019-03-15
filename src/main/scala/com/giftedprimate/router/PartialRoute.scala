package com.giftedprimate.router

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives

trait PartialRoute extends Directives {
  def router: Route
}
