package com.example.dailyexpensetracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyexpensetracker.ai.ExpenseAI
import com.example.dailyexpensetracker.data.Expense
import com.example.dailyexpensetracker.databinding.ActivityAddExpenseBinding
import com.example.dailyexpensetracker.utils.ExpenseUtils

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var b: ActivityAddExpenseBinding
    private val vm: ExpenseViewModel by viewModels()

    companion object {
        fun intent(ctx: Context) = Intent(ctx, AddExpenseActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }

        val cats = ExpenseUtils.getDefaultCategories()
        b.spCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cats)

        b.btnAiDetect.setOnClickListener {
            val text = b.etAi.text.toString().trim()
            if (text.isBlank()) {
                Toast.makeText(this, "Type something like: 'Lunch 150'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val (amount, category) = ExpenseAI.detectCategoryAndAmount(text)
            if (amount > 0) b.etAmount.setText(amount.toString())
            val idx = cats.indexOfFirst { it.equals(category, true) }.takeIf { it >= 0 } ?: cats.indexOf("Other")
            if (idx >= 0) b.spCategory.setSelection(idx)
            b.etDesc.setText(text)
            b.tvTip.text = "Tip: ${ExpenseAI.costSavingTip(category)}"
        }

        b.btnSave.setOnClickListener {
            val amount = b.etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val category = b.spCategory.selectedItem?.toString() ?: "Other"
            val desc = b.etDesc.text.toString().trim()
            if (amount <= 0.0) {
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val exp = Expense(amount = amount, category = category, description = desc, date = ExpenseUtils.now())
            vm.add(exp)

            // Unwanted expense warning (simple rule: non-essential & budget pressure)
            val nonEssential = category in listOf("Entertainment","Shopping")
            val budget = ExpenseUtils.getMonthlyBudget(this)
            if (nonEssential && budget > 0) {
                Toast.makeText(this, "Warning: Unwanted expense? Consider skipping non-essentials.", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
