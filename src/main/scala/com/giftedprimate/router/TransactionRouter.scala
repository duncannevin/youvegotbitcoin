package com.giftedprimate.router
import akka.http.scaladsl.server.Route

class TransactionRouter extends PartialRoute {
  override def router: Route = pathPrefix("transaction") {
    pathEndOrSingleSlash {
      get {
        complete("Hello, World!")
      }
    }
  }
}
