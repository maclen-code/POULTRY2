package com.example.poultry2.ui.dashboard.global.customer

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TableLayout
import android.widget.TableRow
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemDashBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import java.util.ArrayList
import java.util.Locale

class DashCustomerAdapter internal constructor()
    : RecyclerView.Adapter<DashCustomerAdapter.ViewHolder>() {

    private var list = emptyList<Data.SovDashCustomer>() // Cached copy of words
    private var filteredList = emptyList<Data.SovDashCustomer>()

    var onItemClick: ((Data.SovDashCustomer)->Unit) ?= null
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

            Table.createHeader(current.customer,
                listOf("VOLUME","AMOUNT","LAST YEAR", "GROWTH",
                    "LAST MONTH","GROWTH"),binding.table1,true)

            table1(binding.table1,current.sovCustomer)

            if (current.listAccountArSummary.isNotEmpty()) {
                Table.createHeader(
                    "", listOf(
                        "BALANCE TYPE", "CURRENT",
                        "1 - 15", "16 - 30", "ABOVE 30", "TOTAL"
                    ), binding.table2
                )

                table2( binding.table2, current.listAccountArSummary)
            }

            binding.table1.setOnClickListener {
                onItemClick?.invoke(current)
            }
            binding.table2.setOnClickListener {
                onItemClick?.invoke(current)
            }
        }
    }

    private fun table1( table: TableLayout
                       , item:Data.SovCustomer){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

            val row = TableRow(context)
            table.addView(row)

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.volume),
                    Gravity.END ,textColor,true))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.totalNet),
                Gravity.END ,textColor))

            row.addView(Table.cell(context,  Utils.formatDoubleToString(item.lastYearVolume),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastYearVolume),
                Gravity.END,textColor))

            row.addView(Table.cell(context,  Utils.formatDoubleToString(item.lastMonthVolume),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastMonthVolume),
                Gravity.END,textColor))



    }


    private fun table2( table: TableLayout, list:List<Data.CustomerArSummary>){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        list.forEach { item ->

            val row = TableRow(context)

            row.addView(
                Table.cell(context,item.balanceType, Gravity.START,textColor,true))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a1,0),
                Gravity.END))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a2,0),
                Gravity.END))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a3,0),
                Gravity.END))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a4,0),
                Gravity.END))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.total,0),
                Gravity.END))

            table.addView(row)
        }

    }
    internal fun setData(data: List<Data.SovDashCustomer>) {
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
                    val filteredList: MutableList<Data.SovDashCustomer> = ArrayList()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.customer.lowercase(Locale.ROOT)
                                .contains(charString.lowercase(Locale.ROOT))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    this@DashCustomerAdapter.filteredList = filteredList
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
                filteredList = filterResults.values as List<Data.SovDashCustomer>
                notifyDataSetChanged()
            }
        }
    }
}