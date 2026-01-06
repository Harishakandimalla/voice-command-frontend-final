package com.example.voicecommandaiapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HelpSupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Help & Support"

        setupGetHelpSection()
        setupFaqSection()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupGetHelpSection() {
        val chatWithAiView = findViewById<View>(R.id.chat_with_ai)
        chatWithAiView.findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_chat_bubble)
        chatWithAiView.findViewById<TextView>(R.id.item_title).text = "Chat with AI Assistant"
        chatWithAiView.findViewById<TextView>(R.id.item_subtitle).text = "Get instant help from our AI"
        chatWithAiView.setOnClickListener {
            startActivity(Intent(this, AiAssistantActivity::class.java))
        }

        val userGuideView = findViewById<View>(R.id.user_guide)
        userGuideView.findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_book)
        userGuideView.findViewById<TextView>(R.id.item_title).text = "User Guide"
        userGuideView.findViewById<TextView>(R.id.item_subtitle).text = "Learn how to use TaskAI"

        val contactSupportView = findViewById<View>(R.id.contact_support)
        contactSupportView.findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_mail)
        contactSupportView.findViewById<TextView>(R.id.item_title).text = "Contact Support"
        contactSupportView.findViewById<TextView>(R.id.item_subtitle).text = "Email us at harishkandimalla92@gmail.com"
        contactSupportView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:harishkandimalla92@gmail.com")
            startActivity(intent)
        }
    }

    private fun setupFaqSection() {
        val faq1View = findViewById<View>(R.id.faq1)
        faq1View.findViewById<TextView>(R.id.faq_question).text = "How does voice command work?"
        faq1View.findViewById<TextView>(R.id.faq_answer).text = "Simply tap the microphone button and speak naturally. Our AI understands context and creates tasks automatically."

        val faq2View = findViewById<View>(R.id.faq2)
        faq2View.findViewById<TextView>(R.id.faq_question).text = "What is auto-scheduling?"
        faq2View.findViewById<TextView>(R.id.faq_answer).text = "AI analyzes your calendar and automatically finds the best time slots for your tasks based on priorities and preferences."

        val faq3View = findViewById<View>(R.id.faq3)
        faq3View.findViewById<TextView>(R.id.faq_question).text = "How do I upgrade to PRO?"
        faq3View.findViewById<TextView>(R.id.faq_answer).text = "Go to Settings > Subscription and choose a plan that works for you. Enjoy 7 days free trial!"
    }
}
