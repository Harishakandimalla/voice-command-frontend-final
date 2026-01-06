package com.example.voicecommandaiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.R
import com.example.voicecommandaiapp.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>, filter: String) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // ✅ MATCHES XML EXACTLY
        private val tvTaskTitle: TextView =
            itemView.findViewById(R.id.tvTaskTitle)
        private val tvTaskTime: TextView =
            itemView.findViewById(R.id.tvTaskTime)
        private val tvTaskCategory: TextView =
            itemView.findViewById(R.id.tvTaskCategory)
        private val tvTaskPriority: TextView =
            itemView.findViewById(R.id.tvTaskPriority)
        private val tvTaskStatus: TextView =
            itemView.findViewById(R.id.tvTaskStatus)

        fun bind(task: Task) {

            tvTaskTitle.text = task.title ?: "Untitled Task"
            tvTaskCategory.text = task.category ?: "General"
            tvTaskPriority.text =
                task.priority
                    ?.lowercase()
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Normal"

            // 🗓 Date logic
            val today = LocalDate.now()
            
            val taskDate = try {
                 task.task_date?.let { LocalDate.parse(it) }
            } catch (e: Exception) {
                null
            }

            val dateLabel = when {
                taskDate == null -> "No date"
                taskDate.isEqual(today) -> "Today"
                taskDate.isBefore(today) -> "Overdue"
                else -> taskDate.format(
                    DateTimeFormatter.ofPattern("dd MMM")
                )
            }

            val time =
                task.task_time?.takeIf { it.isNotBlank() } ?: ""

            tvTaskTime.text =
                if (time.isNotEmpty()) "$dateLabel • $time"
                else dateLabel

            tvTaskStatus.text =
                if (task.status == "COMPLETED") "Completed" else "Active"

            itemView.setOnClickListener {
                // 🛡 SAFETY GUARD
                if (task.id > 0) {
                    onTaskClick(task)
                }
            }
        }
    }
}
