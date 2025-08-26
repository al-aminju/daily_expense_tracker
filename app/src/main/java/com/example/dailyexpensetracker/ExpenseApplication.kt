package com.example.dailyexpensetracker

import android.app.Application
import com.example.dailyexpensetracker.data.ExpenseDatabase
import com.example.dailyexpensetracker.repository.ExpenseRepository

class ExpenseApplication : Application() {
    val database by lazy { ExpenseDatabase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.expenseDao()) }
}
