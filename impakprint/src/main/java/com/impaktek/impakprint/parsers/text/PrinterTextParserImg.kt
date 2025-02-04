package com.impaktek.impakprint.parsers.text

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.impaktek.impakprint.utils.ImpakPrinterCommands
import com.impaktek.impakprint.ImpakPrinterSize
import kotlin.math.ceil

open class PrinterTextParserImg(
    private val printerTextParserColumn: PrinterTextParserColumn,
    private val textAlign: String,
    private var image: ByteArray
) : IPrinterTextParserElement {

    private var length: Int = 0

    init {

        val printer = printerTextParserColumn.textParserLine.textParser.printer
        var image = this.image
        val byteWidth = (image[4].toInt() and 0xFF) + (image[5].toInt() and 0xFF) * 256
        val width = byteWidth * 8
        val height = (image[6].toInt() and 0xFF) + (image[7].toInt() and 0xFF) * 256
        val nbrByteDiff = ((printer.printerWidthPx - width) / 8f).toInt()
        var nbrWhiteByteToInsert = 0

        when (textAlign) {
            PrinterTextParser.TAGS_ALIGN_CENTER -> nbrWhiteByteToInsert = (nbrByteDiff / 2f).toInt()
            PrinterTextParser.TAGS_ALIGN_RIGHT -> nbrWhiteByteToInsert = nbrByteDiff
        }

        if (nbrWhiteByteToInsert > 0) {
            val newByteWidth = byteWidth + nbrWhiteByteToInsert
            val newImage = ImpakPrinterCommands.initGSv0Command(newByteWidth, height)
            for (i in 0 until height) {
                System.arraycopy(image, byteWidth * i + 8, newImage, newByteWidth * i + nbrWhiteByteToInsert + 8, byteWidth)
            }
            image = newImage
        }

        length = (ceil((byteWidth * 8).toFloat() / printer.printerCharSizeWidthPx.toFloat())).toInt()
        this.image = image
    }

    companion object {

        /**
         * Convert Drawable instance to a hexadecimal string of the image data.
         *
         * @param printerSize A ImpakPrinterSize instance that will print the image.
         * @param drawable Drawable instance to be converted.
         * @return A hexadecimal string of the image data. Empty string if Drawable cannot be cast to BitmapDrawable.
         */
        @JvmStatic
        fun bitmapToHexadecimalString(printerSize: ImpakPrinterSize, drawable: Drawable): String {
            return if (drawable is BitmapDrawable) {
                bitmapToHexadecimalString(printerSize, drawable)
            } else {
                ""
            }
        }

        /**
         * Convert Drawable instance to a hexadecimal string of the image data.
         *
         * @param printerSize A ImpakPrinterSize instance that will print the image.
         * @param drawable Drawable instance to be converted.
         * @param gradient false : Black and white image, true : Grayscale image
         * @return A hexadecimal string of the image data. Empty string if Drawable cannot be cast to BitmapDrawable.
         */
        @JvmStatic
        fun bitmapToHexadecimalString(printerSize: ImpakPrinterSize, drawable: Drawable, gradient: Boolean): String {
            return if (drawable is BitmapDrawable) {
                bitmapToHexadecimalString(printerSize, drawable, gradient)
            } else {
                ""
            }
        }

        /**
         * Convert BitmapDrawable instance to a hexadecimal string of the image data.
         *
         * @param printerSize A ImpakPrinterSize instance that will print the image.
         * @param bitmapDrawable BitmapDrawable instance to be converted.
         * @return A hexadecimal string of the image data.
         */
        @JvmStatic
        fun bitmapToHexadecimalString(printerSize: ImpakPrinterSize, bitmapDrawable: BitmapDrawable): String {
            return bitmapToHexadecimalString(printerSize, bitmapDrawable.bitmap)
        }

        /**
         * Convert BitmapDrawable instance to a hexadecimal string of the image data.
         *
         * @param printerSize A ImpakPrinterSize instance that will print the image.
         * @param bitmapDrawable BitmapDrawable instance to be converted.
         * @param gradient false : Black and white image, true : Grayscale image
         * @return A hexadecimal string of the image data.
         */
        @JvmStatic
        fun bitmapToHexadecimalString(printerSize: ImpakPrinterSize, bitmapDrawable: BitmapDrawable, gradient: Boolean): String {
            return bitmapToHexadecimalString(printerSize, bitmapDrawable.bitmap, gradient)
        }

        /**
         * Convert Bitmap instance to a hexadecimal string of the image data.
         *
         * @param printerSize A ImpakPrinterSize instance that will print the image.
         * @param bitmap Bitmap instance to be converted.
         * @return A hexadecimal string of the image data.
         */
        @JvmStatic
        fun bitmapToHexadecimalString(printerSize: ImpakPrinterSize, bitmap: Bitmap): String {
            return bitmapToHexadecimalString(printerSize, bitmap, true)
        }

        /**
         * Convert Bitmap instance to a hexadecimal string of the image data.
         *
         * @param printerSize A ImpakPrinterSize instance that will print the image.
         * @param bitmap Bitmap instance to be converted.
         * @param gradient false : Black and white image, true : Grayscale image
         * @return A hexadecimal string of the image data.
         */
        @JvmStatic
        fun bitmapToHexadecimalString(printerSize: ImpakPrinterSize, bitmap: Bitmap, gradient: Boolean): String {
            return bytesToHexadecimalString(printerSize.bitmapToBytes(bitmap, gradient))
        }

        /**
         * Convert byte array to a hexadecimal string of the image data.
         *
         * @param bytes Bytes contain the image in ESC/POS command.
         * @return A hexadecimal string of the image data.
         */
        @JvmStatic
        fun bytesToHexadecimalString(bytes: ByteArray): String {
            val imageHexString = StringBuilder()
            for (aByte in bytes) {
                val hexString = Integer.toHexString(aByte.toInt() and 0xFF)
                if (hexString.length == 1) {
                    imageHexString.append("0")
                }
                imageHexString.append(hexString)
            }
            return imageHexString.toString()
        }

        /**
         * Convert hexadecimal string of the image data to bytes ESC/POS command.
         *
         * @param hexString Hexadecimal string of the image data.
         * @return Bytes contain the image in ESC/POS command.
         */
        @JvmStatic
        fun hexadecimalStringToBytes(hexString: String): ByteArray {
            val bytes = ByteArray(hexString.length / 2)
            for (i in bytes.indices) {
                val pos = i * 2
                bytes[i] = hexString.substring(pos, pos + 2).toInt(16).toByte()
            }
            return bytes
        }
    }

    // Primary constructor
    constructor(
        printerTextParserColumn: PrinterTextParserColumn,
        textAlign: String,
        hexadecimalString: String
    ) : this(printerTextParserColumn, textAlign, hexadecimalStringToBytes(hexadecimalString)) {
        // Custom constructor logic goes here
    }

    // Overloaded constructor

    /**
     * Get the image width in char length.
     *
     * @return int
     */
    override fun length(): Int {
        return length
    }

    /**
     * Print image
     *
     * @param printerSocket Instance of ImpakPrinterCommands
     * @return this Fluent method
     */
    override fun print(printerSocket: ImpakPrinterCommands): PrinterTextParserImg {
        printerSocket.printImage(image)
        return this
    }
}

