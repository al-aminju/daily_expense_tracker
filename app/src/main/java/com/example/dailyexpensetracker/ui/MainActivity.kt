package com.example.dailyexpensetracker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyexpensetracker.R
import com.example.dailyexpensetracker.data.Expense
import com.example.dailyexpensetracker.databinding.ActivityMainBinding
import com.example.dailyexpensetracker.utils.ExpenseUtils
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val vm: ExpenseViewModel by viewModels()
    private val adapter = ExpenseAdapter(onDelete = { vm.delete(it) })

    private val addLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* LiveData refreshes automatically */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)
        b.rvExpenses.layoutManager = LinearLayoutManager(this)
        b.rvExpenses.adapter = adapter

        vm.allExpenses.observe(this) { list ->
            adapter.submit(list)
            updateSummary(list)
        }

        b.fabAdd.setOnClickListener {
            addLauncher.launch(AddExpenseActivity.intent(this))
        }

        b.btnReport.setOnClickListener {
            startActivity(ReportActivity.intent(this))
        }

        b.btnBudget.setOnClickListener { showBudgetDialog() }
    }

    private fun updateSummary(list: List<Expense>) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val monthStart = calendar.timeInMillis
        val monthTotal = list.filter { it.date >= monthStart }.sumOf { it.amount }
        val budget = ExpenseUtils.getMonthlyBudget(this)
        b.tvMonthlyTotal.text = "This month: ৳ ${"%.2f".format(monthTotal)}"
        b.tvBudget.text = if (budget > 0) "Budget: ৳ ${"%.2f".format(budget)}" else "Budget: Not set"

        if (budget > 0 && monthTotal >= budget * 0.9) {
            b.tvBudgetWarning.text = if (monthTotal > budget)
                "⚠ Exceeded monthly budget!"
            else "⚠ Nearing budget limit."
            b.tvBudgetWarning.animate().alpha(1f).setDuration(250).start()
        } else {
            b.tvBudgetWarning.alpha = 0f
        }
    }

    private fun showBudgetDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "e.g., 10000"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        AlertDialog.Builder(this)
            .setTitle("Set Monthly Budget (৳)")
            .setView(input)
            .setPositiveButton("Save") { d, _ ->
                val value = input.text.toString().toDoubleOrNull() ?: 0.0
                ExpenseUtils.setMonthlyBudget(this, value)
                d.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_report) {
            startActivity(ReportActivity.intent(this))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
