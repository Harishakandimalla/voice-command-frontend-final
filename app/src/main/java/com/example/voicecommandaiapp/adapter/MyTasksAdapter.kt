package com.example.voicecommandaiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.R
import com.example.voicecommandaiapp.model.ListItem
import com.example.voicecommandaiapp.model.Task

class MyTasksAdapter(
    private var items: List<ListItem>,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TASK = 1
    }

    fun updateData(newItems: List<ListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> TYPE_HEADER
            is ListItem.TaskItem -> TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderVH(inflater.inflate(R.layout.item_header, parent, false))
        } else {
            TaskVH(inflater.inflate(R.layout.item_task, parent, false))
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderVH).bind(item)
            is ListItem.TaskItem -> (holder as TaskVH).bind(item.task)
        }
    }

    inner class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        private val headerTitle: TextView = view.findViewById(R.id.headerTitle)
        private val headerSubtitle: TextView = view.findViewById(R.id.headerSubtitle)

        fun bind(header: ListItem.Header) {
            headerTitle.text = header.title
            headerSubtitle.text = header.subtitle
        }
    }

    inner class TaskVH(view: View) : RecyclerView.ViewHolder(view) {
        private val taskTitle: TextView = view.findViewById(R.id.taskTitle)
        private val taskTime: TextView = view.findViewById(R.id.taskTime)

        fun bind(task: Task) {
            taskTitle.text = task.title ?: "Untitled"
            taskTime.text = task.task_date ?: "-"
            itemView.setOnClickListener { onTaskClick(task) }
        }
    }
}
