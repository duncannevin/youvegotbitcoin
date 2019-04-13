package com.giftedprimate.emailbitcoin.entities
import io.circe.Encoder
import io.circe.generic.semiauto._
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}

object EBTransaction {
//  implicit val encoder: Encoder[EBTransaction] = deriveEncoder[EBTransaction]
  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(
        Macros.createCodecProvider[EBTransaction](),
        Macros.createCodecProvider[CreationForm]()
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class EBTransaction(
    createdAt: String,
    publicKey: String,
    transactionId: String,
    creationForm: CreationForm,
    value: Long
)
