package com.impaktek.impakprint.connection.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.util.Log
import com.impaktek.impakprint.exceptions.ImpakConnectionException


@SuppressLint("MissingPermission")
class BluetoothPrintersConnections(private val bluetoothAdapter: BluetoothAdapter) : BluetoothConnections(bluetoothAdapter) {

    /**
     * Easy way to get the first Bluetooth printer paired/connected.
     *
     * @return an ImpakPrinterCommands instance
     */

    companion object {
        fun selectFirstPaired(adapter: BluetoothAdapter, onError: ((Throwable) -> Unit)? = null): BluetoothConnection? {
            val printers = BluetoothPrintersConnections(bluetoothAdapter = adapter)
            val bluetoothPrinters = printers.getList()
            Log.d("TAG", "selectFirstPaired: ${bluetoothPrinters?.size}")
            bluetoothPrinters?.let {
                for (printer in it) {
                    try {
                        return printer.connect()
                    } catch (e: ImpakConnectionException) {
                        onError?.invoke(e)
                        e.printStackTrace()
                    }
                }
            }
            return null
        }

        fun pairDevice(connection: BluetoothConnection): BluetoothConnection? {
            return  connection.connect()
        }

        fun fetchPairedDevices(adapter: BluetoothAdapter): List<BluetoothConnection> {
            val printers = BluetoothPrintersConnections(bluetoothAdapter = adapter)
            return  printers.getList() ?: emptyList()
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

            Log.d("TAG", "selectFirstPaired: $majDeviceCl ->>>> $deviceCl")

            if ((majDeviceCl == BluetoothClass.Device.Major.IMAGING || majDeviceCl == BluetoothClass.Device.Major.UNCATEGORIZED) &&
                (deviceCl == 1664 || deviceCl == BluetoothClass.Device.Major.UNCATEGORIZED || deviceCl == BluetoothClass.Device.Major.IMAGING)) {
                printersTmp.add(BluetoothConnection(device, bluetoothAdapter = bluetoothAdapter))
            }
        }
        return printersTmp
    }
}
