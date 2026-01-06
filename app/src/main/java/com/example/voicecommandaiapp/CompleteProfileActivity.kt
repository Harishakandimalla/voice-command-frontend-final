package com.example.voicecommandaiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CompleteProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        val etDisplayName = findViewById<EditText>(R.id.etDisplayName)
        val btnCompleteSetup = findViewById<Button>(R.id.btnCompleteSetup)

        btnCompleteSetup.setOnClickListener {
            val displayName = etDisplayName.text.toString().trim()

            if (displayName.isEmpty()) {
                Toast.makeText(this, "Display name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save the user's name to SharedPreferences
            val sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("USER_NAME", displayName)
            editor.apply()

            // Go to Home Activity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity() // Finish all previous auth activities
        }
    }
}
