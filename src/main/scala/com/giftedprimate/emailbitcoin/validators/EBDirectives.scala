package com.giftedprimate.emailbitcoin.validators

import akka.http.scaladsl.server.{Directive1, Directives, Route}
import com.giftedprimate.emailbitcoin.entities.{
  ActorFailed,
  RecipientWallet,
  Session
}
import com.giftedprimate.emailbitcoin.loggers.DirectiveLogger
import com.giftedprimate.emailbitcoin.messages.ApiError
import play.twirl.api.HtmlFormat

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait EBDirectives extends Directives with ClientDirectives {
  private val logger = new DirectiveLogger

  def handle[T](f: Future[T])(e: Throwable => ApiError): Directive1[T] =
    onComplete(f) flatMap {
      case Success(t) => provide(t)
      case Failure(error) =>
        val apiError = e(error)
        complete(apiError.statusCode, apiError.message)
    }

  def handleSessionWallet[T](f: Future[T])(desiredStatus: String)(
      handler: (Session, RecipientWallet) => HtmlFormat.Appendable): Route =
    handle(f)(_ => ApiError.generic) { t =>
      t.asInstanceOf[(Option[Session], Option[RecipientWallet])] match {
        case (Some(session), None) =>
          logger.sessionHasNoWallet(session)
          toHtml(html.serverError.render())
        case (Some(session), Some(wallet)) if session.status == desiredStatus =>
          toHtml(handler(session, wallet))
        case (Some(session), Some(wallet)) if session.status != desiredStatus =>
          toHtml(html.badRequest.render())
        case _ => toHtml(html.serverError())
      }
    }

  def handleActor[T](f: Future[T])(e: Throwable => ApiError): Directive1[T] = {
    onSuccess(f) flatMap {
      case actorFailed: ActorFailed =>
        val apiError = e(actorFailed)
        complete(apiError.statusCode, apiError.message)
      case t => provide(t)
    }
  }
}
