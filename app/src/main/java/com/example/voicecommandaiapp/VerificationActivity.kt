package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voicecommandaiapp.model.request.OtpRequest
import com.example.voicecommandaiapp.model.request.ResendOtpRequest
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.network.ApiService
import kotlinx.coroutines.launch

class VerificationActivity : AppCompatActivity() {

    private lateinit var email: String

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText

    private lateinit var btnVerify: Button
    private lateinit var tvResend: TextView
    private lateinit var tvTimer: TextView

    private var canResend = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        // Get email from SignupActivity
        email = intent.getStringExtra("email") ?: ""

        if (email.isEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Bind views
        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        otp6 = findViewById(R.id.otp6)

        btnVerify = findViewById(R.id.btnVerify)
        tvResend = findViewById(R.id.tvResend)
        tvTimer = findViewById(R.id.tvTimer)

        // Setup auto-advance and backspace
        setupOtpInputs()

        // Verify OTP
        btnVerify.setOnClickListener {
            val otp = otp1.text.toString() +
                    otp2.text.toString() +
                    otp3.text.toString() +
                    otp4.text.toString() +
                    otp5.text.toString() +
                    otp6.text.toString()

            if (otp.length != 6) {
                Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            verifyOtp(otp)
        }

        // Resend OTP
        tvResend.setOnClickListener {
            if (canResend) resendOtp()
        }

        startResendTimer()
    }

    private fun setupOtpInputs() {
        val editTexts = arrayOf(otp1, otp2, otp3, otp4, otp5, otp6)
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(GenericTextWatcher(editTexts[i], editTexts.getOrNull(i + 1)))
            if (i > 0) {
                editTexts[i].setOnKeyListener(GenericKeyEvent(editTexts[i], editTexts.getOrNull(i - 1)))
            }
        }
    }

    inner class GenericTextWatcher(private val currentView: EditText, private val nextView: EditText?) : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            if (text.length == 1) {
                nextView?.requestFocus()
            }
        }
        override fun beforeTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
        override fun onTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
    }

    inner class GenericKeyEvent(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otp1 && currentView.text.isEmpty()) {
                previousView?.requestFocus()
                return true
            }
            return false
        }
    }

    // ---------------- VERIFY OTP ----------------
    private fun verifyOtp(otp: String) {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.apiService
                val request = OtpRequest(email, otp)
                val response = apiService.verifyOtp(request)

                if (response.ok) {
                    // ✅ Go to Complete Profile
                    startActivity(
                        Intent(
                            this@VerificationActivity,
                            CompleteProfileActivity::class.java
                        ).putExtra("email", email)
                    )
                    finish()
                } else {
                    Toast.makeText(
                        this@VerificationActivity,
                        response.message ?: "Invalid OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@VerificationActivity,
                    "Network error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------- RESEND OTP ----------------
    private fun resendOtp() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.apiService
                val request = ResendOtpRequest(email)
                val response = apiService.resendOtp(request)

                if (response.ok) {
                    Toast.makeText(
                        this@VerificationActivity,
                        "Verification code sent again",
                        Toast.LENGTH_SHORT
                    ).show()
                    startResendTimer()
                } else {
                    Toast.makeText(
                        this@VerificationActivity,
                        response.message ?: "Failed to resend OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@VerificationActivity,
                    "Network error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------- TIMER ----------------
    private fun startResendTimer() {
        canResend = false
        tvResend.isEnabled = false
        tvResend.alpha = 0.5f

        object : CountDownTimer(60000, 1000) {
            override fun onTick(ms: Long) {
                tvTimer.text = "Resend in ${ms / 1000}s"
            }

            override fun onFinish() {
                canResend = true
                tvTimer.text = ""
                tvResend.isEnabled = true
                tvResend.alpha = 1f
            }
        }.start()
    }
}
