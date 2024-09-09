package com.example.poultry2.ui.dashboard.global.customer.customerDashboard.summary


import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.data.siv.SivViewModel
import com.example.poultry2.data.sivTarget.SivTarget
import com.example.poultry2.data.sivTarget.SivTargetViewModel
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.FragmentDashSivSovBinding
import com.example.poultry2.ui.function.MyDate.monthFirstDate
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.global.target.UpdateTargetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate


class CustomerSummaryFragment : Fragment(){

    private var _binding: FragmentDashSivSovBinding? = null
    private val binding get() = _binding!!

    private var customerNo=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashSivSovBinding.inflate(inflater, container, false)
        val args = arguments
        if (args!!.containsKey("customerNo")) customerNo = args.getString("customerNo").toString()

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
                ViewModelProvider(this@CustomerSummaryFragment)[SovViewModel::class.java]


            val listSovAcct=vm.sovAcct(
                Filter.cid ,Filter.dates.from, Filter.dates.to, Filter.transType,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,-1,"",
                "",customerNo)


            scopeMainThread.launch {

                Table.createHeader("LIST OF ACCOUNTS",
                    listOf("STORE NAME","VOLUME","AMOUNT","LAST YEAR", "GROWTH",
                        "LAST MONTH","GROWTH"),binding.table1,true)

                table1(binding.table1,listSovAcct)
                binding.progress.visibility=View.GONE
                table2Subtotal(binding.table1,listSovAcct)
            }
        }
    }

    private fun table1( table: TableLayout
                        , list:List<Data.SovAcct>){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

        list.forEach { item ->

            val row = TableRow(context)
            table.addView(row)

            row.addView(Table.cell(context,item.storeName ,Gravity.START ,textColor,true))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.volume,0),
                Gravity.END ,textColor))

            row.addView(Table.cell(context,Utils.formatDoubleToString(item.totalNet,0),
                Gravity.END ,textColor))

            row.addView(Table.cell(context,  Utils.formatDoubleToString(item.lastYearVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastYearVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,  Utils.formatDoubleToString(item.lastMonthVolume,0),
                Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.volume-item.lastMonthVolume,0),
                Gravity.END,textColor))
        }

    }


    private fun table2Subtotal( table: TableLayout, list:List<Data.SovAcct>){
        val context=table.context
        val lastYearVolume=list.sumOf { it.lastYearVolume }
        val lastMonthVolume=list.sumOf { it.lastMonthVolume }
        val volume=list.sumOf { it.volume }
        val totalNet=list.sumOf { it.totalNet }



        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)


        val row = TableRow(context)
        table.addView(row)

        row.addView(
            Table.subCell(context,"TOTAL", Gravity.START,textColor,true
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

}





