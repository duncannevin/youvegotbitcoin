package com.duncannevin.youvegotbitcoin.router

import com.google.inject.Inject

class Routes @Inject()(
    homeRouter: HomeRouter,
    transactionRouter: TransactionRouter,
    WSRouter: WSRouter
) {
  def routes: Seq[PartialRoute] = Seq(WSRouter, homeRouter, transactionRouter)
}
