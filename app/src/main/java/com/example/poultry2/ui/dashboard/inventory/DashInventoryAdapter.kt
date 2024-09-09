package com.example.poultry2.ui.dashboard.inventory

import android.annotation.SuppressLint
import android.graphics.Typeface

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.data.inventory.Inventory
import com.example.poultry2.databinding.ItemDashBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import java.util.ArrayList
import java.util.Locale

class DashInventoryAdapter internal constructor()
    : RecyclerView.Adapter<DashInventoryAdapter.ViewHolder>() {

    private var list = emptyList<Data.SovDashInventory>() // Cached copy of words
    private var filteredList = emptyList<Data.SovDashInventory>()

    var onItemClick: ((Data.SovDashInventory)->Unit) ?= null

    var onCellClick: ((String,Map<String,String>)->Unit) ?= null

    inner class ViewHolder(val binding: ItemDashBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(filteredList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = filteredList[position]

        with (holder) {
            binding.tvNo.text=Utils.formatIntToString(position+1) + "."


            val headers1= listOf("ITEM CODE","ITEM","ON-HAND",
                "ACTUAL","PURCHASE","GOOD\nSTOCKS","ROUTE\nRETURN","RECEIVED","TOTAL\nINV",
                "ROUTE\nISSUE","SALES\nPRE-SELL","PULL\nOUT","WAREHOUSE\nBO",)

            Table.createHeader(current.category,
                headers1,binding.table1)
            table1(binding.table1,current.listInventory)

            if (current.listInventory.size>1)
                table1Total(binding.table1,current.listInventory)
        }
    }





    private fun table1( table: TableLayout, list:List<Inventory>){
        val context=table.context

        list.sortedBy { it.itemCode }.forEach { item ->
            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
            if (item.onHand <= 0)
                textColor = ContextCompat.getColor(context, R.color.textWarning)
            val row = TableRow(context)
            table.addView(row)


            row.addView(Table.cell(context, item.itemCode, Gravity.START, textColor,true))
            row.addView(Table.cell(context, item.itemDesc, Gravity.START, textColor))
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.onHand), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.actual), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.purchase), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.goodStocks), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.routeReturn), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.received), Gravity.END, textColor
                )
            )
            val totalInv=item.actual+item.purchase+item.goodStocks+item.routeReturn+item.received
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(totalInv), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.routeIssue), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.salesPresell), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.pullOut), Gravity.END, textColor
                )
            )
            row.addView(
                Table.cell(
                    context,
                    Utils.formatDoubleToString(item.warehouseBo), Gravity.END, textColor
                )
            )
        }
    }

    private fun table1Total(table: TableLayout, list:List<Inventory>){
        val context=table.context
        val onHand=list.sumOf { it.onHand }
        val actual=list.sumOf { it.actual }
        val purchase=list.sumOf { it.purchase }
        val goodStocks=list.sumOf { it.goodStocks }
        val routeReturn=list.sumOf { it.routeReturn }
        val received=list.sumOf { it.received }
        val routeIssue=list.sumOf { it.routeIssue }
        val salesPresell=list.sumOf { it.salesPresell }
        val pullOut=list.sumOf { it.pullOut }
        val warehouseBo=list.sumOf { it.warehouseBo }


        var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        if (onHand <= 0)
            textColor = ContextCompat.getColor(context, R.color.textWarning)
        val row = TableRow(context)
        table.addView(row)

            row.addView(Table.subCell(context, "TOTAL", Gravity.START, textColor,
                true,2))

        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(onHand), Gravity.END, textColor
            )
        )

        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(actual), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(purchase), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(goodStocks), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(routeReturn), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(received), Gravity.END, textColor
            )
        )
        val totalInv=actual+purchase+goodStocks+routeReturn+received
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(totalInv), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(routeIssue), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(salesPresell), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(pullOut), Gravity.END, textColor
            )
        )
        row.addView(
            Table.subCell(
                context,
                Utils.formatDoubleToString(warehouseBo), Gravity.END, textColor
            )
        )
    }

    internal fun setData(data: List<Data.SovDashInventory>) {
        this.list = data
        this.filteredList=data
        notifyDataSetChanged()
    }

    override fun getItemCount() =filteredList.size

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredList = list
                } else {
                    val filteredList: MutableList<Data.SovDashInventory> = ArrayList()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.category.lowercase(Locale.ROOT)
                                .contains(charString.lowercase(Locale.ROOT))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    this@DashInventoryAdapter.filteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                @Suppress("UNCHECKED_CAST")
                filteredList = filterResults.values as List<Data.SovDashInventory>
                notifyDataSetChanged()
            }
        }
    }
}