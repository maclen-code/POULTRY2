package com.example.poultry2.ui.dashboard.cluster


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
import com.example.poultry2.data.dspTarget.DspTarget
import com.example.poultry2.data.dspTarget.DspTargetViewModel
import com.example.poultry2.data.sivTarget.SivTarget
import com.example.poultry2.data.sivTarget.SivTargetViewModel
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.FragmentDashBinding
import com.example.poultry2.ui.dashboard.cluster.clusterDashboard.ClusterDashboardActivity
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.function.MyDate.monthFirstDate
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.function.Utils.toInt
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.global.target.UpdateTargetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashClusterFragment : Fragment(),UpdateTargetDialog.DialogListener{

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashClusterAdapter
    private lateinit var dspTarget:DspTarget

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        adapter= DashClusterAdapter()
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
                ViewModelProvider(this@DashClusterFragment)[SovViewModel::class.java]

            val listSovClusterTrade=vm.sovClusterTrade(
                Filter.cid, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType)

            val listSovClusterDsp=vm.sovClusterDsp(
                Filter.cid, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType)


            val listSovDashCluster= mutableListOf<Data.SovDashCluster>()
            listSovClusterTrade
                .groupBy { it.clusterId }
                .map {item ->
                    Data.SovClusterVolume(item.key,item.value.maxOf{it.cluster}, item.value.sumOf { it.volume} )
                }.sortedByDescending { it.volume }.forEach { c->
                    listSovDashCluster.add(
                        Data.SovDashCluster(c.clusterId,c.cluster,
                            listSovClusterTrade.filter { it.clusterId==c.clusterId },
                            listSovClusterDsp.filter { it.clusterId==c.clusterId })
                    )
               }
            scopeMainThread.launch {
                  adapter.setData(listSovDashCluster)
                  adapter.onItemClick = {
                      val intent = Intent(activity, ClusterDashboardActivity::class.java)
                      intent.putExtra("clusterId", it.clusterId )
                      intent.putExtra("cluster", it.cluster )
                      startActivity(intent)
                  }

                  adapter.onCellClick={ col,map ->
                      val intent: Intent = if (col=="volume")
                          Intent(activity, OrderedActivity::class.java)
                      else
                          Intent(activity, NotOrderedActivity::class.java)

                      if (map.containsKey("clusterId"))
                          intent.putExtra("clusterId", map["clusterId"])
                      if (map.containsKey("cluster"))
                          intent.putExtra("cluster", map["cluster"])

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

                adapter.onTargetClick={ col,map ->
                    val args = Bundle()
                    args.putString("source", "fragment")
                    args.putString("title", map["dsp"])
                    args.putInt("volumeTarget", map["volumeTarget"]!!.toInt())
                    args.putInt("amountTarget", map["amountTarget"]!!.toInt())

                    val targetDate=Filter.dates.from.toLocalDate().monthFirstDate()
                    dspTarget= DspTarget(map["rid"]!!,Filter.cid,map["clusterId"]!!.toInt(),
                        targetDate,map["volumeTarget"]!!.toInt(),map["amountTarget"]!!.toInt(),false)

                    val updateTargetDialog = UpdateTargetDialog()
                    updateTargetDialog.arguments = args
                    updateTargetDialog.show(
                        childFragmentManager, "target"
                    )

                }
                  binding.progress.visibility=View.GONE
            }
        }
    }

    override fun onFinishSetTargetDialog(volumeTarget: Int, amountTarget: Int) {
        val job = Job()
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            val vm =
                ViewModelProvider(this@DashClusterFragment)[DspTargetViewModel::class.java]

            dspTarget.volumeTarget=volumeTarget
            dspTarget.amountTarget=amountTarget
            vm.insert(dspTarget)
            Filter.updated.postValue(true)
        }
    }
}





