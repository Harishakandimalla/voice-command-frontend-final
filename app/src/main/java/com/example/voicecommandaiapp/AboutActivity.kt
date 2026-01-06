package com.example.voicecommandaiapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About TaskAI"

        setupKeyFeatures()
        setupCredits()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupKeyFeatures() {
        val feature1 = findViewById<View>(R.id.feature1)
        feature1.findViewById<TextView>(R.id.feature_text).text = "Voice-powered task creation"

        val feature2 = findViewById<View>(R.id.feature2)
        feature2.findViewById<TextView>(R.id.feature_text).text = "AI auto-scheduling"

        val feature3 = findViewById<View>(R.id.feature3)
        feature3.findViewById<TextView>(R.id.feature_text).text = "Smart reminders"

        val feature4 = findViewById<View>(R.id.feature4)
        feature4.findViewById<TextView>(R.id.feature_text).text = "Productivity analytics"

        val feature5 = findViewById<View>(R.id.feature5)
        feature5.findViewById<TextView>(R.id.feature_text).text = "Cross-platform sync"
    }

    private fun setupCredits() {
        val credit1 = findViewById<View>(R.id.credit1)
        credit1.findViewById<TextView>(R.id.credit_title).text = "Developed by:"
        credit1.findViewById<TextView>(R.id.credit_value).text = "TaskAI Team"

        val credit2 = findViewById<View>(R.id.credit2)
        credit2.findViewById<TextView>(R.id.credit_title).text = "Design:"
        credit2.findViewById<TextView>(R.id.credit_value).text = "Modern UI Studio"

        val credit3 = findViewById<View>(R.id.credit3)
        credit3.findViewById<TextView>(R.id.credit_title).text = "AI Technology:"
        credit3.findViewById<TextView>(R.id.credit_value).text = "Advanced ML Models"
    }
}
