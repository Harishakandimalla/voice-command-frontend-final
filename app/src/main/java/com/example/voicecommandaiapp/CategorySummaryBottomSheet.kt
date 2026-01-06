package com.example.voicecommandaiapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.adapter.CategorySummaryAdapter
import com.example.voicecommandaiapp.model.CategorySummary
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategorySummaryBottomSheet : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategorySummaryAdapter

    companion object {
        private const val ARG_TASKS = "tasks_list"

        fun newInstance(tasks: ArrayList<com.example.voicecommandaiapp.model.Task>): CategorySummaryBottomSheet {
            val fragment = CategorySummaryBottomSheet()
            val args = Bundle()
            args.putSerializable(ARG_TASKS, tasks)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_category_summary, container, false)
        recyclerView = view.findViewById(R.id.rv_category_summary)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val tasks = arguments?.getSerializable(ARG_TASKS) as? List<com.example.voicecommandaiapp.model.Task>
        
        if (!tasks.isNullOrEmpty()) {
            val categoryCounts = tasks.groupingBy { 
                it.category?.takeIf { c -> c.isNotBlank() } ?: "Other" 
            }.eachCount()
            
            val categorySummaries = categoryCounts.map { category ->
                val icon = when (category.key) {
                    "Work" -> R.drawable.ic_work_category
                    "Personal" -> R.drawable.ic_personal_category
                    "Health" -> R.drawable.ic_health_category
                    "Shopping" -> R.drawable.ic_shopping_category
                    "Learning" -> R.drawable.ic_learning_category
                    else -> R.drawable.ic_other_category
                }
                CategorySummary(category.key, icon, category.value)
            }
            adapter = CategorySummaryAdapter(categorySummaries)
            recyclerView.adapter = adapter
        } else {
             // Empty state handling if needed, or just empty list
             adapter = CategorySummaryAdapter(emptyList())
             recyclerView.adapter = adapter
        }

        return view
    }
}
