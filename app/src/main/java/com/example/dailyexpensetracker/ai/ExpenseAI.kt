package com.example.dailyexpensetracker.ai

object ExpenseAI {
    private val categories = mapOf(
        "food" to listOf("lunch","dinner","snacks","meal","restaurant"),
        "transport" to listOf("bus","rickshaw","uber","train","taxi"),
        "bills" to listOf("electricity","internet","water","gas"),
        "shopping" to listOf("clothes","shoes","dress","mall"),
        "entertainment" to listOf("movie","netflix","game","party")
    )

    fun detectCategoryAndAmount(input: String): Pair<Double, String> {
        var detectedCategory = "Other"
        for ((category, keywords) in categories) {
            if (keywords.any { input.contains(it, ignoreCase = true) }) {
                detectedCategory = category
                break
            }
        }
        val amountRegex = Regex("\\d+(\\.\\d+)?")
        val amount = amountRegex.find(input)?.value?.toDoubleOrNull() ?: 0.0
        return Pair(amount, detectedCategory)
    }

    fun costSavingTip(category: String): String {
        return when(category) {
            "food" -> "Cook at home more often to save on food costs."
            "transport" -> "Use public transport or walk for short trips."
            "bills" -> "Turn off unused appliances to reduce bills."
            "shopping" -> "Set a shopping budget before going out."
            "entertainment" -> "Choose free or low-cost entertainment."
            else -> "Track all small expenses—they add up!"
        }
    }
}
