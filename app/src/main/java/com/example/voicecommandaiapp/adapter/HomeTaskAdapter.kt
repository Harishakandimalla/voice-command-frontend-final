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

class HomeTaskAdapter(
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TASK = 1
    }

    /* ---------------- PUBLIC API ---------------- */

    fun updateSections(
        today: List<Task>,
        upcoming: List<Task>,
        pending: List<Task>,
        completed: List<Task>
    ) {
        items.clear()

        if (today.isNotEmpty()) {
            items.add("Today")
            items.addAll(today)
        }

        if (upcoming.isNotEmpty()) {
            items.add("Upcoming")
            items.addAll(upcoming)
        }

        if (pending.isNotEmpty()) {
            items.add("Pending")
            items.addAll(pending)
        }

        if (completed.isNotEmpty()) {
            items.add("Completed")
            items.addAll(completed)
        }

        notifyDataSetChanged()
    }

    /* ---------------- ADAPTER ---------------- */

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TYPE_HEADER else TYPE_TASK
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_task, parent, false)
            TaskViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as String)
            is TaskViewHolder -> holder.bind(items[position] as Task)
        }
    }

    override fun getItemCount(): Int = items.size

    /* ---------------- VIEW HOLDERS ---------------- */

    class HeaderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvTitle =
            itemView.findViewById<TextView>(R.id.tvSectionTitle)

        fun bind(title: String) {
            tvTitle.text = title

            // 🚫 Headers should NOT be clickable
            itemView.setOnClickListener(null)
            itemView.isClickable = false
        }
    }

    inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvTaskTitle =
            itemView.findViewById<TextView>(R.id.tvTaskTitle)
        private val tvTaskTime =
            itemView.findViewById<TextView>(R.id.tvTaskTime)
        private val tvTaskCategory =
            itemView.findViewById<TextView>(R.id.tvTaskCategory)
        private val tvTaskPriority =
            itemView.findViewById<TextView>(R.id.tvTaskPriority)
        private val tvTaskStatus =
            itemView.findViewById<TextView>(R.id.tvTaskStatus)

        fun bind(task: Task) {

            // ---------- TITLE ----------
            tvTaskTitle.text = task.title ?: "Untitled Task"

            val today = LocalDate.now()

            // ---------- DATE (SAFE) ----------
            val taskDate = try {
                task.task_date?.takeIf { it.isNotBlank() }
                    ?.let { LocalDate.parse(it) }
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

            // ---------- TIME ----------
            val timeLabel = task.task_time?.takeIf { it.isNotBlank() }

            tvTaskTime.text =
                if (!timeLabel.isNullOrEmpty())
                    "$dateLabel • $timeLabel"
                else
                    dateLabel

            // ---------- CATEGORY ----------
            tvTaskCategory.text =
                task.category ?: "General"

            // ---------- PRIORITY ----------
            tvTaskPriority.text =
                task.priority
                    ?.lowercase()
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Normal"

            // ---------- STATUS ----------
            tvTaskStatus.text = when {
                task.status == "COMPLETED" -> "Completed"
                taskDate != null && taskDate.isBefore(today) -> "Pending"
                else -> "Active"
            }

            // ---------- CLICK (SAFE) ----------
            itemView.setOnClickListener {
                if (task.id > 0) {
                    onTaskClick(task)
                }
            }
        }
    }
}
