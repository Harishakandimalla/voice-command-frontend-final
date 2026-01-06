package com.example.voicecommandaiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.R

data class Category(val name: String, val taskCount: Int, val iconRes: Int)

class CategoriesActivity : AppCompatActivity() {

    private lateinit var categoriesAdapter: CategoriesAdapter
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_categories)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Categories"

        val categories = listOf(
            Category("Work", 12, R.drawable.ic_work_category),
            Category("Personal", 8, R.drawable.ic_personal_category),
            Category("Health", 5, R.drawable.ic_health_category),
            Category("Shopping", 3, R.drawable.ic_shopping_category),
            Category("Learning", 7, R.drawable.ic_learning_category)
        )
        selectedCategory = intent.getStringExtra("SELECTED_CATEGORY")

        val recyclerView = findViewById<RecyclerView>(R.id.rv_categories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoriesAdapter = CategoriesAdapter(categories, selectedCategory) {
            category -> selectedCategory = category.name
        }
        recyclerView.adapter = categoriesAdapter

        findViewById<View>(R.id.btn_apply_category).setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("SELECTED_CATEGORY", selectedCategory)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class CategoriesAdapter(
    private val categories: List<Category>,
    private var selectedCategory: String?,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, category.name == selectedCategory)
        holder.itemView.setOnClickListener {
            selectedCategory = category.name
            onCategorySelected(category)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val categoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        private val taskCount: TextView = itemView.findViewById(R.id.tv_task_count)
        private val selectedButton: Button = itemView.findViewById(R.id.btn_selected)

        fun bind(category: Category, isSelected: Boolean) {
            categoryIcon.setImageResource(category.iconRes)
            categoryName.text = category.name
            taskCount.text = "${category.taskCount} tasks"
            selectedButton.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
    }
}
