package com.impaktek.impakprint

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.impaktek.impakprint.connection.bluetooth.BluetoothPrintersConnections
import com.impaktek.impakprint.parsers.text.PrinterTextParserImg
import com.impaktek.impakprint.ui.theme.ImpakprintTheme
import com.impaktek.impakprint.utils.ImpakPrinter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val permissions = remember {
                arrayListOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            }
            var isPermission by remember {
                mutableStateOf(false)
            }

            val permissionState =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    CoroutineScope(Dispatchers.Default).launch {
                        if (it.any { res -> res.value.not() }) {
                            isPermission = false
                            //SnackbarController.sendEvent(SnackbarEvent.Error("Bluetooth permission has been denied and slip cannot be printed"))
                        } else {
                            isPermission = true
                            //SnackbarController.sendEvent(SnackbarEvent.Success("Bluetooth permission has been granted"))
                        }
                    }
                }

            LaunchedEffect(key1 = Unit) {
                isPermission =
                    (Build.VERSION.SDK_INT > Build.VERSION_CODES.R && ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED)
                            && ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.BLUETOOTH
                    ) == PackageManager.PERMISSION_GRANTED

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val perms = listOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    )
                    permissions.addAll(perms)
                }

                if (!isPermission) {
                    permissionState.launch(permissions.toTypedArray())
                }
            }
            ImpakprintTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val manager = context.getSystemService(BluetoothManager::class.java)
    val adapter = manager.adapter

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Hello $name!",
                modifier = modifier
            )

            Button(
                onClick = {
                    val printer = ImpakPrinter(
                        BluetoothPrintersConnections.selectFirstPaired(adapter, onError = {
                            it.printStackTrace()
                        }),
                        203,
                        48f,
                        32
                    )
                    val logo = ContextCompat.getDrawable(context,
                        R.drawable.crs_logo_print
                    )

                    val receipt = """
                                    [C]<img>${PrinterTextParserImg.bitmapToHexadecimalString(printer, logo!!)}</img>
                                    [C]<b>GOVERNMENT OF CROSS RIVER STATE</b>
                                    [C]Cross River Internal Revenue
                                    [C]Service
                                    [C]--------------------------------
                                    [C]<b>GOVERNMENT OF CROSS RIVER STATE</b>
                                    [C]<b>Cross River Internal Revenue Service</b>
                                    [C]--------------------------------
                                    [C]<b>GOVERNMENT OF CROSS RIVER STATE</b>
                                    [C]<b>Cross River Internal Revenue Service</b>
                                    [C]--------------------------------
                                    [C]<b>GOVERNMENT OF CROSS RIVER STATE</b>
                                    [C]<b>Cross River Internal Revenue Service</b>
                                    [C]--------------------------------
                                    [C]<qrcode size='20'>123456</qrcode>
                                   """.trimIndent()
                    printer.printFormattedText(receipt)
                }
            ) {
                Text("PRINT TEXT")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImpakprintTheme {
        Greeting("Android")
    }
}