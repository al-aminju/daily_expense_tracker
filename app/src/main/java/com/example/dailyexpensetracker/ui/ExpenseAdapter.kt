package com.example.dailyexpensetracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpensetracker.data.Expense
import com.example.dailyexpensetracker.databinding.ItemExpenseBinding
import com.example.dailyexpensetracker.utils.ExpenseUtils

class ExpenseAdapter(
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.VH>() {

    private val items = mutableListOf<Expense>()

    fun submit(list: List<Expense>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemExpenseBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.b.tvCategory.text = e.category
        holder.b.tvDesc.text = e.description.ifBlank { "—" }
        holder.b.tvAmount.text = "৳ ${"%.2f".format(e.amount)}"
        holder.b.tvDate.text = ExpenseUtils.format(e.date)
        holder.b.btnDelete.setOnClickListener { onDelete(e) }
    }

    override fun getItemCount() = items.size
}
