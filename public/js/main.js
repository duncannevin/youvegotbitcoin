function convertUrlToWS (url) {
  return url.replace(/(http)(s)?\:\/\//, 'ws$2://')
}

function POST (location, reqData, cb) {
  axios.post(location, reqData)
    .then(cb)
    .catch((err) => {
      console.error(err.message)
    })
}

