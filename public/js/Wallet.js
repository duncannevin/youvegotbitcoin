const Mnemonic = require('bitcore-mnemonic')
const { Networks, PrivateKey } = require('bitcore-lib')

class Wallet {
  constructor () {
    this.mnemonic = new Mnemonic(Mnemonic.Words.ENGLISH)
    this.xpriv = this.mnemonic.toHDPrivateKey("", Networks.testnet)
    this.address = this.xpriv.privateKey.toAddress().toString()
    console.log(this)
  }
}