package com.example.voicecommandaiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.TaskPriority
import com.example.voicecommandaiapp.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import com.example.voicecommandaiapp.utils.SessionManager
import com.example.voicecommandaiapp.network.ApiClient
import com.example.voicecommandaiapp.model.request.VoiceCommandRequest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class UnderstandingActivity : AppCompatActivity() {

    private lateinit var transcribedTextView: TextView
    private lateinit var sessionManager: SessionManager

    private val taskCreatedResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK, result.data)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_understanding)

        sessionManager = SessionManager(this)
        transcribedTextView = findViewById(R.id.tv_transcribed_text)
        findViewById<ImageView>(R.id.iv_close).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_cancel).setOnClickListener { finish() }

        val spokenText = intent.getStringExtra("SPOKEN_TEXT")
        if (spokenText != null) {
            transcribedTextView.text = spokenText
            processVoiceCommand(spokenText)
        }
    }

    private fun processVoiceCommand(command: String) {
        val userId = sessionManager.userId
        if (userId == -1) {
            transcribedTextView.text = "Error: User not logged in."
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.evaluateVoiceCommand(
                    VoiceCommandRequest(userId, command)
                )

                if (response.ok) {
                    if (response.action == "CREATE_TASK" || response.data != null) {
                        handleCreateTask(response.data)
                    } else if (response.action == "CHAT") {
                        handleChatResponse(response.message)
                    } else {
                        handleChatResponse(response.message ?: "I didn't understand that.")
                    }
                } else {
                    transcribedTextView.text = "Error: ${response.message ?: response.error}"
                }
            } catch (e: Exception) {
                transcribedTextView.text = "Connection error: ${e.localizedMessage}"
                e.printStackTrace()
            }
        }
    }

    private fun handleCreateTask(data: com.example.voicecommandaiapp.model.response.TaskData?) {
        if (data == null) {
            transcribedTextView.text = "Error: Missing task data."
            return
        }

        val newTask = Task(
            id = System.currentTimeMillis().toInt(),
            title = data.title ?: "New Task",
            description = null,
            task_date = data.date,
            task_time = data.time,
            category = data.category ?: "Work",
            priority = data.priority ?: TaskPriority.MEDIUM.name,
            status = TaskStatus.UPCOMING.name,
            reminder = null,
            repeat = null
        )

        // Show a brief success message locally before navigating
        transcribedTextView.text = "Creating task: ${newTask.title}..."

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, TaskCreatedActivity::class.java)
            intent.putExtra("NEW_TASK", newTask)
            taskCreatedResultLauncher.launch(intent)
        }, 1000)
    }

    private fun handleChatResponse(message: String?) {
        transcribedTextView.text = message
        // Maybe keep it open for the user to read
    }
}
