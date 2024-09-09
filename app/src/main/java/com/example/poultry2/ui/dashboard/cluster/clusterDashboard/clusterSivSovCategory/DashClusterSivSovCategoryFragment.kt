package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterSivSovCategory


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


class DashClusterSivSovCategoryFragment : Fragment(){

    private var _binding: FragmentDashSivSovBinding? = null
    private val binding get() = _binding!!
    private var clusterId=-1
    private var cluster=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashSivSovBinding.inflate(inflater, container, false)
        val args = arguments
        clusterId = args!!.getInt("clusterId",0)
        cluster = args.getString("cluster","")
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
            val sivVm =
                ViewModelProvider(this@DashClusterSivSovCategoryFragment)[SivViewModel::class.java]

            val listSivSovClusterCategory=sivVm.sivSovClusterCategory(Filter.cid,Filter.dates.from,
                Filter.dates.to,clusterId).toMutableList()


            scopeMainThread.launch {

                Table.createHeader("",listOf("CATEGORY","SIV VOL","SOV VOL","%",
                    "SIV AMT","SOV AMT","%"),
                    binding.table1)

                table1(binding.table1,listSivSovClusterCategory.sortedByDescending { it.sovVolume })
                if (listSivSovClusterCategory.size>1)
                    table1Total(binding.table1,listSivSovClusterCategory)

                binding.progress.visibility=View.GONE
            }
        }
    }

    private fun table1(table: TableLayout, list:List<Data.SivSovClusterCategory>){
        val context=table.context
        list.sortedBy {it.category} .forEach { item ->
            var volumePercent=0.0
            var amountPercent=0.0

            if (item.sivVolume>0)  volumePercent=(item.sovVolume/item.sivVolume)*100
            if (item.sivAmount>0)  amountPercent=(item.sovAmount/item.sivAmount)*100

            val par= Utils.par()
            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
            if (volumePercent>0 && (volumePercent<par || amountPercent<par))
                textColor=ContextCompat.getColor(context, R.color.textWarning)

            val row = TableRow(context)
            table.addView(row)

            row.addView(Table.cell(context,item.category,Gravity.START,textColor,true))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sivVolume,0     ),Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sovVolume,0     ),Gravity.END,textColor))

            var strVolumePercent="-"
            if (volumePercent>0) strVolumePercent= Utils.formatDoubleToString(volumePercent) + " %"
            row.addView(Table.cell(context,strVolumePercent,Gravity.END,textColor,false,1, Typeface.BOLD))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sivAmount,0),Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sovAmount,0),Gravity.END,textColor))

            var strAmountPercent="-"
            if (amountPercent>0) strAmountPercent= Utils.formatDoubleToString(amountPercent) + " %"
            row.addView(Table.cell(context,strAmountPercent,Gravity.END,textColor,false,1, Typeface.BOLD))


        }
    }

    private fun table1Total(table: TableLayout, list:List<Data.SivSovClusterCategory>){

        val sivVolume=list.sumOf { it.sivVolume }
        val sivAmount=list.sumOf { it.sivAmount }
        val sovVolume=list.sumOf { it.sovVolume }
        val sovAmount=list.sumOf { it.sovAmount }


        val context=table.context


        var volumePercent=0.0
        var amountPercent=0.0

        if (sivVolume>0)  volumePercent=(sovVolume/sivVolume)*100
        if (sivAmount>0)  amountPercent=(sovAmount/sivAmount)*100

        val par= Utils.par()
        var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

        if (volumePercent>0 && (volumePercent<par || amountPercent<par))
            textColor=ContextCompat.getColor(context, R.color.textWarning)

        val row = TableRow(context)
        table.addView(row)

        row.addView(Table.subCell(context,"TOTAL",Gravity.START,textColor,true))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sivVolume,0     ),Gravity.END,textColor))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sovVolume,0     ),Gravity.END,textColor))

        var strVolumePercent="-"
        if (volumePercent>0) strVolumePercent= Utils.formatDoubleToString(volumePercent) + " %"
        row.addView(Table.subCell(context,strVolumePercent,Gravity.END,textColor,false,1, Typeface.BOLD))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sivAmount,0),Gravity.END,textColor))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sovAmount,0),Gravity.END,textColor))

        var strAmountPercent="-"
        if (amountPercent>0) strAmountPercent= Utils.formatDoubleToString(amountPercent) + " %"
        row.addView(Table.subCell(context,strAmountPercent,Gravity.END,textColor,false,1, Typeface.BOLD))

    }

}





