package com.tii.reversetcpportforwarding

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
	@GET("test")
	suspend fun getTestMessage(): Response<MessageResponse>
}

data class MessageResponse(val message: String)
