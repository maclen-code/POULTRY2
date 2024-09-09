package com.example.poultry2.ui.dashboard.global.acct.acctDashboard.summary


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.poultry2.data.Data
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.FragmentSalesBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SalesFragment : Fragment(){

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private var acctNo=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSalesBinding.inflate(inflater, container, false)

        val args = arguments
        if (args!!.containsKey("acctNo")) acctNo = args.getString("acctNo").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Filter.updated.observe(viewLifecycleOwner
        ) {
            show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun show(){
        binding.progress.visibility=View.VISIBLE
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            val vm =
                ViewModelProvider(this@SalesFragment)[SovViewModel::class.java]


            val listSovBunitCategory = vm.sovBunitCategory(
                Filter.cid,Filter.dates.from, Filter.dates.to, Filter.transType,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                acctNo
            )


            scopeMainThread.launch {
                val headers1= listOf("CATEGORY","VOLUME","AMOUNT","LAST YEAR","GROWTH",
                    "LAST MONTH","GROWTH")

                Table.createHeader("",headers1,binding.table1)
                table1(binding.table1,listSovBunitCategory)
                binding.progress.visibility=View.GONE
            }

        }
    }

    private fun table1( table: TableLayout, list:List<Data.SovBunitCategory>){
        val context=table.context


        list.groupBy { it.bunitId }
            .map {item ->
                Data.SovBunitVolume(item.key,item.value.maxOf { it.bunit},
                    item.value.sumOf { it.volume} )
            }.sortedByDescending { it.volume }.forEach { t->

                table1SubTotal(table,t.bunit, list.filter { it.bunitId==t.bunitId})
                list.filter { it.bunitId==t.bunitId}
                    .sortedByDescending { it.totalNet }.forEach {item ->


                        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)


                        val row = TableRow(context)
                        table.addView(row)
                        row.addView(Table.cell(context,item.category, Gravity.START,textColor,true))
                        row.addView(Table.cell(context,
                            Utils.formatDoubleToString(item.volume,0),
                            Gravity.END,textColor))
                        row.addView(Table.cell(context,
                            Utils.formatDoubleToString(item.totalNet,0),
                            Gravity.END,textColor))

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

                addSeparator(table)
            }
        table1Total(table,"TOTAL", list)
    }
    private fun table1SubTotal( table: TableLayout,label:String, list:List<Data.SovBunitCategory>){
        val context=table.context
        val lastYearVolume=list.sumOf { it.lastYearVolume }
        val lastMonthVolume=list.sumOf { it.lastMonthVolume }
        val volume=list.sumOf { it.volume }
        val totalNet=list.sumOf { it.totalNet }


        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

        val row = TableRow(context)
        table.addView(row)

        row.addView(
            Table.subCell(context,label, Gravity.START,textColor,true
            ))


        row.addView(Table.subCell(context, Utils.formatDoubleToString(volume,0),
            Gravity.END,textColor))

        row.addView(Table.subCell(context, Utils.formatDoubleToString(totalNet,0),
            Gravity.END,textColor))

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
    private fun table1Total( table: TableLayout,label:String, list:List<Data.SovBunitCategory>){
        val context=table.context
        val lastYearVolume=list.sumOf { it.lastYearVolume }
        val lastMonthVolume=list.sumOf { it.lastMonthVolume }
        val volume=list.sumOf { it.volume }
        val totalNet=list.sumOf { it.totalNet }

        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)


        val row = TableRow(context)
        table.addView(row)

        row.addView(
            Table.subCell(context,label, Gravity.START,textColor,true
            ))


        row.addView(Table.subCell(context, Utils.formatDoubleToString(volume,0),
            Gravity.END,textColor))

        row.addView(Table.subCell(context, Utils.formatDoubleToString(totalNet,0),
            Gravity.END,textColor))


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

    private fun addSeparator(table: TableLayout){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        val row = TableRow(context)
        table.addView(row)
        row.addView(Table.cell(context,"", Gravity.START,textColor,true,13))
    }
}








