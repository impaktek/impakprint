package com.impaktek.impakprint.connection

import android.bluetooth.BluetoothDevice
import com.impaktek.impakprint.exceptions.ImpakConnectionException
import java.io.IOException
import java.io.OutputStream

abstract class DeviceConnection {
    var outputStream: OutputStream? = null
    var data: ByteArray

    init {
        this.data = ByteArray(0)
    }

    @Throws(ImpakConnectionException::class)
    abstract fun connect(): DeviceConnection?

    abstract fun devices(): List<BluetoothDevice>

    abstract fun disconnect(): DeviceConnection?

    open val isConnected: Boolean
        get() = this.outputStream != null


    fun write(bytes: ByteArray) {
        val data = ByteArray(bytes.size + data.size)
        System.arraycopy(this.data, 0, data, 0, this.data.size)
        System.arraycopy(bytes, 0, data, this.data.size, bytes.size)
        this.data = data
    }


    /**
     * Send data to the device.
     */
    /**
     * Send data to the device.
     */
    @JvmOverloads
    @Throws(ImpakConnectionException::class)
    fun send(addWaitingTime: Int = 0) {
        if (!this.isConnected) {
            throw ImpakConnectionException("Unable to send data to device.")
        }
        try {
            outputStream!!.write(this.data)
            outputStream!!.flush()
            val waitingTime = addWaitingTime + data.size / 16
            this.data = ByteArray(0)
            if (waitingTime > 0) {
                Thread.sleep(waitingTime.toLong())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw ImpakConnectionException(e.message)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            throw ImpakConnectionException(e.message)
        }
    }
}