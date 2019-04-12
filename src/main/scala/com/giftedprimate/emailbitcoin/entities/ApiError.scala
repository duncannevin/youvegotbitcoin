package com.giftedprimate.emailbitcoin.entities

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

final case class ApiError private (statusCode: StatusCode, message: String)

object ApiError {
  private def apply(statusCode: StatusCode, message: String): ApiError =
    new ApiError(statusCode, message)

  val generic: ApiError =
    new ApiError(StatusCodes.InternalServerError, "Unknown error.")
  def invalidEmail(email: String): ApiError =
    new ApiError(StatusCodes.BadRequest, s"Email is invalid: $email.")
  def noSessionError: ApiError =
    new ApiError(StatusCodes.BadRequest, "Session does not exist.")
  def noWalletError: ApiError =
    new ApiError(StatusCodes.BadRequest, "Wallet does not exist.")
}
