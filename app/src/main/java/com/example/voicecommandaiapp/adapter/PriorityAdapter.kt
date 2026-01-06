package com.example.voicecommandaiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.databinding.ItemPriorityBinding
import com.example.voicecommandaiapp.model.TaskPriority

class PriorityAdapter(
    private val priorities: List<TaskPriority>,
    private val onPrioritySelected: (TaskPriority) -> Unit
) : RecyclerView.Adapter<PriorityAdapter.PriorityViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriorityViewHolder {
        val binding = ItemPriorityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PriorityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PriorityViewHolder, position: Int) {
        val priority = priorities[position]
        holder.bind(priority, position == selectedPosition)
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            onPrioritySelected(priority)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = priorities.size

    inner class PriorityViewHolder(private val binding: ItemPriorityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(priority: TaskPriority, isSelected: Boolean) {
            binding.tvPriorityName.text = priority.name
            binding.ivPriorityCheckmark.visibility = if (isSelected) View.VISIBLE else View.GONE
            itemView.isSelected = isSelected
        }
    }
}
