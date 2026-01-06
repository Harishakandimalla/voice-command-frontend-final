package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class OnboardingSecondActivity : BaseOnboardingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_second)

        setupSkip(R.id.tvSkip)

        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            startActivity(Intent(this, OnboardingThirdActivity::class.java))
            finish()
        }
    }
}
