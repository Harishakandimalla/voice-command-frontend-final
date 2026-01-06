package com.example.voicecommandaiapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.model.Task

class CalendarActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        recyclerView = findViewById(R.id.rv_calendar_tasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        TaskManager.tasks.observe(this) { _: List<Task> ->
            // empty for now
        }
    }
}
