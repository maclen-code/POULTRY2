package com.example.poultry2.ui.dashboard.sivSov


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
import java.time.LocalDate


class DashSivSovFragment : Fragment(),UpdateTargetDialog.DialogListener{

    private var _binding: FragmentDashSivSovBinding? = null
    private val binding get() = _binding!!
    private lateinit var selected:Data.SivSovCluster

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashSivSovBinding.inflate(inflater, container, false)

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
                ViewModelProvider(this@DashSivSovFragment)[SivViewModel::class.java]

            val listSivSovCluster=sivVm.sivSovCluster(Filter.cid,Filter.dates.from,
                Filter.dates.to).toMutableList()

            val listVolumeClusterTradeType=sivVm.volumeClusterTradeType(Filter.cid,Filter.dates.from,
                Filter.dates.to).toMutableList()


//            if (Filter.dates.from!= LocalDate.now().monthFirstDate()
//                ||  Filter.dates.to.toLocalDate()!= LocalDate.now()) {
//                val daysFrom=Filter.dates.from.toLocalDate().dayOfMonth
//                val daysTo=Filter.dates.to.toLocalDate().dayOfMonth
//                val totalDays=Filter.dates.to.toLocalDate().lengthOfMonth()
//                val period =daysTo-daysFrom+1
//                val percent=period.toDouble()/totalDays
//                listSivSovCluster.forEach {
//                    it.sivVolumeTarget=(it.sivVolumeTarget*percent).toInt()
//                    it.sivAmountTarget=(it.sivAmountTarget*percent).toInt()
//                    it.sovVolumeTarget=(it.sovVolumeTarget*percent).toInt()
//                }
//            }



            scopeMainThread.launch {

                Table.createHeader("",listOf("CLUSTER","SIV VOL","TARGET","","%",
                    "SIV AMT","TARGET","%",
                    "SOV VOL","TARGET","%","SOV AMT","SOV vs SIV\n(VOL)","SOV vs SIV\n(AMT)","PROMO","PROMO vs\nSOV AMT"),
                    binding.table1)

                table1(binding.table1,listSivSovCluster.sortedByDescending { it.sovVolume })
                if (listSivSovCluster.size>1)
                    table1Total(binding.table1,listSivSovCluster)


                table2Header(listVolumeClusterTradeType)
                table2(binding.table2, listSivSovCluster.filter { it.cluster!="TOTAL" }
                    .sortedByDescending { it.sovVolume }, listVolumeClusterTradeType)

                if (listSivSovCluster.size>1)
                    table2Total(binding.table2, listVolumeClusterTradeType )
                binding.progress.visibility=View.GONE
            }
        }
    }

    private fun table1(table: TableLayout, list:List<Data.SivSovCluster>){
        val context=table.context



        list.forEach { item ->
            var sivVolumePercent=0.0
            var sivAmountPercent=0.0
            var sovVolumePercent=0.0

            if (item.sivVolumeTarget>0)  sivVolumePercent=(item.sivVolume/item.sivVolumeTarget)*100
            if (item.sivAmountTarget>0)  sivAmountPercent=(item.sivAmount/item.sivAmountTarget)*100
            if (item.sovVolumeTarget>0)  sovVolumePercent=(item.sovVolume/item.sovVolumeTarget)*100



            val par= Utils.par()
            var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)


            if ((sivVolumePercent<par) || (sivAmountPercent<par) || (sovVolumePercent<par ))
                textColor=ContextCompat.getColor(context, R.color.textWarning)

            val row = TableRow(context)
            table.addView(row)

            row.addView(Table.cell(context,item.cluster,Gravity.START,textColor,true))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sivVolume,0     ),Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatIntToString(item.sivVolumeTarget),Gravity.END,textColor))

            val imgSivTarget=Table.icon(context)
            row.addView(imgSivTarget)

            imgSivTarget.setOnClickListener {
                selected=item
                val args = Bundle()
                args.putString("source", "fragment")
                args.putString("title",item.cluster)
                args.putInt("volumeTarget",item.sivVolumeTarget)
                args.putInt("amountTarget",item.sivAmountTarget)

                val updateTargetDialog = UpdateTargetDialog()
                updateTargetDialog.arguments = args
                updateTargetDialog.show(
                    childFragmentManager, "target"
                )
            }


            var strSivVolumePercent="-"
            if (sivVolumePercent>0) strSivVolumePercent= Utils.formatDoubleToString(sivVolumePercent) + " %"
            row.addView(Table.cell(context,strSivVolumePercent,Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sivAmount,0),Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatIntToString(item.sivAmountTarget),Gravity.END,textColor))

            var strSivAmountPercent="-"
            if (sivAmountPercent>0) strSivAmountPercent= Utils.formatDoubleToString(sivAmountPercent) + " %"
            row.addView(Table.cell(context,strSivAmountPercent,Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sovVolume,0),
                Gravity.END,textColor,true)
            )
            row.addView(Table.cell(context,
                Utils.formatIntToString(item.sovVolumeTarget),Gravity.END,textColor))

            var strSovVolumePercent="-"
            if (sovVolumePercent>0) strSovVolumePercent= Utils.formatDoubleToString(sovVolumePercent) + " %"
            row.addView(Table.cell(context,strSovVolumePercent,Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.sovAmount,0),
                Gravity.END,textColor,true)
            )

            var sivSovVolumePercent=0.0
            var sivSovAmountPercent=0.0
            if (item.sivVolume>0)  sivSovVolumePercent=(item.sovVolume/item.sivVolume)*100
            if (item.sivAmount>0)  sivSovAmountPercent=(item.sovAmount/item.sivAmount)*100

            var strSivSovVolumePercent="-"
            if (sivSovVolumePercent>0) strSivSovVolumePercent= Utils.formatDoubleToString(sivSovVolumePercent) + " %"
            row.addView(Table.cell(context,strSivSovVolumePercent,Gravity.END,textColor))

            var strSivSovAmountPercent="-"
            if (sivSovAmountPercent>0) strSivSovAmountPercent= Utils.formatDoubleToString(sivSovAmountPercent) + " %"
            row.addView(Table.cell(context,strSivSovAmountPercent,Gravity.END,textColor))

            row.addView(Table.cell(context,
                Utils.formatDoubleToString(item.promoDiscount,0),
                Gravity.END,textColor,true)
            )

            var promoDiscPercent=0.0
            if (item.sovAmount>0)  promoDiscPercent=(item.promoDiscount/item.sovAmount)*100
            var strPromoPercent="-"
            if (promoDiscPercent>0) strPromoPercent= Utils.formatDoubleToString(promoDiscPercent) + " %"
            row.addView(Table.cell(context,strPromoPercent,Gravity.END,textColor))


        }
    }

    private fun table1Total(table: TableLayout, list:List<Data.SivSovCluster>){

        val sivVolume=list.sumOf { it.sivVolume }
        val sivAmount=list.sumOf { it.sivAmount }
        val sivVolumeTarget=list.sumOf { it.sivVolumeTarget }
        val sivAmountTarget=list.sumOf { it.sivAmountTarget }
        val sovVolume=list.sumOf { it.sovVolume }
        val sovAmount=list.sumOf { it.sovAmount }
        val sovVolumeTarget=list.sumOf { it.sovVolumeTarget }
        val promoDiscount=list.sumOf { it.promoDiscount }

        val context=table.context


        var sivVolumePercent=0.0
        var sivAmountPercent=0.0
        var sovVolumePercent=0.0

        if (sivVolumeTarget>0)  sivVolumePercent=(sivVolume/sivVolumeTarget)*100
        if (sivAmountTarget>0)  sivAmountPercent=(sivAmount/sivAmountTarget)*100
        if (sovVolumeTarget>0)  sovVolumePercent=(sivVolume/sivVolumeTarget)*100

        val par= Utils.par()
        var textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

        if ((sivVolumePercent<par) || (sivAmountPercent<par) || (sovVolumePercent<par ))
            textColor=ContextCompat.getColor(context, R.color.textWarning)


        val row = TableRow(context)
        table.addView(row)

        row.addView(Table.subCell(context,"TOTAL",Gravity.START,textColor,true))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sivVolume,2),Gravity.END,textColor))

        row.addView(Table.subCell(context,
            Utils.formatIntToString(sivVolumeTarget),Gravity.END,textColor))

        row.addView(Table.subCell(context,"",Gravity.END,textColor))

        var strSivVolumePercent="-"
        if (sivVolumePercent>0) strSivVolumePercent= Utils.formatDoubleToString(sivVolumePercent) + " %"
        row.addView(Table.subCell(context,strSivVolumePercent,Gravity.END,textColor,false,1, Typeface.BOLD))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sivAmount,0),Gravity.END,textColor))

        row.addView(Table.subCell(context,
            Utils.formatIntToString(sivAmountTarget),Gravity.END,textColor))

        var strSivAmountPercent="-"
        if (sivAmountPercent>0) strSivAmountPercent= Utils.formatDoubleToString(sivAmountPercent) + " %"
        row.addView(Table.subCell(context,strSivAmountPercent,Gravity.END,textColor,false,
            1, Typeface.BOLD))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sovVolume,0),
            Gravity.END,textColor,true)
        )
        row.addView(Table.subCell(context,
            Utils.formatIntToString(sovVolumeTarget),Gravity.END,textColor))

        var strSovVolumePercent="-"
        if (sovVolumePercent>0) strSovVolumePercent= Utils.formatDoubleToString(sovVolumePercent) + " %"
        row.addView(Table.subCell(context,strSovVolumePercent,Gravity.END,textColor,false,
            1, Typeface.BOLD))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(sovAmount,0),
            Gravity.END,textColor,true)
        )

        var sivSovVolumePercent=0.0
        var sivSovAmountPercent=0.0
        if (sivVolume>0)  sivSovVolumePercent=(sovVolume/sivVolume)*100
        if (sivAmount>0)  sivSovAmountPercent=(sovAmount/sivAmount)*100

        var strSivSovVolumePercent="-"
        if (sivSovVolumePercent>0) strSivSovVolumePercent= Utils.formatDoubleToString(sivSovVolumePercent) + " %"
        row.addView(Table.subCell(context,strSivSovVolumePercent,Gravity.END,textColor))

        var strSivSovAmountPercent="-"
        if (sivSovAmountPercent>0) strSivSovAmountPercent= Utils.formatDoubleToString(sivSovAmountPercent) + " %"
        row.addView(Table.subCell(context,strSivSovAmountPercent,Gravity.END,textColor))

        row.addView(Table.subCell(context,
            Utils.formatDoubleToString(promoDiscount,0),
            Gravity.END,textColor,true)
        )

        var promoDiscPercent=0.0
        if (sovAmount>0)  promoDiscPercent=(promoDiscount/sovAmount)*100
        var strPromoPercent="-"
        if (promoDiscPercent>0) strPromoPercent= Utils.formatDoubleToString(promoDiscPercent) + " %"
        row.addView(Table.subCell(context,strPromoPercent,Gravity.END,textColor))
    }

    private fun table2Header( listVolumeClusterTradeType: List<Data.SivSovClusterTradeType>) {

        val listHeaders = mutableListOf<String>()
        listHeaders.add("CLUSTER")
        listHeaders.add("SP")
        listHeaders.add("REG")
        listHeaders.add("%")
        listVolumeClusterTradeType.distinctBy { it.tradeType}.forEach { item ->
            listHeaders.add("SOV")
            listHeaders.add("SIV")
            listHeaders.add("%")
        }
        Table.createHeader("" ,listHeaders,binding.table2)

        val context=binding.table2.context
        val textColor:Int= ContextCompat.getColor(context, R.color.header_textColor)

        val row = TableRow(context)
        row.addView(Table.header(context,"", Gravity.CENTER, true,
            textColor,1))
        row.addView(Table.header(context,"SIV", Gravity.CENTER, true,
            textColor,3))

        val map= mutableMapOf<Int,String>()

        listVolumeClusterTradeType.distinctBy { it.tradeType}.forEach { item ->
                when(item.tradeType) {
                    "REG" -> map[0]=item.tradeType
                    "SP" -> map[1]=item.tradeType
                    "FS" -> map[2]=item.tradeType
                    "NMS" -> map[3]=item.tradeType
                    "NMS KAG" -> map[4]=item.tradeType
                }
        }
        map.forEach { entry ->
            row.addView(Table.header(context,entry.value, Gravity.CENTER, false,
                textColor,3))
        }


        binding.table2.addView(row,0)
    }

    private fun table2(table: TableLayout,listCluster:List<Data.SivSovCluster>
                       , list:List<Data.SivSovClusterTradeType>){
        val map= mutableMapOf<Int,String>()
        var tradeCount=0
        list.distinctBy { it.tradeType}.forEach { item ->
            when(item.tradeType) {
                "REG" -> map[0]=item.tradeType
                "SP" -> map[1]=item.tradeType
                "FS" -> map[2]=item.tradeType
                "NMS" -> map[3]=item.tradeType
                "NMS KAG" -> map[4]=item.tradeType
            }
            tradeCount++
        }

        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        listCluster.forEach { c ->
            val row = TableRow(context)
            table.addView(row)
            row.addView(Table.cell(context, c.cluster, Gravity.START, textColor, true))

            var temp=list.firstOrNull{it.cluster==c.cluster && it.transType=="SIV" && it.tradeType=="SP"}
            var spSiv=0.0
            if (temp!=null) spSiv=temp.volume

            row.addView(Table.cell(context,Utils.formatDoubleToString(spSiv,0),
                Gravity.END))

            temp=list.firstOrNull{it.cluster==c.cluster && it.transType=="SIV" && it.tradeType=="REG"}
            var regSiv=0.0
            if (temp!=null) regSiv=temp.volume

            row.addView(Table.cell(context,Utils.formatDoubleToString(regSiv,0),
                Gravity.END))

            var sivSpRegPercent=0.0
            if (regSiv>0)  sivSpRegPercent=(spSiv/regSiv)*100
            var strSivSpRegPercent=""
            if (sivSpRegPercent>0) strSivSpRegPercent=Utils.formatDoubleToString(sivSpRegPercent) + " %"
            row.addView(Table.cell(context,strSivSpRegPercent,Gravity.END,textColor))

            map.forEach { type ->
                var volumeTemp=list.firstOrNull{it.cluster==c.cluster && it.transType=="SOV" && it.tradeType==type.value}
                var sov=0.0
                if (volumeTemp!=null) sov=volumeTemp.volume

                row.addView(Table.cell(context,Utils.formatDoubleToString(sov,0),
                    Gravity.END))


                volumeTemp=list.firstOrNull{it.cluster==c.cluster && it.transType=="SIV" && it.tradeType==type.value}
                var siv=0.0
                if (volumeTemp!=null) siv=volumeTemp.volume

                row.addView(Table.cell(context,Utils.formatDoubleToString(siv,0),
                    Gravity.END))

                var percent=0.0
                if (siv>0)  percent=(sov/siv)*100
                var strPercent=""
                if (percent>0) strPercent=Utils.formatDoubleToString(percent) + " %"
                row.addView(Table.cell(context,strPercent,Gravity.END,textColor))
            }

        }
    }

    private fun table2Total(table: TableLayout, list:List<Data.SivSovClusterTradeType>){
        val map= mutableMapOf<Int,String>()

        list.distinctBy { it.tradeType}.forEach { item ->
            when(item.tradeType) {
                "REG" -> map[0]=item.tradeType
                "SP" -> map[1]=item.tradeType
                "FS" -> map[2]=item.tradeType
                "NMS" -> map[3]=item.tradeType
                "NMS KAG" -> map[4]=item.tradeType
            }
        }

        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)

        val row = TableRow(context)
        table.addView(row)
        row.addView(Table.subCell(context,"TOTAL", Gravity.START, textColor, true))


        val spSiv=list.filter{it.transType=="SIV" && it.tradeType=="SP"}.sumOf { it.volume }
        row.addView(Table.subCell(context,Utils.formatDoubleToString(spSiv,0),
            Gravity.END))


        val regSiv=list.filter{it.transType=="SIV" && it.tradeType=="REG"}.sumOf { it.volume }
        row.addView(Table.subCell(context,Utils.formatDoubleToString(regSiv,0),
                Gravity.END))

        var sivSpRegPercent=0.0
        if (regSiv>0)  sivSpRegPercent=(spSiv/regSiv)*100
        var strSivSpRegPercent=""
        if (sivSpRegPercent>0) strSivSpRegPercent=Utils.formatDoubleToString(sivSpRegPercent) + " %"
        row.addView(Table.subCell(context,strSivSpRegPercent,Gravity.END,textColor,false,
            1,Typeface.BOLD))

        map.forEach { type ->
            val sov=list.filter{it.transType=="SOV" && it.tradeType==type.value}.sumOf { it.volume }
            row.addView(Table.subCell(context,Utils.formatDoubleToString(sov,0),
                    Gravity.END))

            val siv=list.filter{it.transType=="SIV" && it.tradeType==type.value}.sumOf { it.volume }
            row.addView(Table.subCell(context,Utils.formatDoubleToString(siv,0),
                    Gravity.END))

            var percent=0.0
            if (siv>0)  percent=(sov/siv)*100
            var strPercent=""
            if (percent>0) strPercent=Utils.formatDoubleToString(percent) + " %"
            row.addView(Table.subCell(context,strPercent,Gravity.END,textColor,false,
                1,Typeface.BOLD))
            }

    }

    override fun onFinishSetTargetDialog(volumeTarget: Int, amountTarget: Int) {
        val job = Job()
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            val vm =
                ViewModelProvider(this@DashSivSovFragment)[SivTargetViewModel::class.java]

            val sivTarget= SivTarget(Filter.cid,selected.clusterId,
                Filter.dates.from.toLocalDate().monthFirstDate(),
                volumeTarget,amountTarget,false)
            vm.insert(sivTarget)
            Filter.updated.postValue(true)
        }
    }

}





