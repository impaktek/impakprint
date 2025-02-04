package com.impaktek.impakprint.parsers.text

import com.impaktek.impakprint.barcode.Barcode
import com.impaktek.impakprint.barcode.Barcode128
import com.impaktek.impakprint.barcode.Barcode39
import com.impaktek.impakprint.barcode.BarcodeEAN13
import com.impaktek.impakprint.barcode.BarcodeEAN8
import com.impaktek.impakprint.barcode.BarcodeUPCA
import com.impaktek.impakprint.barcode.BarcodeUPCE
import com.impaktek.impakprint.exceptions.ImpakParserException
import com.impaktek.impakprint.utils.ImpakPrinterCommands

class PrinterTextParserBarcode(
    printerTextParserColumn: PrinterTextParserColumn,
    textAlign: String,
    barcodeAttributes: MutableMap<String, String>,
    code: String
) : IPrinterTextParserElement {

    private var barcode: Barcode? = null
    private var length: Int = 0
    private var align: ByteArray = ImpakPrinterCommands.TEXT_ALIGN_LEFT

    init {
        val printer = printerTextParserColumn.textParserLine.textParser.printer
        val tempCode = code.trim()

        align = when (textAlign) {
            PrinterTextParser.TAGS_ALIGN_CENTER -> ImpakPrinterCommands.TEXT_ALIGN_CENTER
            PrinterTextParser.TAGS_ALIGN_RIGHT -> ImpakPrinterCommands.TEXT_ALIGN_RIGHT
            else -> ImpakPrinterCommands.TEXT_ALIGN_LEFT
        }

        this.length = printer.printerNbrCharactersPerLine

        var height = 10f
        barcodeAttributes[PrinterTextParser.ATTR_BARCODE_HEIGHT]?.let {
            try {
                height = it.toFloat()
            } catch (nfe: NumberFormatException) {
                throw ImpakParserException("Invalid barcode ${PrinterTextParser.ATTR_BARCODE_HEIGHT} value")
            }
        }

        var width = 0f
        barcodeAttributes[PrinterTextParser.ATTR_BARCODE_WIDTH]?.let {
            try {
                width = it.toFloat()
            } catch (nfe: NumberFormatException) {
                throw ImpakParserException("Invalid barcode ${PrinterTextParser.ATTR_BARCODE_WIDTH} value")
            }
        }

        var textPosition = ImpakPrinterCommands.BARCODE_TEXT_POSITION_BELOW
        barcodeAttributes[PrinterTextParser.ATTR_BARCODE_TEXT_POSITION]?.let {
            textPosition = when (it) {
                PrinterTextParser.ATTR_BARCODE_TEXT_POSITION_NONE -> ImpakPrinterCommands.BARCODE_TEXT_POSITION_NONE
                PrinterTextParser.ATTR_BARCODE_TEXT_POSITION_ABOVE -> ImpakPrinterCommands.BARCODE_TEXT_POSITION_ABOVE
                else -> textPosition
            }
        }

        var barcodeType = PrinterTextParser.ATTR_BARCODE_TYPE_EAN13
        barcodeAttributes[PrinterTextParser.ATTR_BARCODE_TYPE]?.let {
            barcodeType = it
        }

        this.barcode = when (barcodeType) {
            PrinterTextParser.ATTR_BARCODE_TYPE_EAN8 -> BarcodeEAN8(printer, tempCode, width, height, textPosition)
            PrinterTextParser.ATTR_BARCODE_TYPE_EAN13 -> BarcodeEAN13(printer, tempCode, width, height, textPosition)
            PrinterTextParser.ATTR_BARCODE_TYPE_UPCA -> BarcodeUPCA(printer, tempCode, width, height, textPosition)
            PrinterTextParser.ATTR_BARCODE_TYPE_UPCE -> BarcodeUPCE(printer, tempCode, width, height, textPosition)
            PrinterTextParser.ATTR_BARCODE_TYPE_128 -> Barcode128(printer, tempCode, width, height, textPosition)
            PrinterTextParser.ATTR_BARCODE_TYPE_39 -> Barcode39(printer, tempCode, width, height, textPosition)
            else -> throw ImpakParserException("Invalid barcode attribute: $barcodeType")
        }
    }

    /**
     * Get the barcode width in char length.
     *
     * @return int
     */
    override fun length(): Int {
        return this.length
    }


    /**
     * Print barcode
     *
     * @param printerSocket Instance of ImpakPrinterCommands
     * @return this Fluent method
     */
    override fun print(printerSocket: ImpakPrinterCommands): PrinterTextParserBarcode {
        printerSocket
            .setAlign(this.align)
            .printBarcode(this.barcode!!)
        return this
    }
}
