package com.impaktek.impakprint.barcode

import com.impaktek.impakprint.utils.ImpakPrinterCommands
import com.impaktek.impakprint.ImpakPrinterSize

class BarcodeUPCA(
    printerSize: ImpakPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) :
    BarcodeNumber(
        printerSize, ImpakPrinterCommands.BARCODE_TYPE_UPCA,
        code, widthMM, heightMM, textPosition
    ) {
    override val codeLength: Int
        get() = 12
}