package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterDsp


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
import com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterDsp.clusterDspDashboard.ClusterDspDashboardActivity
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DashClusterDspFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: DashClusterDspAdapter
    private var clusterId=-1
    private var cluster=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId = args!!.getInt("clusterId",0)
        cluster = args.getString("cluster","")

        adapter= DashClusterDspAdapter()
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
                ViewModelProvider(this@DashClusterDspFragment)[SovViewModel::class.java]


            val listSovDspTrade=vm.sovTradeDsp(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType, clusterId)

            val listSovDspBunit=vm.sovDspBunit(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType, clusterId,"")

//            if (Filter.dates.from!= LocalDate.now().monthFirstDate()
//                ||  Filter.dates.to.toLocalDate()!= LocalDate.now()) {
//                val daysFrom=Filter.dates.from.toLocalDate().dayOfMonth
//                val daysTo=Filter.dates.to.toLocalDate().dayOfMonth
//                val totalDays=Filter.dates.to.toLocalDate().lengthOfMonth()
//                val period =daysTo-daysFrom+1
//                val percent=period.toDouble()/totalDays
//                listSovDspBunit.forEach {
//                    it.volumeTarget=(it.volumeTarget*percent).toInt()
//                }
//            }


            val listDashDsp= mutableListOf<Data.SovDashClusterDsp>()
            listSovDspTrade
                .groupBy { it.rid }
                .map {item ->
                    Data.SovDspVolume(item.key,item.value.maxOf { it.dsp} ,
                        item.value.sumOf { it.totalNet} )
                }.sortedByDescending { it.volume }.forEach { x->
                    listDashDsp.add(
                        Data.SovDashClusterDsp(
                            x.rid,x.dsp,
                            listSovDspTrade.filter { it.rid==x.rid},
                            listSovDspBunit.filter { it.rid==x.rid }
                        )
                    )
                }
            scopeMainThread.launch {
                  adapter.setData(listDashDsp)
                  adapter.onItemClick = {
                      val intent = Intent(activity, ClusterDspDashboardActivity::class.java)
                      intent.putExtra("clusterId", clusterId)
                      intent.putExtra("cluster", cluster)
                      intent.putExtra("rid", it.rid)
                      intent.putExtra("dsp", it.dsp)
                      startActivity(intent)
                  }


                  adapter.onCellClick={ col,map ->
                      val intent:Intent = if (col=="volume")
                          Intent(activity, OrderedActivity::class.java)
                      else
                          Intent(activity, NotOrderedActivity::class.java)

                      intent.putExtra("clusterId", clusterId.toString())
                      intent.putExtra("cluster", cluster)
                      if (map.containsKey("tradeCode"))
                          intent.putExtra("tradeCode", map["tradeCode"])
                      if (map.containsKey("bunitId"))
                          intent.putExtra("bunitId", map["bunitId"])
                      if (map.containsKey("bunit"))  intent.putExtra("bunit", map["bunit"])
                      if (map.containsKey("rid")) intent.putExtra("rid", map["rid"])
                      if (map.containsKey("dsp")) intent.putExtra("dsp", map["dsp"])

                      startActivity(intent)
                  }
                  binding.progress.visibility=View.GONE
            }
        }
    }



}





