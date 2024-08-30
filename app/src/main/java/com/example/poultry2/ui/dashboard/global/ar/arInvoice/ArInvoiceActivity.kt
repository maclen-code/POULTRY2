package com.example.poultry2.ui.dashboard.global.ar.arInvoice

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.poultry2.data.Data
import com.example.poultry2.data.ar.ArViewModel
import com.example.poultry2.databinding.ActivityArInvoiceBinding
import com.example.poultry2.ui.function.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArInvoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArInvoiceBinding
    private var balanceType=""
    private var acctNo=""
    private var storeName=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        balanceType=intent.getStringExtra("balanceType").toString()
        acctNo=intent.getStringExtra("acctNo").toString()
        storeName=intent.getStringExtra("storeName").toString()

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title ="AR Invoice"
        actionbar.setDisplayHomeAsUpEnabled(true)

        binding.tvStoreName.text=storeName
        binding.tvBalanceType.text=balanceType


        show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun show(){
        binding.progress.visibility= View.VISIBLE
        val adapter= ArInvoiceAdapter(this)

        binding.rvList.setAdapter(adapter)

        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            val vm = ViewModelProvider(this@ArInvoiceActivity)[ArViewModel::class.java]
            val listAr=vm.arInvoice(acctNo,balanceType)
            scopeMainThread.launch {
                val listAging=listAr.groupBy { it.agingId}
                    .map {item ->
                        Data.ArAging(item.key,item.value.maxOf { it.aging } ,
                            item.value.sumOf{ it.balance} )
                    }.sortedBy { it.agingId }

                val hashMapItem =HashMap<String,List<Data.ArInvoice>>()


                listAging.forEach { a ->

                    val list=listAr.filter { it.aging==a.aging }
                        .sortedWith(compareBy(Data.ArInvoice::date)
                            .thenBy(Data.ArInvoice::invoiceNo))

                    hashMapItem[a.aging]=list
                }

                adapter.setData(listAging, hashMapItem)


                adapter.onItemClick= { item ->
                    item.isChecked=!item.isChecked
                    adapter.notifyDataSetChanged()

                    val hashMap=adapter.getDataList()

                    var totalCountSelected=0
                    var totalBalanceSelected=0.0
                    hashMap.forEach { m ->
                        totalCountSelected+=m.value.filter { it.isChecked }.size
                        totalBalanceSelected+=m.value.filter { it.isChecked }.sumOf { it.balance }
                    }

                    binding.tvCollectedCount.text= Utils.formatIntToString(totalCountSelected)
                    binding.tvCollectedAmount.text=Utils.formatDoubleToString(totalBalanceSelected)
                }

                binding.progress.visibility= View.GONE
            }
        }
    }



}