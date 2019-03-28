package com.giftedprimate.views

import scalatags.Text.TypedTag
import scalatags.Text.all._

trait Views {
  private def getContent(pageName: String): TypedTag[String] = pageName match {
    case "main" => main()
    case _      => p("Page not found...")
  }

  def index(page: String, scripts: List[String] = List.empty[String]): String =
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
          script(
            src := "/bower_components/bitcore-lib/bitcore-lib.min.js"
          ),
          script(
            src := "/bower_components/bitcore-mnemonic/bitcore-mnemonic.js"
          ),
          for (scriptLocation <- scripts) yield script(s"/js/$scriptLocation"),
          script(src := "/js/index.js"),
        ),
        body(
          div(
            `class` := "container-fluid",
            div(
              `id` := "top-section",
              `class` := "row",
              h1("Email Bitcoin"),
              p("The name says it all!")
            ),
            getContent(page)
          )
        )
      )

  private def main() =
    div(
      `class` := "row",
      div(
        `class` := "col-6",
        form(
          div(
            `class` := "form-group",
            label(
              `for` := "sender-email-field",
              "Your Email:"
            ),
            input(
              `type` := "email",
              `class` := "form-control",
              `id` := "sender-email-field",
              attr("aria-describedby") := "emailHelp",
              `placeholder` := "Your email address"
            ),
            label(
              `for` := "recipient-email-field",
              "Recipient Email:"
            ),
            input(
              `type` := "email",
              `class` := "form-control",
              `id` := "recipient-email-field",
              attr("aria-describedby") := "emailHelp",
              `placeholder` := "Recipient email address"
            ),
            label(
              `for` := "message-field",
              "Message:"
            ),
            textarea(
              `class` := "form-control",
              `id` := "message-field",
              `rows` := "3"
            ),
            button(
              `type` := "submit",
              `class` := "btn btn-primary",
              "Get Address"
            )
          )
        )
      )
    )
}
