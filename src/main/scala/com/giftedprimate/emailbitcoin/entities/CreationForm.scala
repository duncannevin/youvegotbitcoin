package com.giftedprimate.emailbitcoin.entities
import io.circe.Encoder
import io.circe.generic.semiauto._

object CreationForm {
//  implicit val encoder: Encoder[CreationForm] = deriveEncoder[CreationForm]
}
case class CreationForm(
    recipientEmail: String,
    senderEmail: String,
    message: String
)
