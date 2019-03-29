package com.giftedprimate.emailbitcoin.entities
import akka.http.scaladsl.model.DateTime
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.wallet.Wallet
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}

object RecipientWallet {
  def apply(
      wallet: Wallet,
      createForm: CreationForm
  ): RecipientWallet = {
    val deterministic: DeterministicKey = wallet.getWatchingKey
    new RecipientWallet(
      createForm,
      createdAt = DateTime.now.toString(),
      seed = wallet.getKeyChainSeed.getMnemonicCode.toArray().mkString(" "),
      privateKey = deterministic.serializePrivB58(wallet.getParams),
      publicKey = deterministic.serializePubB58(wallet.getParams),
      publicKeyAddress = wallet.currentReceiveAddress().toString
    )
  }

  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(
        Macros.createCodecProvider[RecipientWallet](),
        Macros.createCodecProvider[CreationForm]()
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class RecipientWallet(
    createForm: CreationForm,
    createdAt: String,
    seed: String,
    privateKey: String,
    publicKey: String,
    publicKeyAddress: String
)
