package com.example.voicecommandaiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.R
import com.example.voicecommandaiapp.model.CategorySummary

class CategorySummaryAdapter(private val categorySummaries: List<CategorySummary>) :
    RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categorySummaries[position])
    }

    override fun getItemCount() = categorySummaries.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val categoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        private val taskCount: TextView = itemView.findViewById(R.id.tv_task_count)

        fun bind(categorySummary: CategorySummary) {
            categoryIcon.setImageResource(categorySummary.icon)
            categoryName.text = categorySummary.name
            taskCount.text = "${categorySummary.taskCount} tasks"
        }
    }
}
