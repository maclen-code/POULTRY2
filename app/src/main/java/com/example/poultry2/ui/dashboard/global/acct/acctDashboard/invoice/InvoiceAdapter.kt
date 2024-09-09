package com.example.poultry2.ui.dashboard.global.acct.acctDashboard.invoice

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.print.PrintAttributes
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemDashBinding
import com.example.poultry2.ui.function.MyDate.toDateString
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils


class InvoiceAdapter internal constructor()
    : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    private var data = emptyList<Data.SovInvoice>() // Cached copy of words
    var onItemClick: ((Data.SovInvoice)->Unit) ?= null

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

            val context=binding.tvNo.context


            val headers= listOf("","CODE","ITEM","QTY","AMOUNT")
            Table.createHeader("",headers,binding.table1)
            invoiceHeader(binding.table1,current.invoiceNo,current.date)
            table1(binding.table1,current.listProduct)


        }
    }

    private fun invoiceHeader( table: TableLayout,invoiceNo:String,date:String){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorPrimary)
        val row = TableRow(context)
        row.addView(Table.customCell(context,invoiceNo,Gravity.START,textColor,
            2,Typeface.BOLD,14f,
            PrintAttributes.Margins(1,1,0,0)))

        row.addView(Table.customCell(context,date.toLocalDate()
            .toDateString("MMMM dd, yyyy"),Gravity.END,textColor,
            3,Typeface.BOLD,14f,
            PrintAttributes.Margins(0,1,1,0)))


        table.addView(row,0)
    }

    private fun table1(table: TableLayout, list:List<Data.SovInvoiceProduct>){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        var ctr=1
        list.sortedByDescending { it.itemCode }.forEach {item ->
            val row = TableRow(context)
            table.addView(row)
            row.addView(Table.cell(context,Utils.formatIntToString(ctr), Gravity.END,textColor,
                true))

            row.addView(Table.cell(context,item.itemCode, Gravity.START))

            row.addView(Table.cell(context,item.itemDesc, Gravity.START))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.volume),Gravity.END))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.totalNet),Gravity.END))
            ctr++
        }
        val rowTotal = TableRow(context)
        table.addView(rowTotal)
        rowTotal.addView(Table.subCell(context,"TOTAL", Gravity.START,textColor,
            true,3))
        rowTotal.addView(Table.subCell(context,
            Utils.formatDoubleToString(list.sumOf { it.volume }), Gravity.END))
        rowTotal.addView(Table.subCell(context,
            Utils.formatDoubleToString(list.sumOf { it.totalNet }), Gravity.END))
    }

    internal fun setData(data: List<Data.SovInvoice>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}