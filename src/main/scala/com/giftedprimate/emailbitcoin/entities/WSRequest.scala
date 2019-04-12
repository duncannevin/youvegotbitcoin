package com.giftedprimate.emailbitcoin.entities
import io.circe._, io.circe.generic.semiauto._

object WSRequest {
  implicit val fooDecoder: Decoder[WSRequest] = deriveDecoder[WSRequest]
  implicit val fooEncoder: Encoder[WSRequest] = deriveEncoder[WSRequest]
}

case class WSRequest(action: String)
