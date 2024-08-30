package com.example.poultry2.ui.dashboard.global.ar.arAccount

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.poultry2.ui.dashboard.global.ar.arInvoice.ArInvoiceActivity
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.data.ar.ArViewModel
import com.example.poultry2.databinding.ActivityArAccountBinding
import com.example.poultry2.ui.function.Table
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArAccountBinding

    private var clusterId=-1
    private var cluster=""
    private var tradeCode=""
    private var rid=""
    private var dsp=""
    private var channel=""
    private var balanceType=""
    private var customerNo=""

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        balanceType=intent.getStringExtra("balanceType").toString()
       if (intent.hasExtra("clusterId"))
           clusterId=intent.getIntExtra("clusterId",0)
       if (intent.hasExtra("cluster"))
           cluster=intent.getStringExtra("cluster").toString()
       if (intent.hasExtra("tradeCode"))
           tradeCode=intent.getStringExtra("tradeCode").toString()
        if (intent.hasExtra("rid"))
            rid=intent.getStringExtra("rid").toString()
       if (intent.hasExtra("dsp"))
           dsp=intent.getStringExtra("dsp").toString()
        if (intent.hasExtra("channel"))
            channel=intent.getStringExtra("channel").toString()
       if (intent.hasExtra("customerNo"))
           customerNo=intent.getStringExtra("customerNo").toString()

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title =balanceType
        actionbar.setDisplayHomeAsUpEnabled(true)

        val map = mutableMapOf<String, String>()

        if (cluster!="") map["cluster"]=cluster
        if (tradeCode!="") map["trade Code"]=tradeCode
        if (dsp!="") map["dsp"]=dsp
        if (channel!="") map["channel"]=channel


       show(map)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun show(map:Map<String,String>){
        binding.progress.visibility= View.VISIBLE
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {

            val arVm =
                ViewModelProvider(this@ArAccountActivity)[ArViewModel::class.java]
            val listAccountArSummary=arVm.acctArSummary(Filter.cid,Filter.sno,clusterId,tradeCode,
                rid,balanceType)


            scopeMainThread.launch {
                Table.showFilter(map,binding.table1)
                showAccountAr(listAccountArSummary)
                binding.progress.visibility= View.GONE
            }
        }
    }

    private fun showAccountAr(listAccountArSummary:List<Data.AcctArSummary>){

        Table.createHeader(
            "", listOf("NO","ACCOUNT", "CURRENT","1 - 15", "16 - 30",
                "ABOVE 30", "TOTAL",""
            ), binding.table2
        )

        val context=binding.table2.context
        val size=14f
        val textColor=context.resolveColorAttr(android.R.attr.textColorSecondary)
        val typeFace=Typeface.NORMAL
        var ctr=1
        listAccountArSummary.sortedByDescending { it.total }.forEach { item->


            val row = TableRow(context)

            row.addView(Table.cell(context,Utils.formatIntToString(ctr),Gravity.END ,
                textColor,true,1,typeFace,size))

            row.addView(Table.cell(context,item.storeName,Gravity.START,textColor,
                false,1,typeFace,size))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a1,0),
                Gravity.END,textColor,false,1,typeFace,size))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a2,0),
                Gravity.END,textColor,false,1,typeFace,size))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a3,0),
                Gravity.END,textColor,false,1,typeFace,size))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.a4,0),
                Gravity.END,textColor,false,1,typeFace,size))

            row.addView(Table.cell(context, Utils.formatDoubleToString(item.total,0),
                Gravity.END,textColor,false,1,Typeface.BOLD,size))

            val img=Table.icon(context, R.drawable.ic_open)
            row.addView(img)

            img.setOnClickListener {
                val intent = Intent(this, ArInvoiceActivity::class.java)
                intent.putExtra("balanceType", balanceType)
                intent.putExtra("acctNo", item.acctNo)
                intent.putExtra("storeName", item.storeName)
                startActivity(intent)
            }

            binding.table2.addView(row)
            ctr++
        }
    }

}