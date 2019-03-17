package com.giftedprimate.transaction

case class RecipientWallet(
    createForm: CreationForm,
    seed: Seed,
    privateKey: String,
    publicKey: String,
    publicKeyAddress: String
)
