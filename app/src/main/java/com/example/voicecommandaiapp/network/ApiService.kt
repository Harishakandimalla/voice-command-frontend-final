package com.example.voicecommandaiapp.network

import com.example.voicecommandaiapp.model.*
import com.example.voicecommandaiapp.model.request.*
import com.example.voicecommandaiapp.model.response.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // ---------- AUTH ----------

    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): SimpleResponse

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("forgot_password.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): SimpleResponse

    @POST("verify_otp.php")
    suspend fun verifyOtp(@Body request: OtpRequest): SimpleResponse

    @POST("verify_reset_otp.php")
    suspend fun verifyResetOtp(@Body request: OtpRequest): SimpleResponse

    @POST("resend_otp.php")
    suspend fun resendOtp(@Body request: ResendOtpRequest): SimpleResponse

    @POST("reset_password.php")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): SimpleResponse

    // ---------- AI ----------

    @GET("ai/get_ai_suggestions.php")
    suspend fun getAiSuggestions(@Query("user_id") userId: Int): AiSuggestionsResponse

    @GET("ai/mark_ai_read.php")
    suspend fun markAiSuggestionRead(@Query("id") suggestionId: Int): SimpleResponse

    @GET("ai/get_ai_unread_count.php")
    suspend fun getAiUnreadCount(@Query("user_id") userId: Int): UnreadCountResponse

    @POST("ai/voice_to_task.php")
    suspend fun evaluateVoiceCommand(@Body voiceRequest: VoiceCommandRequest): VoiceCommandResponse

    @GET("ai/generate_ai_suggestions.php")
    suspend fun generateAiSuggestions(@Query("user_id") userId: Int): SimpleResponse

    // Chat Endpoint
    @POST("ai/chat.php")
    suspend fun chat(@Body request: ChatRequest): SimpleResponse


    // ---------- TASKS ----------

    @GET("tasks/get_tasks.php")
    suspend fun getTasks(
        @Query("user_id") userId: Int,
        @Query("filter") filter: String
    ): TasksResponse

    @GET("tasks/get_task_by_id.php")
    suspend fun getTaskById(
        @Query("id") taskId: Int,
        @Query("user_id") userId: Int
    ): TaskResponse

    @GET("tasks/task_count.php")
    suspend fun getTaskCounts(
        @Query("user_id") userId: Int
    ): TaskCountResponse

    @POST("tasks/create_task.php")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): CreateTaskResponse

    @POST("tasks/update_task.php")
    suspend fun updateTask(
        @Body request: UpdateTaskRequest
    ): SimpleResponse

    // ✅ MATCHES ViewModel + Repository
    @POST("tasks/complete_task.php")
    suspend fun updateTaskStatus(
        @Body request: UpdateTaskStatusRequest
    ): SimpleResponse

    @POST("tasks/delete_task.php")
    suspend fun deleteTask(
        @Body request: DeleteTaskRequest
    ): SimpleResponse

    // ---------- NOTIFICATIONS ----------
    @GET("notifications/mark_notification_read.php")
    suspend fun markNotificationRead(
        @Query("id") notificationId: Int
    ): SimpleResponse

    @GET("notifications/get_notifications.php")
    suspend fun getNotifications(
        @Query("user_id") userId: Int
    ): NotificationsResponse
}
