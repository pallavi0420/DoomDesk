package com.dormdesk.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AiApiService {
    @POST("api/chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>

    @GET("api/health")
    suspend fun checkHealth(): Response<HealthResponse>

    @GET("api/faq")
    suspend fun getFaqs(): Response<List<FaqItem>>
}
