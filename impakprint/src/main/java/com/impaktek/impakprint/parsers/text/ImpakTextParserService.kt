package com.impaktek.impakprint.parsers.text

import com.impaktek.impakprint.exceptions.ImpakConnectionException
import com.impaktek.impakprint.exceptions.ImpakEncodingException
import com.impaktek.impakprint.utils.ImpakPrinterCommands

interface IPrinterTextParserElement {
    @Throws(ImpakEncodingException::class)
    fun length(): Int

    @Throws(ImpakEncodingException::class, ImpakConnectionException::class)
    fun print(printerSocket: ImpakPrinterCommands): IPrinterTextParserElement?
}