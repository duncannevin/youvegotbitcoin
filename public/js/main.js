const $createTransactionForm = $('#create-transaction-form')
const createWalletLocation = '/api/createwallet'
const displayAddressLocation = '/displayaddress'

function POST (reqData, cb) {
  axios.post(createWalletLocation, reqData)
    .then(cb)
    .catch((err) => {
      console.error(err.message)
    })
}

function handleTransactionSubmit (evt) {
  evt.preventDefault()
  const $inputs = $(this).find('input, textarea')
  const values = {}
  $inputs.each(function (_, input) {
    values[input.name] = input.value
  })
  POST(values, ({data}) => {
    window.location = data
  })
}

$(function () {
  $createTransactionForm.on('submit', handleTransactionSubmit)
})