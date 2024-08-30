package com.example.poultry2.ui.dashboard.global.uba.ordered

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.poultry2.data.Data
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.ActivityOrderedBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderedBinding

    private var clusterId=-1
    private var cluster=""
    private var tradeCode=""
    private var rid=""
    private var dsp=""
    private var channel=""
    private var bunitId=""
    private var bunit=""
    private var catId=""
    private var category=""
    private var itemCode=""


    private val map = mutableMapOf<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title ="ORDERED"
        actionbar.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent.hasExtra("clusterId"))
            clusterId=intent.getStringExtra("clusterId").toString().toInt()

        if (intent.hasExtra("cluster")) cluster=intent.getStringExtra("cluster").toString()
        if (intent.hasExtra("tradeCode")) tradeCode=intent.getStringExtra("tradeCode").toString()
        if (intent.hasExtra("rid")) rid=intent.getStringExtra("rid").toString()
        if (intent.hasExtra("dsp")) dsp=intent.getStringExtra("dsp").toString()
        if (intent.hasExtra("channel")) channel=intent.getStringExtra("channel")
            .toString()
        if (intent.hasExtra("bunitId")) bunitId=intent.getStringExtra("bunitId")
            .toString()
        if (intent.hasExtra("bunit")) bunit=intent.getStringExtra("bunit")
            .toString()
        if (intent.hasExtra("itemCode")) itemCode=intent.getStringExtra("itemCode")
            .toString()
        if (intent.hasExtra("catId")) catId=intent.getStringExtra("catId")
            .toString()
        if (intent.hasExtra("category")) category=intent.getStringExtra("category")
            .toString()


        if (cluster!="") map["cluster"]=cluster
        if (tradeCode!="") map["trade Code"]=tradeCode
        if (dsp!="") map["dsp"]=dsp
        if (channel!="") map["channel"]=channel
        if (category!="") map["category"]=category
        if (itemCode!="") map["item code"]=itemCode
        map["date from"]=Filter.dates.from
        map["date to"]=Filter.dates.to

        show()

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun show(){
        binding.progress.visibility= View.VISIBLE
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            val vm =
                ViewModelProvider(this@OrderedActivity)[SovViewModel::class.java]

            val list=vm.sovOrdered(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.transType,
                clusterId,tradeCode,rid,channel,bunitId,catId,itemCode)

            scopeMainThread.launch {

                Table.showFilter(map,binding.table1)

                val headers= mutableListOf("NO","ACCOUNT","DSP","CHANNEL","VOLUME","AMOUNT")
                Table.createHeader("",headers,binding.table2)

                table2(binding.table2,list)
                table2Subtotal(binding.table2,list)
                binding.progress.visibility= View.GONE
            }
        }
    }


    private fun table2(table: TableLayout, list:List<Data.Ordered>){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        var ctr=1
        list.sortedByDescending { it.volume }.forEach {item ->
            val row = TableRow(context)
            table.addView(row)
            row.addView(Table.cell(context,Utils.formatIntToString(ctr), Gravity.END,
                textColor,true))

            row.addView(Table.cell(context, item.storeName, Gravity.START))
            row.addView(Table.cell(context, item.dsp, Gravity.START))
            row.addView(Table.cell(context, item.channel, Gravity.START))

            row.addView(Table.cell(context,  Utils.formatDoubleToString(item.volume), Gravity.END))

            row.addView(Table.cell(context,  Utils.formatDoubleToString(item.totalNet), Gravity.END))
            ctr++

        }
    }

    private fun table2Subtotal( table: TableLayout, list:List<Data.Ordered>){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        val volume=list.sumOf { it.volume }
        val amount=list.sumOf { it.totalNet }

        val row = TableRow(context)
        table.addView(row)

        row.addView(Table.cell(context,"TOTAL", Gravity.END,
            textColor,true,4))

        row.addView(Table.cell(context, Utils.formatDoubleToString(volume), Gravity.END,
            textColor,false,1,Typeface.BOLD))

        row.addView(Table.cell(context, Utils.formatDoubleToString(amount), Gravity.END,
            textColor,false,1,Typeface.BOLD))

    }

}