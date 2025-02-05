package com.impaktek.impakprint.parsers.text

import android.util.Log
import com.impaktek.impakprint.utils.ImpakPrinter
import com.impaktek.impakprint.exceptions.ImpakEncodingException
import com.impaktek.impakprint.utils.ImpakPrinterCommands
import java.io.UnsupportedEncodingException

class PrinterTextParserString(
    printerTextParserColumn: PrinterTextParserColumn,
    private val text: String,
    private val textSize: ByteArray,
    private val textColor: ByteArray,
    private val textReverseColor: ByteArray,
    private val textBold: ByteArray,
    private val textUnderline: ByteArray,
    private val textDoubleStrike: ByteArray
) : IPrinterTextParserElement {

    private val printer: ImpakPrinter = printerTextParserColumn.textParserLine.textParser.printer

    override fun length(): Int {
        val charsetEncoding = printer.encoding

        val coefficient = when {
            textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_DOUBLE_WIDTH) ||
                    textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_BIG) -> 2

            textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_BIG_2) -> 3
            textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_BIG_3) -> 4
            textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_BIG_4) -> 5
            textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_BIG_5) -> 6
            textSize.contentEquals(ImpakPrinterCommands.TEXT_SIZE_BIG_6) -> 7
            else -> 1
        }

        return try {
            charsetEncoding?.let {
                text.toByteArray(charset(it.charsetName)).size * coefficient
            } ?: (text.length * coefficient)
        } catch (e: UnsupportedEncodingException) {
            throw ImpakEncodingException(e.message ?: "Encoding error")
        }
    }

    /**
     * Print text
     *
     * @param printerSocket Instance of ImpakPrinterCommands
     * @return this Fluent method
     */
    override fun print(printerSocket: ImpakPrinterCommands): PrinterTextParserString {
        printerSocket.printText(text, textSize, textColor, textReverseColor, textBold, textUnderline, textDoubleStrike)
        return this
    }
}
