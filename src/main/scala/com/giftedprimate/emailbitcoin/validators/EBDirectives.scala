package com.giftedprimate.emailbitcoin.validators

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, Directives, Route}
import com.giftedprimate.emailbitcoin.entities.{
  ActorFailed,
  ApiError,
  RecipientWallet,
  Session,
  SessionWallet
}
import com.giftedprimate.emailbitcoin.loggers.DirectiveLogger
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

  def handleWithGeneric[T](f: Future[T]): Directive1[T] =
    handle(f)(_ => ApiError.generic)

  def handleSessionWallet[T](f: Future[T]): Directive1[SessionWallet] =
    onComplete(f) flatMap {
      case Success(t) =>
        t match {
          case (Some(session: Session), None) =>
            logger.sessionHasNoWallet(session)
            complete(ApiError.noWalletError.statusCode,
                     ApiError.noWalletError.message)
          case (Some(session: Session),
                Some(recipientWallet: RecipientWallet)) =>
            provide(SessionWallet(session, recipientWallet))
          case _ =>
            logger.unknownReason
            complete(ApiError.noSessionError.statusCode,
                     ApiError.noSessionError.message)
        }
      case Failure(error) =>
        complete(ApiError.generic.statusCode, ApiError.generic.message)
    }

  def handleSessionWalletHtml[T](f: Future[T])(desiredStatus: String)(
      handler: (Session, RecipientWallet) => HtmlFormat.Appendable): Route =
    handle(f)(_ => ApiError.generic) { t =>
      t.asInstanceOf[(Option[Session], Option[RecipientWallet])] match {
        case (Some(session), None) =>
          logger.sessionHasNoWallet(session)
          toHtml(html.serverError.render())
        case (Some(session), Some(wallet)) if session.status == desiredStatus =>
          toHtml(handler(session, wallet))
        case (Some(session), Some(_)) if session.status != desiredStatus =>
          toHtml(html.badRequest.render())
        case _ =>
          logger.unknownReason
          toHtml(html.serverError())
      }
    }

  def handleActor[T](f: Future[T])(e: Throwable => ApiError): Directive1[T] =
    onSuccess(f) flatMap {
      case actorFailed: ActorFailed =>
        val apiError = e(actorFailed)
        complete(apiError.statusCode, apiError.message)
      case t => provide(t)
    }
}
