package com.giftedprimate.emailbitcoin.validators
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, StandardRoute}

trait ClientDirectives extends Directives {
  def toHtml(page: String): StandardRoute = {
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, page))
  }
}
