package com.example.poultry2.ui.dashboard.cluster.tradeDashboard.tradeChannel


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


class DashTradeChannelFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: DashTradeChannelAdapter
    private var clusterId=-1
    private var cluster=""
    private var tradeCode=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId=args!!.getInt("clusterId",-1)
        cluster=args.getString("cluster","")
        tradeCode = args.getString("tradeCode","")

        adapter= DashTradeChannelAdapter()
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
                ViewModelProvider(this@DashTradeChannelFragment)[SovViewModel::class.java]

            val listSovChannelDsp=vm.sovDspChannel(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType, -1,tradeCode,"")

            val listSovChannelCategory=vm.sovCategoryChannel(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType, -1,tradeCode,"")


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


            val listDashChannel= mutableListOf<Data.SovDashTradeChannel>()
            listSovChannelDsp
                .groupBy { it.channel }
                .map {item ->
                    Data.SovChannelVolume(item.key,item.value.sumOf { it.totalNet} )
                }.sortedByDescending { it.volume }.forEach { x->
                    listDashChannel.add(
                        Data.SovDashTradeChannel(
                            x.channel,
                            listSovChannelDsp.filter { it.channel==x.channel},
                            listSovChannelCategory.filter { it.channel==x.channel }
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
                      intent.putExtra("tradeCode", tradeCode)
                      if (map.containsKey("rid")) intent.putExtra("rid", map["rid"])
                      if (map.containsKey("dsp")) intent.putExtra("dsp", map["dsp"])
                      if (map.containsKey("channel")) intent.putExtra("channel", map["channel"])
                      if (map.containsKey("bunitId"))  intent.putExtra("bunitId", map["bunitId"])
                      if (map.containsKey("bunit"))  intent.putExtra("bunit", map["bunit"])
                      if (map.containsKey("catId"))  intent.putExtra("catId", map["catId"])
                      if (map.containsKey("category"))  intent.putExtra("category", map["category"])
                      startActivity(intent)
                  }
                  binding.progress.visibility=View.GONE
            }
        }
    }



}





