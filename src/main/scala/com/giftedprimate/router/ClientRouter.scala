package com.giftedprimate.router
import akka.http.scaladsl.server.Route
import com.giftedprimate.validators.ClientDirectives
import com.giftedprimate.views.Index

class ClientRouter extends Index with PartialRoute with ClientDirectives {
  override def router: Route = {
    pathEndOrSingleSlash {
      get {
        toHtml(main("Hello, Me!"))
      }
    } ~ pathPrefix("css") {
      getFromDirectory("public/css")
    } ~ pathPrefix("bower_components") {
      getFromDirectory("public/bower_components")
    } ~ pathPrefix("js") {
      getFromDirectory("public/js")
    }
  }
}
