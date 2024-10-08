package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterDsp.clusterDspDashboard.clusterDspChannel

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

class DashClusterDspChannelAdapter internal constructor()
    : RecyclerView.Adapter<DashClusterDspChannelAdapter.ViewHolder>() {

    private var data = emptyList<Data.SovDashClusterDspChannel>() // Cached copy of words

    var onItemClick: ((Data.SovDashClusterDspChannel)->Unit) ?= null
    var onCellClick: ((String,Map<String,String>)->Unit) ?= null

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



            val headers1=listOf("CHANNEL","VOLUME","AMOUNT","","ORDERED","UNIVERSE","VARIANCE","","%",
                "LAST YEAR","GROWTH","LAST MONTH","GROWTH")
            Table.createHeader(
                Table.headerTitle(current.tradeCode,headers1[0]),headers1,binding.table1,true
            )

            table1(binding.table1, current.listSovDspClusterChannel)
            if (current.listSovDspClusterChannel.size > 1)
                table1Subtotal(binding.table1,current.listSovDspClusterChannel)


//            val headers2= listOf("CATEGORY","VOLUME","AMOUNT","", "ORDERED", "UNIVERSE", "VARIANCE","",
//                "%","LAST YEAR","GROWTH","LAST MONTH","GROWTH")
//            Table.createHeader(
//                Table.headerTitle(current.channel,headers2[0]),headers2, binding.table2,true
//            )
//
//            table2(binding.table2, current.listSovChannelCategory)



            binding.table1.setOnClickListener {
                onItemClick?.invoke(current)
            }
            binding.table2.setOnClickListener {
                onItemClick?.invoke(current)
            }

        }

    }

    private fun table1(table: TableLayout, list: List<Data.SovTradeChannel>){
        val context=table.context
        list.sortedByDescending { it.volume }.forEach {item ->

            val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100
            val par= Utils.par()

            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

            if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
                textColor=ContextCompat.getColor(context, R.color.textWarning)

            val row = TableRow(context)
            table.addView(row)
            row.addView(Table.cell(context,item.channel, Gravity.START,textColor,true))
            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume,0),
                Gravity.END,textColor))
            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.totalNet,0),
                Gravity.END,textColor))

            val imgVolume=Table.icon(context)
            row.addView(imgVolume)

            if (item.volume>0) {
                imgVolume.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["channel"] = item.channel
                    map["tradeCode"] = item.tradeCode
                    onCellClick?.invoke("volume",map)
                }
            }


            row.addView(Table.cell(context, Utils.formatIntToString(item.ordered),
                Gravity.END,textColor))

            row.addView(Table.cell(context, Utils.formatIntToString(item.universe),
                Gravity.END,textColor))


            val variance=item.ordered-item.universe
            row.addView(Table.cell(context, Utils.formatIntToString(variance),
                Gravity.END,textColor))

            val imgUba=Table.icon(context)
            row.addView(imgUba)

            if (variance<0) {
                imgUba.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["channel"] = item.channel
                    map["tradeCode"] = item.tradeCode

                    onCellClick?.invoke("uba",map)
                }
            }
            var strOrderPercent="-"
            if (orderedPercent>0)  strOrderPercent=Utils.formatDoubleToString(orderedPercent) + " %"
            row.addView(Table.cell(context, strOrderPercent, Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.lastYearVolume,0),
                Gravity.END,textColor))


            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastYearVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.lastMonthVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastMonthVolume,0),
                Gravity.END,textColor))


        }
    }

    private fun table1Subtotal( table: TableLayout, list:List<Data.SovTradeChannel>){
        val context=table.context
        val lastYearVolume=list.sumOf { it.lastYearVolume }
        val lastMonthVolume=list.sumOf { it.lastMonthVolume }
        val volume=list.sumOf { it.volume }
        val totalNet=list.sumOf { it.totalNet }
        val ordered=list.sumOf { it.ordered }
        val universe=list.sumOf { it.universe }


        val orderedPercent: Double = (ordered.toDouble()/universe)*100

        val par= Utils.par()
        var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
            textColor=ContextCompat.getColor(context, R.color.textWarning)

        val row = TableRow(context)
        table.addView(row)

        row.addView(
            Table.subCell(context,"TOTAL", Gravity.START,textColor,true
            ))


        row.addView(Table.subCell(context, Utils.formatDoubleToString(volume,0),
            Gravity.END,textColor))

        row.addView(Table.subCell(context, Utils.formatDoubleToString(totalNet,0),
            Gravity.END,textColor))

        val imgVolume=Table.icon(context)
        row.addView(imgVolume)

        if (volume!=0.0) {
            imgVolume.setOnClickListener {
                val map = mutableMapOf<String, String>()
                map["tradeCode"] =  list[0].tradeCode
                onCellClick?.invoke("volume",map)
            }
        }

        row.addView(
            Table.subCell(context, Utils.formatIntToString(ordered), Gravity.END,
                textColor))

        row.addView(
            Table.subCell(context, Utils.formatIntToString(universe), Gravity.END,
                textColor))


        val variance=ordered-universe


        row.addView(Table.subCell(context, Utils.formatIntToString(variance), Gravity.END,
            textColor))

        val imgUba=Table.icon(context)
        row.addView(imgUba)

        if (variance<0) {
            imgUba.setOnClickListener {
                val map = mutableMapOf<String, String>()
                map["tradeCode"] =  list[0].tradeCode
                onCellClick?.invoke("uba",map)
            }
        }

        var strOrderPercent="-"
        if (orderedPercent>0)  strOrderPercent=Utils.formatDoubleToString(orderedPercent) + " %"
        row.addView(Table.subCell(context, strOrderPercent, Gravity.END,textColor))

        row.addView(
            Table.subCell(context, Utils.formatDoubleToString(lastYearVolume,0),
                Gravity.END,textColor))

        row.addView(
            Table.subCell(context,
                Utils.formatDoubleToString(volume-lastYearVolume,0),
                Gravity.END,textColor
            ))

        row.addView(
            Table.subCell(context, Utils.formatDoubleToString(lastMonthVolume,0),
                Gravity.END,textColor
            ))

        row.addView(
            Table.subCell(context,
                Utils.formatDoubleToString(volume-lastMonthVolume,0),
                Gravity.END,textColor
            ))


    }
    private fun table2(table: TableLayout, list: List<Data.SovCategoryChannel>){
        val context=table.context
        list.sortedByDescending { it.totalNet }.forEach {item ->

            val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100
            val par= Utils.par()

            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

            if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
                textColor=ContextCompat.getColor(context, R.color.textWarning)

            val row = TableRow(context)
            table.addView(row)
            row.addView(Table.cell(context,item.category, Gravity.START,textColor,true))
            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume,0),
                Gravity.END,textColor))
            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.totalNet,0),
                Gravity.END,textColor))

            val imgVolume=Table.icon(context)
            row.addView(imgVolume)

            if (item.volume>0) {
                imgVolume.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["catId"] = item.catId
                    map["category"] = item.category
                    map["channel"] = item.channel
                    onCellClick?.invoke("volume",map)
                }
            }


            row.addView(Table.cell(context, Utils.formatIntToString(item.ordered),
                Gravity.END,textColor))

            row.addView(Table.cell(context, Utils.formatIntToString(item.universe),
                Gravity.END,textColor))


            val variance=item.ordered-item.universe
            row.addView(Table.cell(context, Utils.formatIntToString(variance),
                Gravity.END,textColor))

            val imgUba=Table.icon(context)
            row.addView(imgUba)

            if (variance<0) {
                imgUba.setOnClickListener {
                    val map = mutableMapOf<String, String>()
                    map["catId"] = item.catId
                    map["category"] = item.category
                    map["channel"] = item.channel
                    onCellClick?.invoke("uba",map)
                }
            }
            var strOrderPercent="-"
            if (orderedPercent>0)  strOrderPercent=Utils.formatDoubleToString(orderedPercent) + " %"
            row.addView(Table.cell(context, strOrderPercent, Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.lastYearVolume,0),
                Gravity.END,textColor))


            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastYearVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.lastMonthVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastMonthVolume,0),
                Gravity.END,textColor))


        }
    }

    internal fun setData(data: List<Data.SovDashClusterDspChannel>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}