package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterTrade


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
import com.example.poultry2.ui.dashboard.cluster.tradeDashboard.TradeDashboardActivity
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashClusterTradeFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashClusterTradeAdapter
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

        adapter= DashClusterTradeAdapter()
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
                ViewModelProvider(this@DashClusterTradeFragment)[SovViewModel::class.java]


            val listSovTradeDsp=vm.sovTradeDsp(
                Filter.cid, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType,clusterId)

            val listSovTradeChannel=vm.sovTradeChannel(
                Filter.cid, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType,clusterId,"","")


            val listSovTrade= mutableListOf<Data.SovDashClusterTrade>()
            listSovTradeDsp
                .groupBy { it.tradeCode }
                .map {item ->
                    Data.SovTradeVolume(item.key, item.value.sumOf { it.volume} )
                }.sortedByDescending { it.volume }.forEach { c->
                    listSovTrade.add(
                        Data.SovDashClusterTrade(c.tradeCode,
                            listSovTradeDsp.filter { it.tradeCode==c.tradeCode },
                            listSovTradeChannel.filter { it.tradeCode==c.tradeCode })
                    )
               }

            scopeMainThread.launch {
                  adapter.setData(listSovTrade)
                  adapter.onItemClick = {
                      val intent = Intent(activity, TradeDashboardActivity::class.java)
                      intent.putExtra("clusterId", clusterId)
                      intent.putExtra("cluster", cluster )
                      intent.putExtra("tradeCode", it.tradeCode )
                      startActivity(intent)
                  }
                  adapter.onCellClick={ col,map ->
                      val intent: Intent = if (col=="volume")
                          Intent(activity, OrderedActivity::class.java)
                      else
                          Intent(activity, NotOrderedActivity::class.java)

                      intent.putExtra("clusterId", clusterId.toString())
                      intent.putExtra("cluster", cluster)

                      if (map.containsKey("tradeCode"))
                          intent.putExtra("tradeCode", map["tradeCode"])

                      if (map.containsKey("rid"))
                          intent.putExtra("rid", map["rid"])

                      if (map.containsKey("dsp"))
                          intent.putExtra("dsp", map["dsp"])

                      if (map.containsKey("channel"))
                          intent.putExtra("channel", map["channel"])

                      startActivity(intent)
                  }
                  binding.progress.visibility=View.GONE
            }
        }
    }
}





