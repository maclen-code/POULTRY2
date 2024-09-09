package com.example.poultry2.ui.dashboard.global.product

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemDashBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import java.util.ArrayList
import java.util.Locale

class DashProductAdapter internal constructor()
    : RecyclerView.Adapter<DashProductAdapter.ViewHolder>() {
    private var lastLevel=false
    private var list = emptyList<Data.SovDashProduct>() // Cached copy of words
    private var filteredList = emptyList<Data.SovDashProduct>()

    var onItemClick: ((Data.SovDashProduct)->Unit) ?= null
    var onCellClick: ((String,Map<String,String>)->Unit) ?= null
    var onDashClick: ((String,Map<String,String>)->Unit) ?= null

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

            var headers= listOf("CODE","ITEM","VOLUME","AMOUNT","",
                "ORDERED","UNIVERSE", "VARIANCE","", "%","LAST YEAR","GROWTH","LAST MONTH",
                "GROWTH")

            if (lastLevel)
                headers= listOf("CODE","ITEM","VOLUME","AMOUNT",
                    "LAST YEAR","GROWTH","LAST MONTH",
                    "GROWTH")

            Table.createHeader(current.category,headers,binding.table1)

            table1(binding.table1,current.listSovProduct)
            if (current.listSovProduct.size>1)
                table1Subtotal(binding.table1,current.sovCategory!!)
        }
    }

    private fun table1( table: TableLayout, list:List<Data.SovProduct>){
        val context=table.context
        list.sortedByDescending { it.volume }.forEach {item ->

//            val par= Utils.par()
            val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

            val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100

//            if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
//                textColor=ContextCompat.getColor(context, R.color.textWarning)
            val row = TableRow(context)
            table.addView(row)

            row.addView( Table.cell(context,item.itemCode, Gravity.START,textColor,true))

            row.addView( Table.cell(context,item.itemDesc, Gravity.START,textColor))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.volume),
                Gravity.END,textColor))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.totalNet),
                Gravity.END,textColor))

            if (!lastLevel) {

                val imgVolume=Table.icon(context)
                row.addView(imgVolume)
                if (item.volume > 0) {
                    imgVolume.setOnClickListener {
                        val map = mutableMapOf<String, String>()
                        map["itemCode"] = item.itemCode
                        map["itemDesc"] = item.itemDesc
                        onCellClick?.invoke("volume", map)
                    }
                }


                row.addView(
                    Table.cell(
                        context, Utils.formatIntToString(item.ordered),
                        Gravity.END, textColor
                    )
                )

                row.addView(
                    Table.cell(
                        context, Utils.formatIntToString(item.universe),
                        Gravity.END, textColor
                    )
                )


                val variance = item.ordered - item.universe

                row.addView(
                    Table.cell(
                        context, Utils.formatIntToString(variance),
                        Gravity.END, textColor
                    )
                )

                val imgUba = Table.icon(context)
                row.addView(imgUba)
                if (variance < 0) {
                    imgUba.setOnClickListener {
                        val map = mutableMapOf<String, String>()
                        map["itemCode"] = item.itemCode
                        map["itemDesc"] = item.itemDesc
                        onCellClick?.invoke("uba", map)
                    }
                }

                var strOrderPercent="-"
                if (orderedPercent>0)  strOrderPercent=Utils.formatDoubleToString(orderedPercent) + " %"
                row.addView(Table.cell(context, strOrderPercent, Gravity.END,textColor))
            }



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
    }

    private fun table1Subtotal( table: TableLayout, item:Data.SovCategory){
        val context=table.context

        val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100

        val par= Utils.par()
        var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
            textColor=ContextCompat.getColor(context, R.color.textWarning)

        val row = TableRow(context)
        table.addView(row)

        row.addView(
            Table.subCell(context,"TOTAL", Gravity.START,textColor,true,2
            ))


        row.addView(Table.subCell(context, Utils.formatDoubleToString(item.volume,0),
            Gravity.END,textColor))

        row.addView(Table.subCell(context, Utils.formatDoubleToString(item.totalNet,0),
            Gravity.END,textColor))

        if (!lastLevel) {
            val imgVolume = Table.icon(context)
            row.addView(imgVolume)

            if (item.volume != 0.0) {
                imgVolume.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["catId"] = list[0].catId
                    map["category"] = list[0].category
                    onCellClick?.invoke("volume", map)
                }
            }

            row.addView(
                Table.subCell(
                    context, Utils.formatIntToString(item.ordered), Gravity.END,
                    textColor
                )
            )

            row.addView(
                Table.subCell(
                    context, Utils.formatIntToString(item.universe), Gravity.END,
                    textColor
                )
            )


            val variance = item.ordered - item.universe


            row.addView(
                Table.subCell(
                    context, Utils.formatIntToString(variance), Gravity.END,
                    textColor
                )
            )

            val imgUba = Table.icon(context)
            row.addView(imgUba)

            if (variance < 0) {
                imgUba.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["catId"] = list[0].catId
                    map["category"] = list[0].category
                    onCellClick?.invoke("uba", map)
                }
            }

            var strOrderPercent = "-"
            if (orderedPercent > 0) strOrderPercent =
                Utils.formatDoubleToString(orderedPercent) + " %"
            row.addView(Table.subCell(context, strOrderPercent, Gravity.END, textColor))
        }

        row.addView(
            Table.subCell(context, Utils.formatDoubleToString(item.lastYearVolume,0),
                Gravity.END,textColor))

        row.addView(
            Table.subCell(context,
                Utils.formatDoubleToString(item.volume-item.lastYearVolume,0),
                Gravity.END,textColor
            ))

        row.addView(
            Table.subCell(context, Utils.formatDoubleToString(item.lastMonthVolume,0),
                Gravity.END,textColor
            ))

        row.addView(
            Table.subCell(context,
                Utils.formatDoubleToString(item.volume-item.lastMonthVolume,0),
                Gravity.END,textColor
            ))


    }


    internal fun setData(data: List<Data.SovDashProduct>,lastLevel:Boolean) {
        this.list = data
        this.filteredList=data
        this.lastLevel=lastLevel
        notifyDataSetChanged()
    }

    override fun getItemCount() = filteredList.size

    fun getFilter(): android.widget.Filter {
        return object : android.widget.Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredList = list
                } else {
                    val filteredList: MutableList<Data.SovDashProduct> = ArrayList()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.category.lowercase(Locale.ROOT)
                                .contains(charString.lowercase(Locale.ROOT))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    this@DashProductAdapter.filteredList = filteredList
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
                filteredList = filterResults.values as List<Data.SovDashProduct>
                notifyDataSetChanged()
            }
        }
    }
}