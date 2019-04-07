package com.giftedprimate.emailbitcoin.utils

import java.io.ByteArrayOutputStream
import java.util.Base64

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeUtil {
  def getQRCode(str: String, width: Int = 350, height: Int = 350): String = {
    val codeWriter = new QRCodeWriter()
    val bitMatrix = codeWriter.encode(str, BarcodeFormat.QR_CODE, width, height)
    val pngOutPut = new ByteArrayOutputStream()
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutPut)
    Base64.getEncoder.encodeToString(pngOutPut.toByteArray)
  }
}
