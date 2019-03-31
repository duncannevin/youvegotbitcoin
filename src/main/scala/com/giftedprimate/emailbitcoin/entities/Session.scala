package com.giftedprimate.emailbitcoin.entities
import java.util.UUID

import akka.http.scaladsl.model.DateTime
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}

object Session {
  def apply(recipientWallet: RecipientWallet): Session = {
    val date = DateTime.now.toString
    new Session(UUID.randomUUID().toString,
                date,
                date,
                recipientWallet.publicKey,
                "pending")
  }

  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(
        Macros.createCodecProvider[Session]()
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class Session(sessionId: String,
                   createdAt: String,
                   updatedAt: String,
                   publicKey: String,
                   status: String)
