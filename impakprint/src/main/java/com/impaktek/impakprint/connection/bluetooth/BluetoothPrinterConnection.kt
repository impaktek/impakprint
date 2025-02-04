package com.impaktek.impakprint.connection.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import com.impaktek.impakprint.exceptions.ImpakConnectionException


@SuppressLint("MissingPermission")
class BluetoothPrintersConnections(private val bluetoothAdapter: BluetoothAdapter) : BluetoothConnections(bluetoothAdapter) {

    /**
     * Easy way to get the first Bluetooth printer paired/connected.
     *
     * @return an ImpakPrinterCommands instance
     */

    companion object {
        fun selectFirstPaired(adapter: BluetoothAdapter): BluetoothConnection? {
            val printers = BluetoothPrintersConnections(bluetoothAdapter = adapter)
            val bluetoothPrinters = printers.getList()

            bluetoothPrinters?.let {
                for (printer in it) {
                    try {
                        return printer.connect()
                    } catch (e: ImpakConnectionException) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }
    }

    /**
     * Get a list of Bluetooth printers.
     *
     * @return an array of ImpakPrinterCommands
     */
    @SuppressLint("MissingPermission")
    override fun getList(): MutableList<BluetoothConnection>? {
        val bluetoothDevicesList = super.getList() ?: return null

        val printersTmp = mutableListOf<BluetoothConnection>()
        for (bluetoothConnection in bluetoothDevicesList) {
            val device = bluetoothConnection.device ?: continue

            val majDeviceCl = device.bluetoothClass.majorDeviceClass
            val deviceCl = device.bluetoothClass.deviceClass

            if (majDeviceCl == BluetoothClass.Device.Major.IMAGING &&
                (deviceCl == 1664 || deviceCl == BluetoothClass.Device.Major.IMAGING)) {
                printersTmp.add(BluetoothConnection(device, bluetoothAdapter = bluetoothAdapter))
            }
        }
        return printersTmp
    }
}
