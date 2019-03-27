package com.giftedprimate.views

import scalatags.Text.all._

trait Index {
  def main(page: String): String =
    "<!DOCTYPE html>" +
      html(
        lang := "en",
        head(
          meta(name := "viewport",
               content := "width=device-width, initial-scale=1.0"),
          meta(httpEquiv := "Content-Type",
               content := "text/html; charset=UTF-8"),
          scalatags.Text.tags2.title("Email Bitcoin"),
          meta(name := "description", content := "The name says it all"),
          link(rel := "stylesheet", href := "/css/style.css"),
          link(
            rel := "stylesheet",
            href := "/bower_components/bootstrap/dist/css/bootstrap.min.css"),
          script(src := "/bower_components/jquery/dist/jquery.min.js"),
          script(src := "/bower_components/bootstrap/dist/js/bootstrap.min.js"),
          script(src := "/js/index.js")
        ),
        body(
          div(`class` := "container", page)
        )
      )
}
