package com.giftedprimate.router
import akka.http.scaladsl.server.Route
import com.giftedprimate.validators.ClientDirectives
import com.giftedprimate.views.Views

class ClientRouter extends Views with PartialRoute with ClientDirectives {
  override def router: Route = {
    pathEndOrSingleSlash {
      get {
        toHtml(index("main"))
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
