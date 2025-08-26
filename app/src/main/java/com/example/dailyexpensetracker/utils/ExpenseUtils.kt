package com.example.dailyexpensetracker.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object ExpenseUtils {
    private val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val daySdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun now(): Long = System.currentTimeMillis()
    fun format(ts: Long): String = sdf.format(Date(ts))
    fun dayKey(ts: Long): String = daySdf.format(Date(ts))

    fun getDefaultCategories() = listOf("Food","Transport","Bills","Shopping","Entertainment","Other")

    // SharedPreferences for budget
    private const val PREF = "budget_prefs"
    private const val KEY_BUDGET = "monthly_budget"

    fun setMonthlyBudget(context: Context, amount: Double) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_BUDGET, amount.toFloat()).apply()
    }

    fun getMonthlyBudget(context: Context): Double {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getFloat(KEY_BUDGET, 0f).toDouble()
    }
}
