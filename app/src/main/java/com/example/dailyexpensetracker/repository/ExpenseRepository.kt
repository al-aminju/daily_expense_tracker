package com.example.dailyexpensetracker.repository

import androidx.lifecycle.LiveData
import com.example.dailyexpensetracker.data.Expense
import com.example.dailyexpensetracker.data.ExpenseDao

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    // ⬇️ This is what your ViewModel calls
    fun expensesBetween(start: Long, end: Long): LiveData<List<Expense>> =
        expenseDao.getExpensesBetween(start, end)

    suspend fun insert(expense: Expense) = expenseDao.insert(expense)
    suspend fun delete(expense: Expense) = expenseDao.delete(expense)
}
