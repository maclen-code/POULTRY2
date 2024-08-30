package com.example.bmeg.ui.dashboard.product


import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
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
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.dashboard.global.product.DashProductAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DashProductFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!
    private var rid=""
    private var dsp=""
    private var channel=""
    private var acctNo=""
    private var customerNo=""
    private lateinit var adapter: DashProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        val args = arguments
        if (args!!.containsKey("rid")) rid = args.getString("rid").toString()
        if (args.containsKey("dsp")) dsp = args.getString("dsp").toString()
        if (args.containsKey("channel")) channel = args.getString("channel").toString()
        if (args.containsKey("acctNo")) acctNo = args.getString("acctNo").toString()
        if (args.containsKey("customerNo")) customerNo = args.getString("customerNo").toString()

        adapter= DashProductAdapter()
        binding.rvList.adapter =adapter
        binding.rvList.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val divider = DividerItemDecoration(activity,DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.rvList.addItemDecoration(divider)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Filter.updated.observe(viewLifecycleOwner
        ) {
//            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    @SuppressLint("SetTextI18n")
//    private fun show(){
//        binding.progress.visibility=View.VISIBLE
//        val job = Job()
//        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
//        val scopeIO = CoroutineScope(job + Dispatchers.IO)
//        scopeIO.launch {
//            val vm =
//                ViewModelProvider(this@DashProductFragment)[SovViewModel::class.java]
//
//
//            val listAccountProduct=vm.sovAccountProduct( Filter.cid,Filter.sno, Filter.dates.from,
//                Filter.dates.to,Filter.dates.universeFrom,Filter.dates.lastYearFrom,
//                Filter.dates.lastYearTo, Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
//                Filter.transType, channel,rid,acctNo,customerNo)
//
//            val listGroup= mutableListOf<Data.SovAccountCategoryProduct>()
//            listAccountProduct
//                .groupBy { it.category }
//                .map {item ->
//                    Data.SovCategoryTotalNet(item.key, item.value.sumOf { it.totalNet} )
//                }.sortedByDescending { it.totalNet }.forEach { c->
//                    listGroup.add(
//                        Data.SovAccountCategoryProduct(c.category,
//                            listAccountProduct.filter { it.category==c.category }
//                                .sortedByDescending { it.totalNet })
//                    )
//                }
//            scopeMainThread.launch {
//                 adapter.setData(listGroup,acctNo!="" || customerNo!="")
//
//                if (acctNo=="") {
//                    adapter.onCellClick = { col, map ->
//                        val intent:Intent = if (col=="volume")
//                            Intent(activity, OrderedActivity::class.java)
//                        else
//                            Intent(activity, NotOrderedActivity::class.java)
//
//                        if (map.containsKey("bunit")) intent.putExtra("bunit", map["bunit"])
//
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
//                        if (map.containsKey("itemCode")) intent.putExtra(
//                            "itemCode",
//                            map["itemCode"]
//                        )
//                        startActivity(intent)
//                    }
//
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
//                }
//                binding.progress.visibility=View.GONE
//            }
//        }
//    }



}





