let transactions

class Transaction {
  constructor({time, amount, confirmations, fee, txid}) {
    this.time = time
    this.amount = amount
    this.confirmations = confirmations
    this.fee = fee
    this.txid = txid
  }
}

class Transactions {
  constructor(blocks) {
    this.list = blocks.map(block => new Transaction(block))
    this._render()
    console.log(this.list)
  }

  $TransactionInfo = $('#Transaction-info')
  $infoTable = this.$TransactionInfo.find('table > tbody')

  _render () {
    this.list.forEach((transaction, ind) => {
      const rowId = 'Status-row-' + ind
      $('#' + rowId).remove()
      const dateNTime = parseDate(transaction.time)
      const $row = $('<tr id="' + rowId + '">')
      const $date = '<td>' + dateNTime[0] + '</td>'
      const $time = '<td>' + dateNTime[1] + '</td>'
      const $txId = '<td>' + transaction.txid + '</td>'
      const $conf = '<td>' + transaction.confirmations + '</td>'
      const $fee = '<td>' + transaction.fee + '</td>'
      const $amt = '<td>' + transaction.amount + '</td>'
      $row.append($date + $time + $txId + $conf + $fee + $amt)
      this.$infoTable.append($row)
    })
  }
}

function handleBlocks (blocks) {
  transactions = new Transactions(blocks)
}

function handleStatus (status) {
  console.log(status)
}

connectStatus(handleBlocks, handleStatus)
