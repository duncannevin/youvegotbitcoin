package com.duncannevin.youvegotbitcoin.entities

object CreationForm {
//  implicit val encoder: Encoder[CreationForm] = deriveEncoder[CreationForm]
}
case class CreationForm(
    recipientEmail: String,
    senderEmail: String,
    message: String
)
