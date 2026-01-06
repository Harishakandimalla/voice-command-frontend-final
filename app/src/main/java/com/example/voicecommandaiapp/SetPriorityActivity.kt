package com.example.voicecommandaiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicecommandaiapp.adapter.PriorityAdapter
import com.example.voicecommandaiapp.databinding.ActivitySetPriorityBinding
import com.example.voicecommandaiapp.model.TaskPriority

class SetPriorityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetPriorityBinding
    private lateinit var priorityAdapter: PriorityAdapter
    private var selectedPriority: TaskPriority? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPriorityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarSetPriority)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val priorities = TaskPriority.values().toList()
        priorityAdapter = PriorityAdapter(priorities) { priority ->
            selectedPriority = priority
        }

        binding.rvPriorities.apply {
            layoutManager = LinearLayoutManager(this@SetPriorityActivity)
            adapter = priorityAdapter
        }

        binding.btnSetPriority.setOnClickListener {
            selectedPriority?.let {
                val resultIntent = Intent()
                resultIntent.putExtra("SELECTED_PRIORITY", it.name)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
