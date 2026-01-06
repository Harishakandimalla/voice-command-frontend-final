package com.example.voicecommandaiapp.network

import com.example.voicecommandaiapp.model.TaskStatus
import com.example.voicecommandaiapp.util.TaskStatusAdapter
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val gson = GsonBuilder()
        .registerTypeAdapter(TaskStatus::class.java, TaskStatusAdapter())
        .create()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
