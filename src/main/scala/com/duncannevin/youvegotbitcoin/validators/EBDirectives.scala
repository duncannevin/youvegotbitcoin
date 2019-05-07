package com.duncannevin.youvegotbitcoin.validators

import akka.http.scaladsl.server.{Directive1, Directives}
import com.duncannevin.youvegotbitcoin.entities._
import com.duncannevin.youvegotbitcoin.loggers.DirectiveLogger

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

  def handleSessionWallet[T](
      f: Future[T],
      isHtml: Boolean = false): Directive1[SessionWallet] = {
    onComplete(f) flatMap {
      case Success(t) =>
        t match {
          case (Some(session: Session), None) =>
            logger.sessionHasNoWallet(session)
            if (isHtml) {
              toHtml(html.serverError.render())
            } else {
              complete(ApiError.noWalletError.statusCode,
                       ApiError.noWalletError.message)
            }
          case (Some(session: Session),
                Some(recipientWallet: RecipientWallet)) =>
            provide(SessionWallet(session, recipientWallet))
          case _ =>
            logger.unknownReason
            if (isHtml) {
              toHtml(html.badRequest())
            } else {
              complete(ApiError.noSessionError.statusCode,
                       ApiError.noSessionError.message)
            }
        }
      case Failure(error) =>
        complete(ApiError.generic.statusCode, ApiError.generic.message)
    }
  }

  def handleStatus(sessionWallet: SessionWallet,
                   desiredStatus: String,
                   isHtml: Boolean = false): Directive1[SessionWallet] =
    sessionWallet.session.status match {
      case status if desiredStatus.contains(status) => provide(sessionWallet)
      case status if !desiredStatus.contains(status) =>
        toHtml(html.badRequest.render())
      case _ =>
        if (isHtml) {
          toHtml(html.serverError.render())
        } else {
          complete(ApiError.noSessionError.statusCode,
                   ApiError.noSessionError.message)
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
