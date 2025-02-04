package com.impaktek.impakprint.parsers.text

import com.impaktek.impakprint.utils.ImpakPrinter
import com.impaktek.impakprint.exceptions.ImpakBarcodeException
import com.impaktek.impakprint.exceptions.ImpakEncodingException
import com.impaktek.impakprint.exceptions.ImpakParserException
import com.impaktek.impakprint.utils.ImpakPrinterCommands

class PrinterTextParser(var printer: ImpakPrinter) {
    private var textSize = arrayOf(ImpakPrinterCommands.TEXT_SIZE_NORMAL)
    private var textColor = arrayOf(ImpakPrinterCommands.TEXT_COLOR_BLACK)
    private var textReverseColor = arrayOf(ImpakPrinterCommands.TEXT_COLOR_REVERSE_OFF)
    private var textBold = arrayOf(ImpakPrinterCommands.TEXT_WEIGHT_NORMAL)
    private var textUnderline = arrayOf(ImpakPrinterCommands.TEXT_UNDERLINE_OFF)
    private var textDoubleStrike = arrayOf(ImpakPrinterCommands.TEXT_DOUBLE_STRIKE_OFF)
    private var text = ""


    fun setFormattedText(text: String): PrinterTextParser {
        this.text = text
        return this
    }

    val lastTextSize: ByteArray
        get() = textSize[textSize.size - 1]

    fun addTextSize(newTextSize: ByteArray): PrinterTextParser {
        this.textSize = arrayBytePush(this.textSize, newTextSize)
        return this
    }

    fun dropLastTextSize(): PrinterTextParser {
        if (textSize.size > 1) {
            this.textSize = arrayByteDropLast(this.textSize)
        }
        return this
    }

    val lastTextColor: ByteArray
        get() = textColor[textColor.size - 1]

    fun addTextColor(newTextColor: ByteArray): PrinterTextParser {
        this.textColor = arrayBytePush(this.textColor, newTextColor)
        return this
    }

    fun dropLastTextColor(): PrinterTextParser {
        if (textColor.size > 1) {
            this.textColor = arrayByteDropLast(this.textColor)
        }
        return this
    }

    val lastTextReverseColor: ByteArray
        get() = textReverseColor[textReverseColor.size - 1]

    fun addTextReverseColor(newTextReverseColor: ByteArray): PrinterTextParser {
        this.textReverseColor = arrayBytePush(this.textReverseColor, newTextReverseColor)
        return this
    }

    fun dropLastTextReverseColor(): PrinterTextParser {
        if (textReverseColor.size > 1) {
            this.textReverseColor = arrayByteDropLast(this.textReverseColor)
        }
        return this
    }

    val lastTextBold: ByteArray
        get() = textBold[textBold.size - 1]

    fun addTextBold(newTextBold: ByteArray): PrinterTextParser {
        this.textBold = arrayBytePush(this.textBold, newTextBold)
        return this
    }

    fun dropTextBold(): PrinterTextParser {
        if (textBold.size > 1) {
            this.textBold = arrayByteDropLast(this.textBold)
        }
        return this
    }

    val lastTextUnderline: ByteArray
        get() = textUnderline[textUnderline.size - 1]

    fun addTextUnderline(newTextUnderline: ByteArray): PrinterTextParser {
        this.textUnderline = arrayBytePush(this.textUnderline, newTextUnderline)
        return this
    }

    fun dropLastTextUnderline(): PrinterTextParser {
        if (textUnderline.size > 1) {
            this.textUnderline = arrayByteDropLast(this.textUnderline)
        }
        return this
    }

    val lastTextDoubleStrike: ByteArray
        get() = textDoubleStrike[textDoubleStrike.size - 1]

    fun addTextDoubleStrike(newTextDoubleStrike: ByteArray): PrinterTextParser {
        this.textDoubleStrike = arrayBytePush(this.textDoubleStrike, newTextDoubleStrike)
        return this
    }

    fun dropLastTextDoubleStrike(): PrinterTextParser {
        if (textDoubleStrike.size > 1) {
            this.textDoubleStrike = arrayByteDropLast(this.textDoubleStrike)
        }
        return this
    }

    @Throws(ImpakParserException::class, ImpakBarcodeException::class, ImpakEncodingException::class)
    fun parse(): Array<PrinterTextParserLine> {
        val stringLines = text.split("\n", "\r\n")
        return Array(stringLines.size) { i -> PrinterTextParserLine(this, stringLines[i]) }
    }


    companion object {
        const val TAGS_ALIGN_LEFT: String = "L"
        const val TAGS_ALIGN_CENTER: String = "C"
        const val TAGS_ALIGN_RIGHT: String = "R"
        val TAGS_ALIGN: Array<String> = arrayOf(TAGS_ALIGN_LEFT, TAGS_ALIGN_CENTER, TAGS_ALIGN_RIGHT)

        const val TAGS_IMAGE: String = "img"
        const val TAGS_BARCODE: String = "barcode"
        const val TAGS_QRCODE: String = "qrcode"

        const val ATTR_BARCODE_WIDTH: String = "width"
        const val ATTR_BARCODE_HEIGHT: String = "height"
        const val ATTR_BARCODE_TYPE: String = "type"
        const val ATTR_BARCODE_TYPE_EAN8: String = "ean8"
        const val ATTR_BARCODE_TYPE_EAN13: String = "ean13"
        const val ATTR_BARCODE_TYPE_UPCA: String = "upca"
        const val ATTR_BARCODE_TYPE_UPCE: String = "upce"
        const val ATTR_BARCODE_TYPE_128: String = "128"
        const val ATTR_BARCODE_TYPE_39: String = "39"
        const val ATTR_BARCODE_TEXT_POSITION: String = "text"
        const val ATTR_BARCODE_TEXT_POSITION_NONE: String = "none"
        const val ATTR_BARCODE_TEXT_POSITION_ABOVE: String = "above"
        const val ATTR_BARCODE_TEXT_POSITION_BELOW: String = "below"

        const val TAGS_FORMAT_TEXT_FONT: String = "font"
        const val TAGS_FORMAT_TEXT_BOLD: String = "b"
        const val TAGS_FORMAT_TEXT_UNDERLINE: String = "u"
        val TAGS_FORMAT_TEXT: Array<String> =
            arrayOf(TAGS_FORMAT_TEXT_FONT, TAGS_FORMAT_TEXT_BOLD, TAGS_FORMAT_TEXT_UNDERLINE)

        const val ATTR_FORMAT_TEXT_UNDERLINE_TYPE: String = "type"
        const val ATTR_FORMAT_TEXT_UNDERLINE_TYPE_NORMAL: String = "normal"
        const val ATTR_FORMAT_TEXT_UNDERLINE_TYPE_DOUBLE: String = "double"

        const val ATTR_FORMAT_TEXT_FONT_SIZE: String = "size"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG: String = "big"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG_2: String = "big-2"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG_3: String = "big-3"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG_4: String = "big-4"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG_5: String = "big-5"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG_6: String = "big-6"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_TALL: String = "tall"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_WIDE: String = "wide"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_NORMAL: String = "normal"

        const val ATTR_FORMAT_TEXT_FONT_COLOR: String = "color"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_BLACK: String = "black"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_BG_BLACK: String = "bg-black"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_RED: String = "red"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_BG_RED: String = "bg-red"

        const val ATTR_QRCODE_SIZE: String = "size"

        val regexAlignTags: String by lazy {
            TAGS_ALIGN.joinToString("|") { "\\[$it\\]" }
        }

        fun isTagTextFormat(tagName: String): Boolean {
            var tempTagName = tagName
            if (tempTagName.substring(0, 1) == "/") {
                tempTagName = tempTagName.substring(1)
            }

            for (tag in TAGS_FORMAT_TEXT) {
                if (tag == tempTagName) {
                    return true
                }
            }
            return false
        }

        fun arrayByteDropLast(arr: Array<ByteArray>): Array<ByteArray> {
            if (arr.isEmpty()) return arr

            return arr.copyOfRange(0, arr.size - 1)
        }

        fun arrayBytePush(arr: Array<ByteArray>, add: ByteArray): Array<ByteArray> {
            return arrayOf(*arr, add)
        }

    }
}