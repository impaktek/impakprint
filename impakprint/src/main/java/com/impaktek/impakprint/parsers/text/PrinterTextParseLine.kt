package com.impaktek.impakprint.parsers.text

import java.util.regex.Pattern

class PrinterTextParserLine(
    val textParser: PrinterTextParser,
    textLine: String
)  {

    val nbrColumns: Int
    var nbrCharColumn: Int
    var nbrCharForgot: Int
    var nbrCharColumnExceeded: Int = 0
    val columns: Array<PrinterTextParserColumn>

    init {
        val nbrCharactersPerLine = textParser.printer.printerNbrCharactersPerLine

        val pattern = Pattern.compile(PrinterTextParser.regexAlignTags)
        val matcher = pattern.matcher(textLine)

        val columnsList = mutableListOf<String>()
        var lastPosition = 0

        while (matcher.find()) {
            val startPosition = matcher.start()
            if (startPosition > 0) {
                columnsList.add(textLine.substring(lastPosition, startPosition))
            }
            lastPosition = startPosition
        }
        columnsList.add(textLine.substring(lastPosition))

        nbrColumns = columnsList.size
        nbrCharColumn = (nbrCharactersPerLine.toFloat() / nbrColumns.toFloat()).toInt()
        nbrCharForgot = nbrCharactersPerLine - (nbrCharColumn * nbrColumns)

        columns = Array(nbrColumns) { i -> PrinterTextParserColumn(this, columnsList[i]) }
    }
}
