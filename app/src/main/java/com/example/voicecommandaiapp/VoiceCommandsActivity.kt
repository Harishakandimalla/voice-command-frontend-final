package com.example.voicecommandaiapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class VoiceCommandsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_commands)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_voice_commands)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
