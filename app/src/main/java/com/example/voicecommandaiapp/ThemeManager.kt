package com.example.voicecommandaiapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

    fun getThemeMode(): Int {
        return sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setThemeMode(mode: Int) {
        sharedPreferences.edit().putInt("theme_mode", mode).apply()
    }

    fun getAccentColor(): String {
        return sharedPreferences.getString("accent_color", "Purple") ?: "Purple"
    }

    fun setAccentColor(color: String) {
        sharedPreferences.edit().putString("accent_color", color).apply()
    }

    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(getThemeMode())
        val theme = when (getAccentColor()) {
            "Blue" -> R.style.Theme_VoiceCommandAIApp_Blue
            "Green" -> R.style.Theme_VoiceCommandAIApp_Green
            "Pink" -> R.style.Theme_VoiceCommandAIApp_Pink
            "Orange" -> R.style.Theme_VoiceCommandAIApp_Orange
            else -> R.style.Theme_VoiceCommandAIApp_Purple
        }
        context.setTheme(theme)
    }
}