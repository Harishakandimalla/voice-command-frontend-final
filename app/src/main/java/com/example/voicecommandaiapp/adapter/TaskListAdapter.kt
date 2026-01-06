package com.example.voicecommandaiapp.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.R
import com.example.voicecommandaiapp.model.ListItem
import com.example.voicecommandaiapp.model.Task
import com.example.voicecommandaiapp.model.TaskStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class TaskListAdapter(private var items: MutableList<ListItem>, private val onTaskClickListener: (Task) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateData(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TASK_UPCOMING = 1
        private const val TYPE_TASK_PENDING = 2
        private const val TYPE_TASK_OVERDUE = 3
        private const val TYPE_TASK_COMPLETED = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is ListItem.Header -> TYPE_HEADER
            is ListItem.TaskItem -> {
                val statusStr = item.task.status ?: "PENDING"
                val status = try { TaskStatus.valueOf(statusStr) } catch(e: Exception) { TaskStatus.PENDING }
                when (status) {
                    TaskStatus.TODAY -> TYPE_TASK_UPCOMING
                    TaskStatus.UPCOMING -> TYPE_TASK_UPCOMING
                    TaskStatus.PENDING -> TYPE_TASK_PENDING
                    TaskStatus.OVERDUE -> TYPE_TASK_OVERDUE
                    TaskStatus.COMPLETED -> TYPE_TASK_COMPLETED
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_header, parent, false))
            TYPE_TASK_UPCOMING -> UpcomingTaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_upcoming, parent, false))
            TYPE_TASK_PENDING -> PendingTaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_pending, parent, false))
            TYPE_TASK_OVERDUE -> OverdueTaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_overdue, parent, false))
            TYPE_TASK_COMPLETED -> CompletedTaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_completed, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ListItem.TaskItem -> {
                val statusStr = item.task.status ?: "PENDING"
                val status = try { TaskStatus.valueOf(statusStr) } catch(e: Exception) { TaskStatus.PENDING }
                
                when (status) {
                    TaskStatus.TODAY -> (holder as UpcomingTaskViewHolder).bind(item.task, onTaskClickListener)
                    TaskStatus.UPCOMING -> (holder as UpcomingTaskViewHolder).bind(item.task, onTaskClickListener)
                    TaskStatus.PENDING -> (holder as PendingTaskViewHolder).bind(item.task, onTaskClickListener)
                    TaskStatus.OVERDUE -> (holder as OverdueTaskViewHolder).bind(item.task, onTaskClickListener)
                    TaskStatus.COMPLETED -> (holder as CompletedTaskViewHolder).bind(item.task, onTaskClickListener)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_header_title)
        private val subtitle: TextView = itemView.findViewById(R.id.tv_header_subtitle)
        private val viewAll: TextView = itemView.findViewById(R.id.tv_view_all)

        fun bind(header: ListItem.Header) {
            title.text = header.title
            subtitle.text = header.subtitle
            if (header.title == "Overdue Tasks") {
                title.setTextColor(Color.RED)
                viewAll.setTextColor(Color.RED)
            } else {
                // Colors are handled by the theme attributes in the XML layout.
            }
        }
    }

    class UpcomingTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_task_title)
        private val time: TextView = itemView.findViewById(R.id.tv_task_time)
        private val category: TextView = itemView.findViewById(R.id.tv_task_category)
        private val priority: TextView = itemView.findViewById(R.id.tv_task_priority)

        fun bind(task: Task, clickListener: (Task) -> Unit) {
            title.text = task.title ?: "Untitled"
            time.text = task.task_time ?: ""
            category.text = task.category ?: "General"
            priority.text = task.priority ?: "Normal"
            itemView.setOnClickListener { clickListener(task) }
        }
    }

    class PendingTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_task_title)
        private val category: TextView = itemView.findViewById(R.id.tv_task_category)
        private val priority: TextView = itemView.findViewById(R.id.tv_task_priority)

        fun bind(task: Task, clickListener: (Task) -> Unit) {
            title.text = task.title ?: "Untitled"
            category.text = task.category ?: "General"
            priority.text = task.priority ?: "Normal"
            itemView.setOnClickListener { clickListener(task) }
        }
    }

    class OverdueTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_task_title)
        private val overdueDays: TextView = itemView.findViewById(R.id.tv_task_overdue_days)
        private val category: TextView = itemView.findViewById(R.id.tv_task_category)
        private val priority: TextView = itemView.findViewById(R.id.tv_task_priority)

        fun bind(task: Task, clickListener: (Task) -> Unit) {
            title.text = task.title ?: "Untitled"
            try {
                if (task.task_date != null) {
                    val taskDate = LocalDate.parse(task.task_date, DateTimeFormatter.ISO_LOCAL_DATE)
                    val today = LocalDate.now()
                    val days = ChronoUnit.DAYS.between(taskDate, today)
                    overdueDays.text = "${days} days overdue"
                } else {
                    overdueDays.text = "Overdue"
                }
            } catch (e: Exception) {
                overdueDays.text = "Overdue"
            }
            category.text = task.category ?: "General"
            priority.text = task.priority ?: "Normal"
            itemView.setOnClickListener { clickListener(task) }
        }
    }

    class CompletedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_task_title)
        private val category: TextView = itemView.findViewById(R.id.tv_task_category)

        fun bind(task: Task, clickListener: (Task) -> Unit) {
            title.text = task.title ?: "Untitled"
            title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            title.setTextColor(Color.GRAY)
            category.text = task.category ?: "General"
            itemView.setOnClickListener { clickListener(task) }
        }
    }
}
