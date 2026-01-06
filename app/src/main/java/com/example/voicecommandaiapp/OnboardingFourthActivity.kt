package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class OnboardingFourthActivity : BaseOnboardingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_fourth)

        // Skip → Login
        setupSkip(R.id.tvSkip)

        // Continue → Fifth onboarding
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            startActivity(
                Intent(this, OnboardingFifthActivity::class.java)
            )
            finish()
        }
    }
}
