const $createTransactionForm = $('#create-transaction-form')
const createWalletLocation = '/api/createwallet'
const displayAddressLocation = '/displayaddress'

function displayAddress ({data: {publicKeyAddress, recipientEmail}}) {
  const $addressDisplay = $('#address-display')
  $createTransactionForm.hide()
  $addressDisplay.show()
  $('#address-display-input').val(function (_, v) {
    return v + publicKeyAddress
  })
  $addressDisplay.prepend(`<p>Pay any amount to this address, as soon as we see the transaction ${recipientEmail} will
    be notified and the Bitcoin will be transferred to them securely.</p>
  `)
}

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
  POST(values, displayAddress)
}

$(function () {
  $createTransactionForm.on('submit', handleTransactionSubmit)
})