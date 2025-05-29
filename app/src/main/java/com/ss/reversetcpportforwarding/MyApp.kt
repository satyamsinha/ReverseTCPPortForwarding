package com.tii.reversetcpportforwarding

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket


@Composable
fun MyApp() {
	val viewModel = remember { MainViewModel() }
	val message by viewModel.message.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.fetchMessage()
	}

	Text(
		text = message,
		modifier = Modifier.fillMaxSize().wrapContentSize(),
		fontSize = 22.sp
	)
}


class MainViewModel : ViewModel() {
	private val _message = MutableStateFlow("Loading...")
	val message: StateFlow<String> = _message

	private val api = provideApiService()

	fun fetchMessage() {
		viewModelScope.launch {
			try {

				val response = api.getTestMessage()
				if (response.isSuccessful) {
					_message.value = response.body()?.message ?: "No message"
				} else {
					_message.value = "Error: ${response.code()}"
				}
			} catch (e: Exception) {
				_message.value = "Failed: ${e.localizedMessage}"
			}
			Thread {
				_message.value = connectToServer()
			}.start()

		}
	}

	fun provideApiService(): ApiService {
		val retrofit = Retrofit.Builder()
			.baseUrl("http://0.0.0.0:9696")  // 👈 Use your host's IP
			.addConverterFactory(GsonConverterFactory.create())
			.build()
		return retrofit.create(ApiService::class.java)
	}

	private fun connectToServer() :String {
		try {
			Log.d("EthernetClient", "Connecting to server...")
			val socket = Socket("192.168.10.1", 9000) //
			val output: OutputStream = socket.getOutputStream()
			val message = "Hello from Android over Ethernet\n"
			output.write(message.toByteArray())
			output.flush()

			val input = BufferedReader(InputStreamReader(socket.getInputStream()))
			val response = input.readLine()
			Log.d("EthernetClient", "Server Response: $response")
			socket.close()
			return response
		} catch (e: Exception) {
			Log.e("EthernetClient", "Connection failed", e)
			return e.toString()
		}

	}

}
