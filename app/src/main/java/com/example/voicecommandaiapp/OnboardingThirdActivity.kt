package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class OnboardingThirdActivity : BaseOnboardingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_third)

        setupSkip(R.id.tvSkip)

        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            startActivity(Intent(this, OnboardingFourthActivity::class.java))
            finish()
        }
    }
}

