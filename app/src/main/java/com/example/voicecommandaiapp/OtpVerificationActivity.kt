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
import kotlinx.coroutines.launch

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var tvResend: TextView
    private lateinit var tvTimer: TextView
    private var canResend = false
    private lateinit var email: String

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        email = intent.getStringExtra("email") ?: ""

        if (email.isEmpty()) {
            Toast.makeText(this, "Email missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        otp6 = findViewById(R.id.otp6)

        val btnVerify = findViewById<Button>(R.id.btnVerify)
        tvResend = findViewById(R.id.tvResend)
        tvTimer = findViewById(R.id.tvTimer)

        setupOtpInputs()

        btnVerify.setOnClickListener {
            val otp = otp1.text.toString() +
                    otp2.text.toString() +
                    otp3.text.toString() +
                    otp4.text.toString() +
                    otp5.text.toString() +
                    otp6.text.toString()

            if (otp.length == 6) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Enter full 6-digit OTP", Toast.LENGTH_SHORT).show()
            }
        }

        startResendTimer()

        tvResend.setOnClickListener {
            if (canResend) resendOtp()
        }
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
            if (editable.toString().length == 1) nextView?.requestFocus()
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

    private fun verifyOtp(otp: String) {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.apiService
                val request = OtpRequest(email, otp)
                val response = apiService.verifyResetOtp(request)

                if (response.ok) {
                    val intent = Intent(this@OtpVerificationActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@OtpVerificationActivity, response.message ?: "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OtpVerificationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendOtp() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.apiService
                val request = ResendOtpRequest(email)
                val response = apiService.resendOtp(request)

                if (response.ok) {
                    Toast.makeText(this@OtpVerificationActivity, "OTP sent again", Toast.LENGTH_SHORT).show()
                    startResendTimer()
                } else {
                    Toast.makeText(this@OtpVerificationActivity, response.message ?: "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OtpVerificationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
