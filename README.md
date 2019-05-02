# Email Btc

Send Bitcoin via email securely to anyone, whether they have a wallet or not. If the recipient has a wallet already 
they can easily send the funds to themselves otherwise we will securely create a wallet in there browser, send the 
Bitcoin to the wallet then display the 12 word seed for them to write down and use to backup the wallet into there
own wallet. 

Aside from the server acting as an escrow, the server will never know the secret key that is displayed to the recipient. 

This service will be free to use. The main objective is to make it easy to send Bitcoin to people who do not have a 
wallet.

#### Development

---
This project is currently still under development. If you want to contribute feel free! Here's how to run the project.

##### Dependencies
- [Java -v0.8](https://java.com/en/download/) 
- [Scala -v 2.12.2](https://www.scala-lang.org/download/)
- [Bitcoin Full Node(testnet)](https://andrewgriffithsonline.com/blog/180330-how-to-setup-a-lightning-node/) 

##### Config
Any of the configs located in the `emailbitcoin` section of `resources/application.conf` can also be placed in your
environment. Use a `_` to denote each level e.g. `emailbitcoin_location_url=http://some.domain`.
