package com.impaktek.impakprint.barcode

import com.impaktek.impakprint.exceptions.ImpakBarcodeException
import com.impaktek.impakprint.ImpakPrinterSize

abstract class Barcode internal constructor(
    printerSize: ImpakPrinterSize,
    var barcodeType: Int,
    var code: String,
    widthMM: Float,
    private var heightMM: Float,
    var textPosition: Int
) {
    val height: Int by lazy {
        printerSize.mmToPx(heightMM)
    }

    val colWidth: Int by lazy {
        var tempWidth = widthMM

        if (tempWidth == 0f) {
            tempWidth = printerSize.printerWidthMM * 0.7f
        }

        val wantedPxWidth: Int =
            if (tempWidth > printerSize.printerWidthMM) printerSize.printerWidthPx else printerSize.mmToPx(
                tempWidth
            )
        var colWidth = Math.round(wantedPxWidth.toDouble() / colsCount.toDouble()).toInt()

        if ((colWidth * this.colsCount) > printerSize.printerWidthPx) {
            --colWidth
        }

        if (colWidth == 0) {
            throw ImpakBarcodeException("Barcode is too long for the paper size.")
        }

        colWidth
    }



    abstract val codeLength: Int

    abstract val colsCount: Int
}