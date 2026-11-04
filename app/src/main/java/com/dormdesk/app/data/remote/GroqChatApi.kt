package com.dormdesk.app.data.remote

import android.util.Log
import com.dormdesk.app.data.model.ChatMessage
import com.dormdesk.app.data.model.User
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class ChatContextDto(
    @SerializedName("user_id") val userId: String?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("hostel") val hostel: String?,
    @SerializedName("room_number") val roomNumber: String?,
    @SerializedName("pending_complaints_count") val pendingComplaintsCount: Int,
    @SerializedName("active_complaints_summary") val activeComplaintsSummary: String? = null,
    @SerializedName("latest_announcement_title") val latestAnnouncementTitle: String? = null
)

data class ChatRequestDto(
    @SerializedName("message") val message: String,
    @SerializedName("conversation_history") val history: List<Map<String, String>> = emptyList(),
    @SerializedName("context") val context: ChatContextDto
)

data class ChatResponseDto(
    @SerializedName("response") val response: String,
    @SerializedName("source") val source: String,
    @SerializedName("model_used") val modelUsed: String?,
    @SerializedName("suggested_actions") val suggestedActions: List<String>?,
    @SerializedName("target_route") val targetRoute: String?
)

class GroqChatApi(
    // 10.0.2.2 points to host machine from Android Emulator.
    // Can be configured to any local IP or deployed server URL.
    private val baseUrl: String = "http://10.0.2.2:8000"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(
        query: String,
        currentUser: User?,
        pendingCount: Int,
        activeSummary: String?,
        latestAnnouncement: String?,
        history: List<ChatMessage>
    ): ChatResponseDto? = withContext(Dispatchers.IO) {
        try {
            val historyList = history.takeLast(6).map {
                mapOf(
                    "role" to if (it.senderType.name == "USER") "user" else "assistant",
                    "content" to it.message
                )
            }

            val contextDto = ChatContextDto(
                userId = currentUser?.uid,
                userName = currentUser?.fullName,
                hostel = currentUser?.hostel,
                roomNumber = currentUser?.roomNumber,
                pendingComplaintsCount = pendingCount,
                activeComplaintsSummary = activeSummary,
                latestAnnouncementTitle = latestAnnouncement
            )

            val payload = ChatRequestDto(
                message = query,
                history = historyList,
                context = contextDto
            )

            val requestBody = gson.toJson(payload).toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("$baseUrl/api/chat")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: return@withContext null
                return@withContext gson.fromJson(bodyString, ChatResponseDto::class.java)
            } else {
                Log.w("GroqChatApi", "Backend returned error ${response.code}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.d("GroqChatApi", "FastAPI backend unreachable (${e.message}), using local FAQ engine.")
            return@withContext null
        }
    }
}
