package com.impaktek.impakprint.parsers.text

import com.impaktek.impakprint.utils.ImpakPrinter
import com.impaktek.impakprint.exceptions.ImpakBarcodeException
import com.impaktek.impakprint.exceptions.ImpakParserException
import com.impaktek.impakprint.utils.ImpakPrinterCommands

class PrinterTextParserQRCode(
    printerTextParserColumn: PrinterTextParserColumn, textAlign: String?,
    qrCodeAttributes: MutableMap<String, String>, data: String
) : PrinterTextParserImg(
    printerTextParserColumn,
    textAlign!!,
    initConstructor(printerTextParserColumn, qrCodeAttributes, data)
) {
    companion object {
        @Throws(ImpakParserException::class, ImpakBarcodeException::class)
        private fun initConstructor(
            printerTextParserColumn: PrinterTextParserColumn,
            qrCodeAttributes: MutableMap<String, String>,
            data: String
        ): ByteArray {
            var tempData = data
            val printer: ImpakPrinter =
                printerTextParserColumn.textParserLine.textParser.printer
            tempData = tempData.trim { it <= ' ' }

            var size: Int = printer.mmToPx(20f)

            if (qrCodeAttributes.containsKey(PrinterTextParser.ATTR_QRCODE_SIZE)) {
                val qrCodeAttribute = qrCodeAttributes[PrinterTextParser.ATTR_QRCODE_SIZE]
                    ?: throw ImpakParserException("Invalid QR code attribute : " + PrinterTextParser.ATTR_QRCODE_SIZE)
                try {
                    size = printer.mmToPx(qrCodeAttribute.toFloat())
                } catch (nfe: NumberFormatException) {
                    throw ImpakParserException(("Invalid QR code " + PrinterTextParser.ATTR_QRCODE_SIZE) + " value")
                }
            }

            return ImpakPrinterCommands.qRCodeDataToBytes(tempData, size)
        }
    }
}
