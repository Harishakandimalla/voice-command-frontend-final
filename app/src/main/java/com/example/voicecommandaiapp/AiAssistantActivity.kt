package com.example.voicecommandaiapp

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.adapter.ChatAdapter
import com.example.voicecommandaiapp.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class AiAssistantActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageView
    private lateinit var recyclerView: RecyclerView

    private val voiceResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                etMessage.setText(spokenText)
                sendMessage(spokenText)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        setupToolbar()
        setupViews()
        setupChat()
        setupListeners()
    }

    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_ai_assistant)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViews() {
        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.iv_send)
        recyclerView = findViewById(R.id.chat_recycler_view)
    }

    private fun setupChat() {
        chatAdapter = ChatAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AiAssistantActivity)
            adapter = chatAdapter
        }

        // Add initial welcome message
        addAiMessage("Hi! I'm your AI assistant. I can help you create tasks, schedule meetings, or answer questions. What would you like to do?")
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                etMessage.text.clear()
            } else {
                // If empty, trigger voice input (Microphone icon logic if we want to reuse the same button, 
                // but checking the drawable, it seems the mic is inside the EditText. 
                // Let's check `et_message` drawable click listener separately if needed, 
                // but usually the mic is a separate button or inside EditText).
                // Based on layout, mic is `drawableStart` of `et_message`.
                // We'll add a click listener to the drawable.
            }
        }

        // Handle Mic click in EditText
        etMessage.setOnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX <= (etMessage.left + etMessage.compoundDrawables[DRAWABLE_LEFT].bounds.width() + etMessage.paddingStart + 50)) {
                    startVoiceInput()
                    return@setOnTouchListener true
                }
            }
            v.performClick()
            false
        }
    }

    private fun startVoiceInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            voiceResultLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice input not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage(message: String) {
        val userMsg = ChatMessage(message, isUser = true)
        chatAdapter.addMessage(userMsg)
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)

        lifecycleScope.launch {
            // Show typing
            chatAdapter.addMessage(ChatMessage("Typing...", isUser = false))
            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)

            try {
                // Call API
                val sessionManager = com.example.voicecommandaiapp.utils.SessionManager(this@AiAssistantActivity)
                val userId = sessionManager.userId
                val request = com.example.voicecommandaiapp.model.request.ChatRequest(userId, message)
                
                val response = com.example.voicecommandaiapp.network.ApiClient.apiService.chat(request)

                // Remove typing
                chatAdapter.removeLastItem()

                if (response.ok && !response.message.isNullOrBlank()) {
                    addAiMessage(response.message)
                } else {
                    addAiMessage("Sorry, I couldn't get a response. (${response.error ?: "Unknown error"})")
                }

            } catch (e: Exception) {
                chatAdapter.removeLastItem()
                // Show specific error for debugging
                android.widget.Toast.makeText(this@AiAssistantActivity, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                
                // Fallback to mock (Smart Simulation)
                val mockReply = generateMockResponse(message)
                addAiMessage(mockReply)
            }
        }
    }

    private fun addAiMessage(message: String) {
        val aiMsg = ChatMessage(message, isUser = false)
        chatAdapter.addMessage(aiMsg)
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun generateMockResponse(userMessage: String): String {
        val lowerMsg = userMessage.lowercase()
        return when {
            lowerMsg.contains("task") || lowerMsg.contains("create") -> "I can help with that! What is the task title?"
            lowerMsg.contains("meeting") || lowerMsg.contains("schedule") -> "Sure, when would you like to schedule the meeting?"
            lowerMsg.contains("hello") || lowerMsg.contains("hi") -> "Hello! How can I assist you today?"
            lowerMsg.contains("thank") -> "You're welcome! Let me know if you need anything else."
            else -> "I'm still learning, but I can help you manage your tasks and schedule. Try asking 'Create a task'."
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
