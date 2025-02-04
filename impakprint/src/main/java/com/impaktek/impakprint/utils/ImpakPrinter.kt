package com.impaktek.impakprint.utils

import com.impaktek.impakprint.ImpakPrinterSize
import com.impaktek.impakprint.connection.DeviceConnection
import com.impaktek.impakprint.exceptions.ImpakBarcodeException
import com.impaktek.impakprint.exceptions.ImpakConnectionException
import com.impaktek.impakprint.exceptions.ImpakEncodingException
import com.impaktek.impakprint.exceptions.ImpakParserException
import com.impaktek.impakprint.parsers.text.IPrinterTextParserElement
import com.impaktek.impakprint.parsers.text.PrinterTextParser
import com.impaktek.impakprint.parsers.text.PrinterTextParserLine
import com.impaktek.impakprint.parsers.text.PrinterTextParserString

class ImpakPrinter(
    printer: ImpakPrinterCommands?,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int
) : ImpakPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {
    private var printer: ImpakPrinterCommands? = null

    /**
     * Create new instance of ImpakPrinter.
     *
     * @param printerConnection           Instance of class which implement DeviceConnection
     * @param printerDpi                  DPI of the connected printer
     * @param printerWidthMM              Printing width in millimeters
     * @param printerNbrCharactersPerLine The maximum number of characters that can be printed on a line.
     */
    constructor(
        printerConnection: DeviceConnection?,
        printerDpi: Int,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int
    ) : this(
        if (printerConnection != null) ImpakPrinterCommands(printerConnection, null) else null,
        printerDpi,
        printerWidthMM,
        printerNbrCharactersPerLine
    )

    /**
     * Create new instance of ImpakPrinter.
     *
     * @param printerConnection           Instance of class which implement DeviceConnection
     * @param printerDpi                  DPI of the connected printer
     * @param printerWidthMM              Printing width in millimeters
     * @param printerNbrCharactersPerLine The maximum number of characters that can be printed on a line.
     * @param charsetEncoding             Set the charset encoding.
     */
    constructor(
        printerConnection: DeviceConnection?,
        printerDpi: Int,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int,
        charsetEncoding: ImpakCharsetEncoding?
    ) : this(
        if (printerConnection != null) ImpakPrinterCommands(
            printerConnection,
            charsetEncoding
        ) else null, printerDpi, printerWidthMM, printerNbrCharactersPerLine
    )


    init {
        if (printer != null) {
            this.printer = printer.connect()
        }
    }

    /**
     * Close the connection with the printer.
     *
     * @return Fluent interface
     */
    fun disconnectPrinter(): ImpakPrinter {
        printer?.disconnect()
        printer = null
        return this
    }

    /**
     * Active "ESC *" command for image printing.
     *
     * @param enable true to use "ESC *", false to use "GS v 0"
     * @return Fluent interface
     */
    fun useEscAsteriskCommand(enable: Boolean): ImpakPrinter {
        printer?.useEscAsteriskCommand(enable)
        return this
    }


    /**
     * Print a formatted text. Read the README.md for more information about text formatting options.
     *
     * @param text        Formatted text to be printed.
     * @param mmFeedPaper millimeter distance feed paper at the end.
     * @return Fluent interface
     */
    @JvmOverloads
    @Throws(
        ImpakConnectionException::class,
        ImpakParserException::class,
        ImpakEncodingException::class,
        ImpakBarcodeException::class
    )
    fun printFormattedText(text: String, mmFeedPaper: Float = 20f): ImpakPrinter {
        return this.printFormattedText(text, this.mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        ImpakConnectionException::class,
        ImpakParserException::class,
        ImpakEncodingException::class,
        ImpakBarcodeException::class
    )
    fun printFormattedText(text: String, dotsFeedPaper: Int): ImpakPrinter {
        printer?.apply {
            if (printerNbrCharactersPerLine == 0) {
                return this@ImpakPrinter
            }

            val textParser = PrinterTextParser(this@ImpakPrinter)
            val linesParsed: Array<PrinterTextParserLine> = textParser.setFormattedText(text).parse()

            reset()

            for (line in linesParsed) {
                val columns = line.columns

                var lastElement: IPrinterTextParserElement? = null
                for (column in columns) {
                    val elements: Array<IPrinterTextParserElement> = column.elements
                    for (element in elements) {
                        element.print(this)
                        lastElement = element
                    }
                }

                if (lastElement is PrinterTextParserString) {
                    newLine()
                }
            }

            feedPaper(dotsFeedPaper)
        }

        return this
    }

    /**
     * Print a formatted text and cut the paper. Read the README.md for more information about text formatting options.
     *
     * @param text        Formatted text to be printed.
     * @param mmFeedPaper millimeter distance feed paper at the end.
     * @return Fluent interface
     */
    @JvmOverloads
    @Throws(
        ImpakConnectionException::class,
        ImpakParserException::class,
        ImpakEncodingException::class,
        ImpakBarcodeException::class
    )
    fun printFormattedTextAndCut(text: String, mmFeedPaper: Float = 20f): ImpakPrinter {
        return this.printFormattedTextAndCut(text, this.mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text and cut the paper. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        ImpakConnectionException::class,
        ImpakParserException::class,
        ImpakEncodingException::class,
        ImpakBarcodeException::class
    )
    fun printFormattedTextAndCut(text: String, dotsFeedPaper: Int): ImpakPrinter {
        printer?.apply {
            if (printerNbrCharactersPerLine == 0) {
                return this@ImpakPrinter
            }

            printFormattedText(text, dotsFeedPaper)
            cutPaper()
        }

        return this
    }

    /**
     * Print a formatted text, cut the paper and open the cash box. Read the README.md for more information about text formatting options.
     *
     * @param text        Formatted text to be printed.
     * @param mmFeedPaper millimeter distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        ImpakConnectionException::class,
        ImpakParserException::class,
        ImpakEncodingException::class,
        ImpakBarcodeException::class
    )
    fun printFormattedTextAndOpenCashBox(text: String, mmFeedPaper: Float): ImpakPrinter {
        return this.printFormattedTextAndOpenCashBox(text, this.mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text, cut the paper and open the cash box. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        ImpakConnectionException::class,
        ImpakParserException::class,
        ImpakEncodingException::class,
        ImpakBarcodeException::class
    )
    fun printFormattedTextAndOpenCashBox(text: String, dotsFeedPaper: Int): ImpakPrinter {
        if (this.printer == null || this.printerNbrCharactersPerLine == 0) {
            return this
        }

        this.printFormattedTextAndCut(text, dotsFeedPaper)
        printer?.openCashBox()
        return this
    }

    val encoding: ImpakCharsetEncoding?
        /**
         * @return Charset encoding
         */
        get() = printer?.getCharsetEncoding()


    /**
     * Print all characters of all charset encoding
     *
     * @return Fluent interface
     */
    fun printAllCharsetsEncodingCharacters(): ImpakPrinter {
        printer?.printAllCharsetsEncodingCharacters()
        return this
    }

    /**
     * Print all characters of selected charsets encoding
     *
     * @param charsetsId Array of charset id to print.
     * @return Fluent interface
     */
    fun printCharsetsEncodingCharacters(charsetsId: IntArray?): ImpakPrinter {
        printer?.printCharsetsEncodingCharacters(charsetsId ?: intArrayOf())
        return this
    }

    /**
     * Print all characters of a charset encoding
     *
     * @param charsetId Charset id to print.
     * @return Fluent interface
     */
    fun printCharsetEncodingCharacters(charsetId: Int): ImpakPrinter {
        printer?.printCharsetEncodingCharacters(charsetId)
        return this
    }
}