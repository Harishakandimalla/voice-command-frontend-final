package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class OnboardingFifthActivity : BaseOnboardingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_fifth)

        // Skip → Login
        setupSkip(R.id.tvSkip)

        // Continue → Final onboarding
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            startActivity(Intent(this, OnboardingFinalActivity::class.java))
            finish()
        }
    }
}
