package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterDsp

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

class DashClusterDspAdapter internal constructor()
    : RecyclerView.Adapter<DashClusterDspAdapter.ViewHolder>() {

    private var data = emptyList<Data.SovDashClusterDsp>() // Cached copy of words

    var onItemClick: ((Data.SovDashClusterDsp)->Unit) ?= null
    var onCellClick: ((String,Map<String,String>)->Unit) ?= null
    var onDspTradeClick: ((Map<String,String>)->Unit) ?= null

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

            val headers1=listOf("PRODUCT","VOLUME","TARGET","VARIANCE","%")
            Table.createHeader(current.dsp,headers1,binding.table1,true
            )
            table1(binding.table1,current.listSovDspBunit,current.target)

            val headers2=listOf("TRADE","VOLUME","AMOUNT","","ORDERED","UNIVERSE","VARIANCE","","%",
                "LAST YEAR","GROWTH","LAST MONTH","GROWTH","")
            Table.createHeader(
                Table.headerTitle(current.dsp,headers2[0]),headers2,binding.table2,true
            )

            table2(binding.table2,current.listSovDspTrade)
            if (current.listSovDspTrade.size > 1)
                table2Subtotal(binding.table2,current.listSovDspTrade)

            val headers3= listOf("BUNIT","VOLUME","AMOUNT","", "ORDERED", "UNIVERSE", "VARIANCE","",
                "%","LAST YEAR","GROWTH","LAST MONTH","GROWTH")
            Table.createHeader(
                Table.headerTitle(current.dsp,headers3[0]),headers3, binding.table3,true
            )

            table3(binding.table3, current.listSovDspBunit,current.target)


            binding.table1.setOnClickListener {
                onItemClick?.invoke(current)
            }
            binding.table2.setOnClickListener {
                onItemClick?.invoke(current)
            }
            binding.table3.setOnClickListener {
                onItemClick?.invoke(current)
            }

            binding.table3.setOnClickListener {
                onItemClick?.invoke(current)
            }

        }

    }

    private fun table1( table: TableLayout, list:List<Data.SovDspBunit>,
                        target:Data.TargetDsp){
        val context=table.context

        val par= Utils.par()
        var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
//        if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
//            textColor=ContextCompat.getColor(context, R.color.textWarning)
        //////////////////////poultry//////////////////////////////////////////////////////////
        val volume=list.sumOf { it.volume}
        var volumeVariance=0.0
        var percentVolume=0.0
        if (target.volumeTarget>0) {
            volumeVariance=volume-target.volumeTarget
            percentVolume=(volume/target.volumeTarget) * 100
        }
        var percentVolumeStr=""
        if (percentVolume>0) percentVolumeStr="${Utils.formatDoubleToString(percentVolume)} %"

        val row = TableRow(context)
        table.addView(row)
        row.addView(Table.cell(context,"POULTRY VOLUME", Gravity.START,textColor,true))
        row.addView(Table.cell(context,
            Utils.formatDoubleToString(volume,0),
            Gravity.END,textColor))
        row.addView(Table.cell(context,
            Utils.formatIntToString(target.volumeTarget),
            Gravity.END,textColor))


        row.addView(Table.cell(context,
            Utils.formatDoubleToString(volumeVariance,0),
            Gravity.END,textColor))
        row.addView(Table.cell(context, percentVolumeStr, Gravity.END,textColor))

        ///////////////////////////////////////////////////////////////////////////////////////////
        val amount=list.sumOf { it.totalNet}
        var amountVariance=0.0
        var percentAmount=0.0
        if (target.amountTarget>0){
            amountVariance=amount-target.amountTarget
            percentAmount=(amount/target.amountTarget) * 100
        }
        var percentAmountStr=""
        if (percentAmount>0) percentAmountStr="${ Utils.formatDoubleToString(percentAmount)} %"

        val row1 = TableRow(context)
        table.addView(row1)
        row1.addView(Table.cell(context,"POULTRY AND SMIS AMOUNT", Gravity.START,textColor,true))
        row1.addView(Table.cell(context,
            Utils.formatDoubleToString(amount,0),
            Gravity.END,textColor))
        row1.addView(Table.cell(context,
            Utils.formatIntToString(target.amountTarget),
            Gravity.END,textColor))
        row1.addView(Table.cell(context,
            Utils.formatDoubleToString(amountVariance,0),
            Gravity.END,textColor))
        row1.addView(Table.cell(context, percentAmountStr, Gravity.END,textColor))

    }


    private fun table2( table: TableLayout, list:List<Data.SovTradeDsp>){
        val context=table.context
        list.sortedByDescending { it.totalNet }.forEach {item ->

            val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100
            val par= Utils.par()

            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

            if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
                textColor=ContextCompat.getColor(context, R.color.textWarning)

            val row = TableRow(context)
            table.addView(row)
            row.addView(Table.cell(context,item.tradeCode, Gravity.START,textColor,true))
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
                    map["tradeCode"] = item.tradeCode
                    map["rid"] = item.rid
                    map["dsp"] = item.dsp
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
                    map["tradeCode"] = item.tradeCode
                    map["rid"] = item.rid
                    map["dsp"] = item.dsp
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

            val imgDspTrade=Table.icon(context)
            row.addView(imgDspTrade)

            imgDspTrade.setOnClickListener {
                val map = mutableMapOf<String, String>()
                map["tradeCode"] = item.tradeCode
                map["rid"] = item.rid
                map["dsp"] = item.dsp
                onDspTradeClick?.invoke(map)
            }



        }
    }

    private fun table2Subtotal( table: TableLayout, list:List<Data.SovTradeDsp>){
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
                map["rid"] =  list[0].rid
                map["dsp"] =  list[0].dsp
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
                map["rid"] =  list[0].rid
                map["dsp"] =  list[0].dsp
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

        row.addView(
            Table.subCell(context, "",  Gravity.END,textColor
            ))

    }

    private fun table3( table: TableLayout, list:List<Data.SovDspBunit>,
                        target:Data.TargetDsp){
        val context=table.context

        val pt= listOf("P","S")
        pt.forEach { p->
            list.filter { it.productType==p}.sortedByDescending { it.totalNet }.forEach {item ->

                val orderedPercent: Double = (item.ordered.toDouble()/item.universe)*100
                val par= Utils.par()

                var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

                if ((par<80 && orderedPercent<par) || (par>=80 && orderedPercent<80.0))
                    textColor=ContextCompat.getColor(context, R.color.textWarning)

                val row = TableRow(context)
                table.addView(row)
                row.addView(Table.cell(context,item.bunit, Gravity.START,textColor,true))
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
                        map["productType"]=item.productType
                        map["rid"] = item.rid
                        map["dsp"] = item.dsp
                        map["bunitId"] = item.bunitId
                        map["bunit"] = item.bunit
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
                        map["productType"]=item.productType
                        map["rid"] = item.rid
                        map["dsp"] = item.dsp
                        map["bunitId"] = item.bunitId
                        map["bunit"] = item.bunit
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
    }

    internal fun setData(data: List<Data.SovDashClusterDsp>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}