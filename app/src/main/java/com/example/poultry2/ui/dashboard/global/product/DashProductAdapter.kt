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

class DashProductAdapter internal constructor()
    : RecyclerView.Adapter<DashProductAdapter.ViewHolder>() {
    private var lastLevel=false
    private var data = emptyList<Data.SovDashProduct>() // Cached copy of words
    var onItemClick: ((Data.SovDashProduct)->Unit) ?= null
    var onCellClick: ((String,Map<String,String>)->Unit) ?= null
    var onDashClick: ((String,Map<String,String>)->Unit) ?= null

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

            var headers= listOf("","CODE","ITEM","","VOLUME","",
                "ORDERED","UNIVERSE", "VARIANCE","", "%","LAST YEAR","GROWTH","LAST MONTH",
                "GROWTH","UNIT")

            if (lastLevel)
                headers= listOf("","CODE","ITEM","VOLUME",
                    "LAST YEAR","GROWTH","LAST MONTH",
                    "GROWTH","UNIT")

            Table.createHeader(current.category,headers,binding.table1)

            table1(binding.table1,current.listSovProduct)

//            if (current.listSovProduct.size>1)
//                table1Subtotal(binding.table1,current.listSovCategory)

        }
    }

    private fun table1( table: TableLayout, list:List<Data.SovProduct>){
        val context=table.context
        var ctr=1
        list.sortedByDescending { it.totalNet }.forEach {item ->

            val par= Utils.par()
            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

            val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100

            if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
                textColor=ContextCompat.getColor(context, R.color.textWarning)
            val row = TableRow(context)
            row.addView( Table.cell(context,Utils.formatIntToString(ctr), Gravity.END,textColor,
                    true))

            row.addView( Table.cell(context,item.itemCode, Gravity.START,textColor))

            row.addView( Table.cell(context,item.itemDesc, Gravity.START,textColor))
            if (!lastLevel) {
                val imgDash=Table.icon(context, R.drawable.ic_open)
                row.addView(imgDash)
                imgDash.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["itemCode"] = item.itemCode
                    map["itemDesc"] = item.itemDesc
                    onDashClick?.invoke("volume", map)
                }
            }



            row.addView(Table.cell(context,Utils.formatDoubleToString(item.volume),
                Gravity.START,textColor))

            if (!lastLevel) {
                val imgVolume=Table.icon(context, R.drawable.ic_open)
                row.addView(imgVolume)
                if (item.volume > 0) {
                    imgVolume.setOnClickListener {
                        val map = mutableMapOf<String, String>()
                        map["itemCode"] = item.itemCode
                        map["itemDesc"] = item.itemDesc
                        onCellClick?.invoke("volume", map)
                    }
                }
            }
            if (!lastLevel) {
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

                val imgUba = Table.icon(context, R.drawable.ic_open)
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

            row.addView( Table.cell(context,item.volumeUnit, Gravity.START,textColor))

            table.addView(row)
            ctr++
        }
    }


    internal fun setData(data: List<Data.SovDashProduct>,lastLevel:Boolean) {
        this.data = data
        this.lastLevel=lastLevel
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}