package com.example.voicecommandaiapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AiSuggestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_suggestions)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_ai_suggestions)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
