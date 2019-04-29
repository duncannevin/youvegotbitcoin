function convertUrlToWS (connectionPath) {
  const origin = window.location.origin
  const params = window.location.search
  return origin.replace(/(http)(s)?\:\/\//, 'ws$2://') + '/ws/' + connectionPath + params
}

function parseDate (rawDate) {
  const dateNTime = rawDate.split('T')
  dateNTime[0] = dateNTime[0].split('-').reverse().join('/')
  return dateNTime
}

function POST (location, reqData, cb) {
  axios.post(location, reqData)
    .then(cb)
    .catch((err) => {
      console.error(err.message)
    })
}

