package com.impaktek.impakprint.barcode

import com.impaktek.impakprint.utils.ImpakPrinterCommands
import com.impaktek.impakprint.ImpakPrinterSize


class Barcode39(
    printerSize: ImpakPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) :
    Barcode(
        printerSize, ImpakPrinterCommands.BARCODE_TYPE_39,
        code, widthMM, heightMM, textPosition
    ) {
    override val codeLength: Int
        get() = code.length

    override val colsCount: Int
        get() = (this.codeLength + 4) * 16
}