package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voicecommandaiapp.model.request.RegisterRequest
import com.example.voicecommandaiapp.network.ApiClient
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnCreate = findViewById<Button>(R.id.btnCreate)
        val tvSignIn = findViewById<TextView>(R.id.tvSignin)

        // Go to Login
        tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Create Account
        btnCreate.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent double clicks
            btnCreate.isEnabled = false
            btnCreate.text = "Creating..."

            lifecycleScope.launch {
                try {
                    val apiService = ApiClient.apiService
                    val request = RegisterRequest(name, email, password, confirmPassword)
                    val response = apiService.register(request)

                    if (response.error == null) {

                        val intent = Intent(this@SignupActivity, CompleteProfileActivity::class.java)
                        intent.putExtra("email", email) // Pass email to CompleteProfileActivity
                        startActivity(intent)
                        finish()
                    } else {
                        btnCreate.isEnabled = true
                        btnCreate.text = "Create Account"
                        Toast.makeText(
                            this@SignupActivity,
                            response.message ?: "Signup failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    btnCreate.isEnabled = true
                    btnCreate.text = "Create Account"
                    Toast.makeText(
                        this@SignupActivity,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}