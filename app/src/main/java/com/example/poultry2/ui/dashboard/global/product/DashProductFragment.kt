package com.example.poultry2.ui.dashboard.global.product


import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.FragmentDashBinding
import com.example.poultry2.databinding.FragmentDashCustomerBinding
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Locale


class DashProductFragment : Fragment(){

    private var _binding: FragmentDashCustomerBinding? = null
    private val binding get() = _binding!!
    private var clusterId=-1
    private var cluster=""
    private var tradeCode=""
    private var rid=""
    private var dsp=""
    private var channel=""
    private var acctNo=""
    private var storeName=""
    private var customerNo=""
    private var customer=""
    private lateinit var adapter: DashProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashCustomerBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId = args!!.getInt("clusterId",-1)
        cluster = args.getString("cluster","")
        tradeCode = args.getString("tradeCode","")
        rid = args.getString("rid","")
        dsp = args.getString("dsp","")
        channel = args.getString("channel","")
        acctNo = args.getString("acctNo","")
        storeName = args.getString("storeName","")
        customerNo = args.getString("customerNo","")
        customer = args.getString("customer","")

        adapter= DashProductAdapter()
        binding.rvList.adapter =adapter
        binding.rvList.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val divider = DividerItemDecoration(activity,DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.rvList.addItemDecoration(divider)

        binding.etFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.getFilter().filter(s.trim())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

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
                ViewModelProvider(this@DashProductFragment)[SovViewModel::class.java]


            var universeFrom=Filter.dates.universeFrom
            if (acctNo!="" || customerNo!="") universeFrom=Filter.dates.from

            val listSovProduct=vm.sovProduct( Filter.cid,Filter.dates.from,
                Filter.dates.to,universeFrom,Filter.dates.lastYearFrom,
                Filter.dates.lastYearTo, Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType,clusterId,tradeCode,rid, channel,customerNo,acctNo)

            val listSovCategory=vm.sovCategory( Filter.cid,Filter.dates.from,
                Filter.dates.to,universeFrom,Filter.dates.lastYearFrom,
                Filter.dates.lastYearTo, Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType,clusterId,tradeCode,rid, channel,customerNo,acctNo)


            val listGroup= mutableListOf<Data.SovDashProduct>()
            listSovProduct
                .groupBy { it.catId }
                .map {item ->
                    Data.SovCategoryVolume(item.key, item.value.maxOf { it.category } ,
                        item.value.sumOf { it.volume} )
                }.sortedByDescending { it.volume }.forEach { c->
                    listGroup.add(
                        Data.SovDashProduct(c.catId,c.category,
                            listSovProduct.filter { it.catId==c.catId },
                            listSovCategory.firstOrNull{it.catId==c.catId })
                    )
                }
            scopeMainThread.launch {
                 adapter.setData(listGroup,acctNo!="" || customerNo!="")

                if (acctNo=="") {
                    adapter.onCellClick = { col, map ->
                        val intent: Intent = if (col=="volume")
                            Intent(activity, OrderedActivity::class.java)
                        else
                            Intent(activity, NotOrderedActivity::class.java)

                        intent.putExtra("clusterId", clusterId.toString())
                        intent.putExtra("cluster", cluster)
                        intent.putExtra("tradeCode", tradeCode)
                        intent.putExtra("rid", rid)
                        intent.putExtra("dsp", dsp)
                        intent.putExtra("channel", channel)
                        intent.putExtra("customerNo", customerNo)
                        intent.putExtra("customer", customer)
                        intent.putExtra("storeName", storeName)
                        if (map.containsKey("catId")) {
                            intent.putExtra("catId", map["catId"])
                            intent.putExtra("category", map["category"])
                        }
                        if (map.containsKey("itemCode")) {
                            intent.putExtra("itemCode", map["itemCode"])
                            intent.putExtra("itemDesc", map["itemDesc"])
                        }

                        startActivity(intent)
                    }

//                    adapter.onDashClick = { _, map ->
//                        val intent = Intent(activity, ProductDashboardActivity::class.java)
//                        if (rid != "") {
//                            intent.putExtra("rid", rid)
//                            intent.putExtra("dsp", dsp)
//                        } else {
//                            if (map.containsKey("rid")) intent.putExtra("rid", map["rid"])
//                            if (map.containsKey("dsp")) intent.putExtra("dsp", map["dsp"])
//                        }
//                        if (channel != "")
//                            intent.putExtra("channel", channel)
//                        else {
//                            if (map.containsKey("channel"))
//                                intent.putExtra("channel", map["channel"])
//                        }
//
//                        if (map.containsKey("itemCode"))  {
//                            intent.putExtra("itemCode",map["itemCode"])
//                            intent.putExtra("itemDesc",map["itemDesc"])
//                        }
//                        startActivity(intent)
//                    }
                }
                binding.progress.visibility=View.GONE
            }
        }
    }



}





