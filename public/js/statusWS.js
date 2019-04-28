let ws, sessionId, connectionAttempts

function getStatus () {
  ws.send(JSON.stringify({'action': 'check-status'}))
  setTimeout(getStatus, 5000)
}

function connectStatus (handleBlocks, handleStatus) {
  ws = new WebSocket(convertUrlToWS('status'))
  connectionAttempts++

  ws.onopen = function () {
    console.log('transtatus socket open')
    getStatus()
  }

  ws.onerror = function () {
    console.error('transtatus failed to open')
  }

  ws.onclose = function () {
    if (connectionAttempts < 3) {
      setTimeout(() => {
        connectStatus(handleBlocks)
      }, 5000)
    }
  }

  ws.onmessage = function (status) {
    const data = JSON.parse(status.data)
    Object.keys(data).forEach((key) => {
      switch (key) {
        case 'blocks': handleBlocks(data[key]); break;
        case 'sessionId': sessionId = data.sessionId; break;
        case 'status': handleStatus(data.status); break;
        default: console.log('NO RECOGNIZED KEYS: ' + JSON.stringify(data))
      }
    })
  }
}

