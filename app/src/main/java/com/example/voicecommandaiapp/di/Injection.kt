package com.example.voicecommandaiapp.di

import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.repository.TaskRepository
import com.example.voicecommandaiapp.viewmodel.factory.ViewModelFactory

object Injection {

    fun provideViewModelFactory(): ViewModelFactory {
        val apiService = ApiClient.apiService
        val taskRepository = TaskRepository(apiService)
        return ViewModelFactory(taskRepository)
    }
}
