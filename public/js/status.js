let ws, sessionId

function getStatus () {
  ws.send(JSON.stringify({'action': 'check-status'}))
  setTimeout(getStatus, 5000)
}

function connectStatus (handleBlocks, handleTransactions) {
  ws = new WebSocket(convertUrlToWS(window.location.href.replace('pay', 'ws/transtatus')))

  ws.onopen = function () {
    console.log('transtatus socket open')
    getStatus()
  }

  ws.onerror = function () {
    console.error('transtatus failed to open')
  }

  ws.onclose = function () {
    setTimeout(() => {
      connectStatus(handleBlocks, handleTransactions)
    }, 100)
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
}

