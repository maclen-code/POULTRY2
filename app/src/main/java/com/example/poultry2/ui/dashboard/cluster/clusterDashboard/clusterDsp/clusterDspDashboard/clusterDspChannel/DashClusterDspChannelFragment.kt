package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterDsp.clusterDspDashboard.clusterDspChannel


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
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DashClusterDspChannelFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: DashClusterDspChannelAdapter
    private var clusterId=-1
    private var cluster=""
    private var rid=""
    private var dsp=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId = args!!.getInt("clusterId",-1)
        cluster = args.getString("cluster","")
        rid = args.getString("rid","")
        dsp = args.getString("dsp","")


        adapter= DashClusterDspChannelAdapter()
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
                ViewModelProvider(this@DashClusterDspChannelFragment)[SovViewModel::class.java]

            val listSovTradeChannel=vm.sovTradeChannel(
                Filter.cid, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType, clusterId,"",rid)



            val listDashChannel= mutableListOf<Data.SovDashClusterDspChannel>()
            listSovTradeChannel
                .groupBy { it.tradeCode }
                .map {item ->
                    Data.SovTradeVolume(item.key,item.value.sumOf { it.volume} )
                }.sortedByDescending { it.volume }.forEach { x->
                    listDashChannel.add(
                        Data.SovDashClusterDspChannel(
                            x.tradeCode,
                            listSovTradeChannel.filter { it.tradeCode==x.tradeCode}
                        )
                    )
                }


            scopeMainThread.launch {
                  adapter.setData(listDashChannel)
                  adapter.onItemClick = {
//                      val intent = Intent(activity, DspDashboardActivity::class.java)
//                      intent.putExtra("rid", it.rid)
//                      intent.putExtra("dsp", it.dsp)
//                      startActivity(intent)
                  }


                  adapter.onCellClick={ col,map ->
                      val intent:Intent = if (col=="volume")
                          Intent(activity, OrderedActivity::class.java)
                      else
                          Intent(activity, NotOrderedActivity::class.java)

                      intent.putExtra("clusterId", clusterId.toString())
                      intent.putExtra("cluster", cluster)
                      intent.putExtra("rid", rid)
                      intent.putExtra("dsp", dsp)

                      if (map.containsKey("tradeCode")) intent.putExtra("tradeCode", map["tradeCode"])
                      if (map.containsKey("channel")) intent.putExtra("channel", map["channel"])
                      startActivity(intent)
                  }
                  binding.progress.visibility=View.GONE
            }
        }
    }



}





