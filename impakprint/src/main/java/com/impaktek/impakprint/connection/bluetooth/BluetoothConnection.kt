package com.impaktek.impakprint.connection.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.annotation.RequiresPermission
import com.impaktek.impakprint.connection.DeviceConnection
import com.impaktek.impakprint.exceptions.ImpakConnectionException
import java.io.IOException
import java.util.UUID

class BluetoothConnection(val device: BluetoothDevice?, bluetoothAdapter: BluetoothAdapter) : DeviceConnection() {
    val adapter = bluetoothAdapter
    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }

    private var socket: BluetoothSocket? = null

    override val isConnected: Boolean
        get() = socket?.isConnected == true && super.isConnected

    /**
     * Connect to the Bluetooth device.
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override fun connect(): BluetoothConnection {
        if (isConnected) {
            return this
        }

        if (device == null) {
            throw ImpakConnectionException("Bluetooth device is not connected.")
        }

        val bluetoothAdapter = adapter
        val uuid = getDeviceUUID()

        try {
            val dev = adapter.getRemoteDevice(device.address)
            socket = dev.createInsecureRfcommSocketToServiceRecord(uuid)
            bluetoothAdapter.cancelDiscovery()
            socket?.connect()
            outputStream = socket?.outputStream
            data = ByteArray(0)
        } catch (e: IOException) {
            e.printStackTrace()
            disconnect()
            throw ImpakConnectionException("Unable to connect to Bluetooth device.")
        }
        return this
    }

    /**
     * Get the Bluetooth device UUID.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun getDeviceUUID(): UUID {
        val uuids = device!!.uuids
        return if (uuids != null && uuids.isNotEmpty()) {
            uuids.firstOrNull { it.uuid == SPP_UUID }?.uuid ?: uuids[0].uuid
        } else {
            SPP_UUID
        }
    }

    /**
     * Disconnect from the Bluetooth device.
     */
    override fun disconnect(): BluetoothConnection {
        data = ByteArray(0)
        outputStream?.close()
        outputStream = null
        socket?.close()
        socket = null
        return this
    }
}
