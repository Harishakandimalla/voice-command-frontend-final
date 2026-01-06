package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.voicecommandaiapp.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val session = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({

            // 🚨 STRICT RULE: Always go to Onboarding first
            // Do NOT check session here.
            val intent = Intent(this, OnboardingActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()

        }, 1500)
    }
}
