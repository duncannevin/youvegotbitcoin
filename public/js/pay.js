const $payCopy = $('#pay-copy')

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

function handleBlocks (blocks) {}

function handleTransactions (transactions) {
  if (transactions.length) {
    window.location = window.location.href.replace('pay', 'sender/status')
  }
}

connectStatus(handleBlocks, handleTransactions)
