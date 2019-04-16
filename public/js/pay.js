const $payCopy = $('#pay-copy')
const ws = new WebSocket(convertUrlToWS(window.location.href.replace('paytransaction', 'ws/transtatus')))
let sessionId, statusTimeout

function getStatus () {
  if (statusTimeout) statusTimeout.clearTimeout()
  ws.send(JSON.stringify({'action': 'check-status'}))
  statusTimeout = setTimeout(getStatus, 5000)
}

function handleBlocks (blocks) {
  console.log(blocks)
}

function handleTransactions (transactions) {
  console.log(transactions)
}

ws.onopen = function () {
  console.log('transtatus socket open')
  getStatus()
}

ws.onerror = function () {
  console.error('transtatus failed to open')
}

ws.onmessage = function (status) {
  const data = JSON.parse(status.data)
  Object.keys(data).forEach((key) => {
    switch (key) {
      case 'blocks': handleBlocks(data[key]); break;
      case 'transactions': handleTransactions(data[key]); break;
      case 'sessionId': sessionId = data.sessionId; break;
      default: console.log('NO RECOGNIZED KEYS: ' + JSON.stringify(data))
    }
  })
}

function copyCode () {
  const $addressDisplay = $('#address-display-input')
  const $temp = $('<input>')
  $('body').append($temp)
  $temp.val($addressDisplay.val()).select()
  document.execCommand('copy')
  $temp.remove()
  $payCopy.html('Copied!')
  $payCopy.css({'background-color': 'green', color: 'white'})
  setTimeout(() => {
    $payCopy.attr('style', '')
    $payCopy.html('Copy')
  }, 5000)
}

$(function () {
  $payCopy.on('click', copyCode)
})
