package com.giftedprimate.emailbitcoin.validators
import akka.http.scaladsl.server.{Directive1, Directives}
import com.giftedprimate.emailbitcoin.messages.ApiError

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait WalletDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def handle[T](f: Future[T])(e: Throwable => ApiError): Directive1[T] =
    onComplete(f) flatMap {
      case Success(t) => provide(t)
      case Failure(error) =>
        val apiError = e(error)
        complete(apiError.statusCode, apiError.message)
    }
}
