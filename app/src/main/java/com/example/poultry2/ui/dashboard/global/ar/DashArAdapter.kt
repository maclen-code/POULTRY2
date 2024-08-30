package com.example.poultry2.ui.dashboard.global.ar

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemDashBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils

class DashArAdapter internal constructor() : RecyclerView.Adapter<DashArAdapter.ViewHolder>() {

    private var data = emptyList<Data.ArSummary>() // Cached copy of words
    var onItemClick: ((Data.ArSummary)->Unit) ?= null

    inner class ViewHolder(val binding: ItemDashBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]

        with (holder) {
            binding.tvNo.text=Utils.formatIntToString(position+1) + "."


            Table.createHeader(
                current.balanceType, listOf("CURRENT","1 - 15 DAYS", "16 - 30 DAYS",
                    "ABOVE 30 DAYS", "TOTAL"
                ), binding.table1,true
            )
            table1(binding.table1, current)

            binding.table1.setOnClickListener {
                onItemClick?.invoke(current)
            }
        }
    }

    private fun table1( table: TableLayout, item:Data.ArSummary){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        val row = TableRow(context)

        row.addView(Table.cell(context, Utils.formatDoubleToString(item.a1,0),
                Gravity.END ,textColor, true))

        row.addView(Table.cell(context, Utils.formatDoubleToString(item.a2,0),
            Gravity.END ,textColor))

        row.addView(Table.cell(context, Utils.formatDoubleToString(item.a3,0),
            Gravity.END ,textColor))

        row.addView(Table.cell(context, Utils.formatDoubleToString(item.a4,0),
            Gravity.END ,textColor))

        row.addView(Table.cell(context, Utils.formatDoubleToString(item.total,0),
            Gravity.END ,textColor))


        table.addView(row)
    }

    internal fun setData(data: List<Data.ArSummary>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}