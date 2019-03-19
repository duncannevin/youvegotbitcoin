package com.giftedprimate.transaction

import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.wallet.Wallet

object RecipientWallet {
  def apply(
      wallet: Wallet,
      createForm: CreationForm
  ): RecipientWallet = {
    val deterministic: DeterministicKey = wallet.getWatchingKey
    new RecipientWallet(
      createForm,
      seed = wallet.getKeyChainSeed.getMnemonicCode.toArray().mkString(" "),
      privateKey = deterministic.serializePrivB58(wallet.getParams),
      publicKey = deterministic.serializePubB58(wallet.getParams),
      publicKeyAddress = wallet.currentReceiveAddress().toString
    )
  }
}

case class RecipientWallet(
    createForm: CreationForm,
    seed: String,
    privateKey: String,
    publicKey: String,
    publicKeyAddress: String
)
