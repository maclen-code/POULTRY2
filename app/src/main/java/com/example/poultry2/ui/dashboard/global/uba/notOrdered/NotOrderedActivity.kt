package com.example.poultry2.ui.dashboard.global.uba.notOrdered

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.data.sovSmis.SovSmisViewModel
import com.example.poultry2.databinding.ActivityNotOrderedBinding
import com.example.poultry2.ui.dashboard.global.acct.acctDashboard.AccountDashboardActivity
import com.example.poultry2.ui.function.MyDate.toDateString
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotOrderedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotOrderedBinding

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
    private var itemDesc=""
    private var productType=""

    private val map = mutableMapOf<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotOrderedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title ="NOT ORDERED"
        actionbar.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent.hasExtra("clusterId"))
            clusterId=intent.getStringExtra("clusterId").toString().toInt()

        if (intent.hasExtra("cluster")) cluster=intent.getStringExtra("cluster")
            .toString()
        if (intent.hasExtra("tradeCode")) tradeCode=intent.getStringExtra("tradeCode")
            .toString()
        if (intent.hasExtra("rid")) rid=intent.getStringExtra("rid").toString()
        if (intent.hasExtra("dsp")) dsp=intent.getStringExtra("dsp").toString()
        if (intent.hasExtra("channel")) channel=intent.getStringExtra("channel")
            .toString()
        if (intent.hasExtra("bunitId")) bunitId=intent.getStringExtra("bunitId")
            .toString()
        if (intent.hasExtra("bunit")) bunit=intent.getStringExtra("bunit")
            .toString()
        if (intent.hasExtra("catId")) catId=intent.getStringExtra("catId")
            .toString()
        if (intent.hasExtra("category")) category=intent.getStringExtra("category")
            .toString()

        if (intent.hasExtra("itemCode")) itemCode=intent.getStringExtra("itemCode")
            .toString()

        if (intent.hasExtra("itemDesc")) itemDesc=intent.getStringExtra("itemDesc")
            .toString()

        if (intent.hasExtra("productType")) productType=intent.getStringExtra("productType")
            .toString()

        if (cluster!="") map["cluster"]=cluster
        if (tradeCode!="") map["trade Code"]=tradeCode
        if (dsp!="") map["dsp"]=dsp
        if (channel!="") map["channel"]=channel
        if (bunit!="") map["bunit"]=bunit
        if (category!="") map["category"]=category
        if (itemCode!="") map["itemCode"]=itemCode
        if (itemDesc!="") map["item Desc"]=itemDesc
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
                ViewModelProvider(this@NotOrderedActivity)[SovViewModel::class.java]
            val vmSmis =
                ViewModelProvider(this@NotOrderedActivity)[SovSmisViewModel::class.java]

            val list: List<Data.NotOrdered> = if (productType=="" || productType=="P")
                vm.sovNotOrdered(
                    Filter.cid, Filter.dates.from, Filter.dates.to,
                    Filter.dates.universeFrom,Filter.transType,clusterId,tradeCode,rid, channel,
                    bunitId, catId,itemCode)
            else
                vmSmis.sovSmisNotOrdered(
                    Filter.cid, Filter.dates.from, Filter.dates.to,
                    Filter.dates.universeFrom,Filter.transType,clusterId,rid, channel,
                    bunitId)

            scopeMainThread.launch {


                Table.showFilter(map,binding.table1)

                val headers= mutableListOf("NO","ACCOUNT","DSP","CHANNEL","LAST ORDER")
                Table.createHeader("",headers,binding.table2)

                table2(binding.table2,list)
                binding.progress.visibility= View.GONE
            }
        }
    }

    private fun table2( table: TableLayout, list:List<Data.NotOrdered>){
        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        var ctr=1
        list.sortedByDescending { it.lastOrdered }.forEach {item ->
            val row = TableRow(context)

            row.addView(Table.cell(context,Utils.formatIntToString(ctr), Gravity.END,
                textColor,true))

            row.addView(Table.cell(context, item.storeName, Gravity.START))

            row.addView(Table.cell(context, item.dsp, Gravity.START))

            row.addView(Table.cell(context, item.channel, Gravity.START))

            val lastOrdered= item.lastOrdered.toLocalDate().toDateString("MMM dd, yyyy")

            row.addView(Table.cell(context, lastOrdered, Gravity.START))


            ctr++
            table.addView(row)
        }
    }

}