package com.impaktek.impakprint.utils

class ImpakCharsetEncoding(var charsetName: String, escPosCharsetId: Int) {
    val command: ByteArray = byteArrayOf(0x1B, 0x74, escPosCharsetId.toByte())
}