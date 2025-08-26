package com.example.dailyexpensetracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyexpensetracker.ExpenseApplication
import com.example.dailyexpensetracker.data.Expense
import kotlinx.coroutines.launch

class ExpenseViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as ExpenseApplication).repository
    val allExpenses = repo.allExpenses

    fun add(expense: Expense) = viewModelScope.launch { repo.insert(expense) }
    fun delete(expense: Expense) = viewModelScope.launch { repo.delete(expense) }

    fun expensesBetween(start: Long, end: Long): LiveData<List<Expense>> = repo.expensesBetween(start, end)
}
