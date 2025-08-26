package com.example.dailyexpensetracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyexpensetracker.databinding.ActivityReportBinding
import com.example.dailyexpensetracker.utils.ExpenseUtils
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Calendar

class ReportActivity : AppCompatActivity() {
    private lateinit var b: ActivityReportBinding
    private val vm: ExpenseViewModel by viewModels()

    companion object {
        fun intent(ctx: Context) = Intent(ctx, ReportActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityReportBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }

        // Current month range
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis

        vm.expensesBetween(start, end).observe(this) { list ->
            // Pie: totals by category
            val byCat = list.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
            val pieEntries = byCat.entries.map { PieEntry(it.value.toFloat(), it.key) }
            val pieSet = PieDataSet(pieEntries, "By Category")
            b.pieChart.data = PieData(pieSet)
            b.pieChart.description = Description().apply { text = "" }
            b.pieChart.setUsePercentValues(true)
            b.pieChart.invalidate()

            // Bar: totals by day
            val byDay = list.groupBy { ExpenseUtils.dayKey(it.date) }.toSortedMap()
            val barEntries = byDay.entries.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.sumOf { e -> e.amount }.toFloat())
            }
            val barSet = BarDataSet(barEntries, "Daily Spend")
            b.barChart.data = BarData(barSet)
            b.barChart.description = Description().apply { text = "" }
            b.barChart.xAxis.granularity = 1f
            b.barChart.invalidate()

            val total = list.sumOf { it.amount }
            b.tvTotal.text = "Monthly Total: ৳ ${"%.2f".format(total)}"
        }
    }
}
