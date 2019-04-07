const $createTransactionForm = $('#create-transaction-form')
const createWalletLocation = '/api/createwallet'

function handleTransactionSubmit (evt) {
  evt.preventDefault()
  const $inputs = $(this).find('input, textarea')
  const values = {}
  $inputs.each(function (_, input) {
    values[input.name] = input.value
  })
  POST(createWalletLocation, values, ({data}) => {
    window.location = data
  })
}


$(function () {
  $createTransactionForm.on('submit', handleTransactionSubmit)
})
