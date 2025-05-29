package com.tii.reversetcpportforwarding

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MyApp()
		}
		displayUsbDevices()
	}


	private fun displayUsbDevices() {
		val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
		val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList

		if (deviceList.isEmpty()) {
			Log.i ("usb devices","No USB devices connected.");
			return
		}

		val stringBuilder = StringBuilder()
		for ((_, device) in deviceList) {
			stringBuilder.append("Device Name: ${device.deviceName}\n")
			stringBuilder.append("Vendor ID: ${device.vendorId}\n")
			stringBuilder.append("Product ID: ${device.productId}\n")
			stringBuilder.append("Class: ${device.deviceClass}\n")
			stringBuilder.append("Subclass: ${device.deviceSubclass}\n")
			stringBuilder.append("Protocol: ${device.deviceProtocol}\n\n")
		}

		Log.i("Usb devices", stringBuilder.toString())
	}
}
