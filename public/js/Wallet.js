const Mnemonic = require('bitcore-mnemonic')
const { PublicKey, Networks, Address } = require('bitcore-lib')

class Wallet {
  code = new Mnemonic(Mnemonic.Words.ENGLISH)
  seed = this.code.toString()
  privateKey = this.code.toHDPrivateKey('', Networks.testnet).privateKey
  publicKey = new PublicKey(this.privateKey)
  address = this.publicKey.toAddress().toString()
}
