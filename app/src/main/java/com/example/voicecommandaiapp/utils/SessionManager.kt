package com.example.voicecommandaiapp.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    val userId: Int
        get() = prefs.getInt("USER_ID", -1)

    val userName: String
        get() = prefs.getString("USER_NAME", "") ?: ""

    fun saveUser(id: Int, name: String) {
        prefs.edit()
            .putInt("USER_ID", id)
            .putString("USER_NAME", name)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun setOnboardingCompleted() {
        prefs.edit().putBoolean("ONBOARDING_DONE", true).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("ONBOARDING_DONE", false)
    }
}
