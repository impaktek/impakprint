package com.impaktek.impakprint.parsers.text

import com.impaktek.impakprint.utils.ImpakPrinterCommands

class PrinterTextParserColumn(
    val textParserLine: PrinterTextParserLine,
    textColumn: String
) {

    var elements: Array<IPrinterTextParserElement> = arrayOf()
    private var tempTextColumn = textColumn

    init {
        val textParser = textParserLine.textParser
        var textAlign = PrinterTextParser.TAGS_ALIGN_LEFT
        val textUnderlineStartColumn = textParser.lastTextUnderline
        val textDoubleStrikeStartColumn = textParser.lastTextDoubleStrike
        val textColorStartColumn = textParser.lastTextColor
        val textReverseColorStartColumn = textParser.lastTextReverseColor

        if (textColumn.length > 2) {
            when (textColumn.substring(0, 3).uppercase()) {
                "[${PrinterTextParser.TAGS_ALIGN_LEFT}]",
                "[${PrinterTextParser.TAGS_ALIGN_CENTER}]",
                "[${PrinterTextParser.TAGS_ALIGN_RIGHT}]" -> {
                    textAlign = textColumn.substring(1, 2).uppercase()
                    tempTextColumn = textColumn.substring(3)
                }
            }
        }

        val trimmedTextColumn = tempTextColumn.trim()
        var isImgOrBarcodeLine = false

        if (this.textParserLine.nbrColumns == 1 && trimmedTextColumn.startsWith("<")) {
            // =================================================================
            // Image or Barcode Lines
            val openTagIndex = trimmedTextColumn.indexOf("<")
            val openTagEndIndex = trimmedTextColumn.indexOf(">", openTagIndex + 1) + 1
            if (openTagIndex < openTagEndIndex) {
                val textParserTag = PrinterTextParserTag(trimmedTextColumn.substring(openTagIndex, openTagEndIndex))

                when (textParserTag.tagName) {
                    PrinterTextParser.TAGS_IMAGE, PrinterTextParser.TAGS_BARCODE, PrinterTextParser.TAGS_QRCODE -> {
                        val closeTag = "</${textParserTag.tagName}>"
                        val closeTagPosition = trimmedTextColumn.length - closeTag.length

                        if (trimmedTextColumn.substring(closeTagPosition) == closeTag) {
                            when (textParserTag.tagName) {
                                PrinterTextParser.TAGS_IMAGE -> appendImage(textAlign, trimmedTextColumn.substring(openTagEndIndex, closeTagPosition))
                                PrinterTextParser.TAGS_BARCODE -> appendBarcode(textAlign, textParserTag.attributes, trimmedTextColumn.substring(openTagEndIndex, closeTagPosition))
                                PrinterTextParser.TAGS_QRCODE -> appendQRCode(textAlign, textParserTag.attributes, trimmedTextColumn.substring(openTagEndIndex, closeTagPosition))
                            }
                            isImgOrBarcodeLine = true
                        }
                    }
                }
            }
        }

        if (!isImgOrBarcodeLine) {
            var offset = 0
            while (true) {
                var openTagIndex = tempTextColumn.indexOf("<", offset)
                var closeTagIndex = -1

                if (openTagIndex != -1) {
                    closeTagIndex = tempTextColumn.indexOf(">", openTagIndex)
                } else {
                    openTagIndex = tempTextColumn.length
                }

                appendString(tempTextColumn.substring(offset, openTagIndex))

                if (closeTagIndex == -1) {
                    break
                }

                val closeTagIndexEnd = closeTagIndex + 1
                val textParserTag = PrinterTextParserTag(tempTextColumn.substring(openTagIndex, closeTagIndexEnd))

                if (PrinterTextParser.isTagTextFormat(textParserTag.tagName)) {
                    if (textParserTag.isCloseTag) {
                        when (textParserTag.tagName) {
                            PrinterTextParser.TAGS_FORMAT_TEXT_BOLD -> textParser.dropTextBold()
                            PrinterTextParser.TAGS_FORMAT_TEXT_UNDERLINE -> {
                                textParser.dropLastTextUnderline()
                                textParser.dropLastTextDoubleStrike()
                            }
                            PrinterTextParser.TAGS_FORMAT_TEXT_FONT -> {
                                textParser.dropLastTextSize()
                                textParser.dropLastTextColor()
                                textParser.dropLastTextReverseColor()
                            }
                        }
                    } else {
                        when (textParserTag.tagName) {
                            PrinterTextParser.TAGS_FORMAT_TEXT_BOLD -> textParser.addTextBold(ImpakPrinterCommands.TEXT_WEIGHT_BOLD)
                            PrinterTextParser.TAGS_FORMAT_TEXT_UNDERLINE -> {
                                if (textParserTag.hasAttribute(PrinterTextParser.ATTR_FORMAT_TEXT_UNDERLINE_TYPE)) {
                                    when (textParserTag.getAttribute(PrinterTextParser.ATTR_FORMAT_TEXT_UNDERLINE_TYPE)) {
                                        PrinterTextParser.ATTR_FORMAT_TEXT_UNDERLINE_TYPE_NORMAL -> {
                                            textParser.addTextUnderline(ImpakPrinterCommands.TEXT_UNDERLINE_LARGE)
                                            textParser.addTextDoubleStrike(textParser.lastTextDoubleStrike)
                                        }
                                        PrinterTextParser.ATTR_FORMAT_TEXT_UNDERLINE_TYPE_DOUBLE -> {
                                            textParser.addTextUnderline(textParser.lastTextUnderline)
                                            textParser.addTextDoubleStrike(ImpakPrinterCommands.TEXT_DOUBLE_STRIKE_ON)
                                        }
                                    }
                                } else {
                                    textParser.addTextUnderline(ImpakPrinterCommands.TEXT_UNDERLINE_LARGE)
                                    textParser.addTextDoubleStrike(textParser.lastTextDoubleStrike)
                                }
                            }
                            PrinterTextParser.TAGS_FORMAT_TEXT_FONT -> {
                                if (textParserTag.hasAttribute(PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE)) {
                                    when (textParserTag.getAttribute(PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE)) {
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_NORMAL -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_NORMAL)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_TALL -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_DOUBLE_HEIGHT)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_WIDE -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_DOUBLE_WIDTH)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_BIG -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_BIG)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_BIG_2 -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_BIG_2)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_BIG_3 -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_BIG_3)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_BIG_4 -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_BIG_4)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_BIG_5 -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_BIG_5)
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_SIZE_BIG_6 -> textParser.addTextSize(ImpakPrinterCommands.TEXT_SIZE_BIG_6)
                                    }
                                } else {
                                    textParser.addTextSize(textParser.lastTextSize)
                                }

                                if (textParserTag.hasAttribute(PrinterTextParser.ATTR_FORMAT_TEXT_FONT_COLOR)) {
                                    when (textParserTag.getAttribute(PrinterTextParser.ATTR_FORMAT_TEXT_FONT_COLOR)) {
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_COLOR_BLACK,
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_COLOR_BG_BLACK -> {
                                            textParser.addTextColor(ImpakPrinterCommands.TEXT_COLOR_BLACK)
                                            textParser.addTextReverseColor(ImpakPrinterCommands.TEXT_COLOR_REVERSE_OFF)
                                        }
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_COLOR_RED,
                                        PrinterTextParser.ATTR_FORMAT_TEXT_FONT_COLOR_BG_RED -> {
                                            textParser.addTextColor(ImpakPrinterCommands.TEXT_COLOR_RED)
                                            textParser.addTextReverseColor(ImpakPrinterCommands.TEXT_COLOR_REVERSE_OFF)
                                        }
                                    }
                                } else {
                                    textParser.addTextColor(textParser.lastTextColor)
                                    textParser.addTextReverseColor(textParser.lastTextReverseColor)
                                }
                            }
                        }
                    }
                    offset = closeTagIndexEnd
                } else {
                    appendString("<")
                    offset = openTagIndex + 1
                }
            }

            // =================================================================
            // Define the number of spaces required for different alignments

            val nbrCharColumn = this.textParserLine.nbrCharColumn
            var nbrCharForgot = this.textParserLine.nbrCharForgot
            var nbrCharColumnExceeded = this.textParserLine.nbrCharColumnExceeded
            var nbrCharTextWithoutTag = 0
            var leftSpace = 0
            var rightSpace = 0

            elements.forEach { textParserElement ->
                nbrCharTextWithoutTag += textParserElement.length()
            }
            when (textAlign) {
                PrinterTextParser.TAGS_ALIGN_LEFT -> rightSpace = nbrCharColumn - nbrCharTextWithoutTag
                PrinterTextParser.TAGS_ALIGN_CENTER -> {
                    leftSpace = ((nbrCharColumn - nbrCharTextWithoutTag) / 2)
                    rightSpace = nbrCharColumn - nbrCharTextWithoutTag - leftSpace
                }
                PrinterTextParser.TAGS_ALIGN_RIGHT -> leftSpace = nbrCharColumn - nbrCharTextWithoutTag
            }

            if (nbrCharForgot > 0) {
                nbrCharForgot -= 1
                rightSpace++
            }

            if (nbrCharColumnExceeded < 0) {
                leftSpace += nbrCharColumnExceeded
                nbrCharColumnExceeded = 0
                if (leftSpace < 1) {
                    rightSpace += leftSpace - 1
                    leftSpace = 1
                }
            }

            if (leftSpace < 0) {
                nbrCharColumnExceeded += leftSpace
                leftSpace = 0
            }

            if (rightSpace < 0) {
                nbrCharColumnExceeded += rightSpace
                rightSpace = 0
            }

            if (leftSpace > 0) {
                prependString(generateSpace(leftSpace), ImpakPrinterCommands.TEXT_SIZE_NORMAL, textColorStartColumn, textReverseColorStartColumn, ImpakPrinterCommands.TEXT_WEIGHT_NORMAL, textUnderlineStartColumn, textDoubleStrikeStartColumn)
            }
            if (rightSpace > 0) {
                appendString(generateSpace(rightSpace), ImpakPrinterCommands.TEXT_SIZE_NORMAL, textParser.lastTextColor, textParser.lastTextReverseColor, ImpakPrinterCommands.TEXT_WEIGHT_NORMAL, textParser.lastTextUnderline, textParser.lastTextDoubleStrike)
            }

            this.textParserLine.apply {
                this.nbrCharForgot = nbrCharForgot
                this.nbrCharColumnExceeded = nbrCharColumnExceeded
            }
        }
    }

    companion object {
        private fun generateSpace(nbrSpace: Int): String {
            return " ".repeat(nbrSpace)
        }
    }

    // Methods for adding different elements like strings, images, barcodes, and QR codes

    private fun prependString(text: String): PrinterTextParserColumn {
        val textParser = textParserLine.textParser
        return prependString(text, textParser.lastTextSize, textParser.lastTextColor, textParser.lastTextReverseColor, textParser.lastTextBold, textParser.lastTextUnderline, textParser.lastTextDoubleStrike)
    }

    private fun prependString(text: String, textSize: ByteArray, textColor: ByteArray, textReverseColor: ByteArray, textBold: ByteArray, textUnderline: ByteArray, textDoubleStrike: ByteArray): PrinterTextParserColumn {
        return prependElement(PrinterTextParserString(this, text, textSize, textColor, textReverseColor, textBold, textUnderline, textDoubleStrike))
    }

    private fun appendString(text: String): PrinterTextParserColumn {
        val textParser = textParserLine.textParser
        return appendString(text, textParser.lastTextSize, textParser.lastTextColor, textParser.lastTextReverseColor, textParser.lastTextBold, textParser.lastTextUnderline, textParser.lastTextDoubleStrike)
    }

    private fun appendString(text: String, textSize: ByteArray, textColor: ByteArray, textReverseColor: ByteArray, textBold: ByteArray, textUnderline: ByteArray, textDoubleStrike: ByteArray): PrinterTextParserColumn {
        return appendElement(PrinterTextParserString(this, text, textSize, textColor, textReverseColor, textBold, textUnderline, textDoubleStrike))
    }

    private fun prependImage(textAlign: String, hexString: String): PrinterTextParserColumn {
        return prependElement(PrinterTextParserImg(this, textAlign, hexString))
    }

    private fun appendImage(textAlign: String, hexString: String): PrinterTextParserColumn {
        return appendElement(PrinterTextParserImg(this, textAlign, hexString))
    }

    private fun prependBarcode(textAlign: String, barcodeAttributes: MutableMap<String, String>, code: String): PrinterTextParserColumn {
        return prependElement(PrinterTextParserBarcode(this, textAlign, barcodeAttributes, code))
    }

    private fun appendBarcode(textAlign: String, barcodeAttributes: MutableMap<String, String>, code: String): PrinterTextParserColumn {
        return appendElement(PrinterTextParserBarcode(this, textAlign, barcodeAttributes, code))
    }

    private fun prependQRCode(textAlign: String, qrCodeAttributes: MutableMap<String, String>, code: String): PrinterTextParserColumn {
        return prependElement(PrinterTextParserQRCode(this, textAlign, qrCodeAttributes, code))
    }

    private fun appendQRCode(textAlign: String, qrCodeAttributes: MutableMap<String, String>, code: String): PrinterTextParserColumn {
        return appendElement(PrinterTextParserQRCode(this, textAlign, qrCodeAttributes, code))
    }

    private fun prependElement(element: IPrinterTextParserElement): PrinterTextParserColumn {
        elements = arrayOf(element) + elements
        return this
    }

    private fun appendElement(element: IPrinterTextParserElement): PrinterTextParserColumn {
        elements += element
        return this
    }
}

