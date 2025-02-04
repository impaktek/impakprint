package com.impaktek.impakprint.barcode

import com.impaktek.impakprint.exceptions.ImpakBarcodeException
import com.impaktek.impakprint.utils.ImpakPrinterCommands
import com.impaktek.impakprint.ImpakPrinterSize

class BarcodeUPCE(
    printerSize: ImpakPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) :
    Barcode(
        printerSize, ImpakPrinterCommands.BARCODE_TYPE_UPCE,
        code, widthMM, heightMM, textPosition
    ) {
    init {
        this.checkCode()
    }

    override val codeLength: Int
        get() = 6

    override val colsCount: Int
        get() = this.codeLength * 7 + 16

    @Throws(ImpakBarcodeException::class)
    private fun checkCode() {
        val codeLength = this.codeLength

        if (code.length < codeLength) {
            throw ImpakBarcodeException("Code is too short for the barcode type.")
        }

        try {
            this.code = code.substring(0, codeLength)
            for (i in 0 until codeLength) {
                code.substring(i, i + 1).toInt(10)
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            throw ImpakBarcodeException("Invalid barcode number")
        }
    }
}