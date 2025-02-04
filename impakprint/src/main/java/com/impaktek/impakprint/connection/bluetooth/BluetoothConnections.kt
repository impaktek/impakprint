package com.impaktek.impakprint.connection.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.annotation.RequiresPermission

open class BluetoothConnections(private val bluetoothAdapter: BluetoothAdapter?) {
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    open fun getList(): List<BluetoothConnection>? {
        val bluetoothAdapter = bluetoothAdapter ?: return null

        if (!bluetoothAdapter.isEnabled) {
            return null
        }
        return  bluetoothAdapter.bondedDevices.map { BluetoothConnection(it, bluetoothAdapter) }
    }
}
