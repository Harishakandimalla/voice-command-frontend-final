package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voicecommandaiapp.model.request.ForgotPasswordRequest
import com.example.voicecommandaiapp.network.ApiClient
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSendCode = findViewById<Button>(R.id.btnSendOtp)

        btnBack.setOnClickListener {
            finish()
        }

        btnSendCode.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val request = ForgotPasswordRequest(email)
                    val response = ApiClient.apiService.forgotPassword(request)

                    if (response.ok) {
                        val intent = Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            response.message ?: "Could not send reset code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
