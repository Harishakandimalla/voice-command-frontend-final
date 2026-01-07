package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voicecommandaiapp.model.request.LoginRequest
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView
    private lateinit var tvForgotPassword: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnSignIn)
        tvSignup = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgot)

        btnLogin.setOnClickListener { loginUser() }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            btnLogin.isEnabled = false

            try {
                val response = ApiClient.apiService.login(
                    LoginRequest(email, password)
                )

                // ✅ CORRECT CHECK
                if (response.ok && response.user != null) {

                    // ✅ SAVE USER SESSION
                    sessionManager.saveUser(
                        response.user.id,
                        response.user.name,
                        email
                    )

                    sessionManager.setOnboardingCompleted()

                    Toast.makeText(
                        this@LoginActivity,
                        "Login successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    // ✅ GO TO HOME PAGE
                    startActivity(
                        Intent(this@LoginActivity, HomeActivity::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                    )

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        response.error ?: "Invalid credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnLogin.isEnabled = true
            }
        }
    }
}
