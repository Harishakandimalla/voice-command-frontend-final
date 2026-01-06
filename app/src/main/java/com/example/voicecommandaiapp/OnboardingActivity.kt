package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        // Get Started → Second onboarding
        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, OnboardingSecondActivity::class.java))
            finish()
        }

        // ✅ Sign In → Login
        tvSignIn.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
