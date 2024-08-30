package com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterCategory


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
import com.example.poultry2.databinding.FragmentDashCustomerBinding
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashClusterCategoryFragment : Fragment(){

    private var _binding: FragmentDashCustomerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashClusterCategoryAdapter
    private var clusterId=-1
    private var cluster=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashCustomerBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId = args!!.getInt("clusterId",0)
        cluster = args.getString("cluster","")

        adapter= DashClusterCategoryAdapter()
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
                ViewModelProvider(this@DashClusterCategoryFragment)[SovViewModel::class.java]

            val listSovCategoryDsp=vm.sovCategoryDsp(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType,clusterId,"")

            val listSovCategoryChannel=vm.sovCategoryChannel(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.dates.universeFrom,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,
                Filter.transType,clusterId,"","")


            val listSovCategory= mutableListOf<Data.SovDashClusterCategory>()
            listSovCategoryDsp
                .groupBy { it.catId }
                .map {item ->
                    Data.SovCategoryVolume(item.key,item.value.maxOf { it.category},
                        item.value.sumOf { it.volume} )
                }.sortedByDescending { it.volume }.forEach { c->
                    listSovCategory.add(
                        Data.SovDashClusterCategory(c.catId,c.category,
                            listSovCategoryDsp.filter { it.catId==c.catId },
                            listSovCategoryChannel.filter { it.catId==c.catId })
                    )
               }

            scopeMainThread.launch {
                  adapter.setData(listSovCategory)
                  adapter.onItemClick = {
//                      val intent = Intent(activity, ChannelActivity::class.java)
//                      intent.putExtra("channel", it.channel )
//                      intent.putExtra("rid", rid)
//                      intent.putExtra("dsp", dsp)
//                      startActivity(intent)
                  }
                  adapter.onCellClick={ col,map ->
                      val intent: Intent = if (col=="volume")
                          Intent(activity, OrderedActivity::class.java)
                      else
                          Intent(activity, NotOrderedActivity::class.java)


//                      if (clusterId!=-1) {
//                          intent.putExtra("clusterId", clusterId)
//                          intent.putExtra("cluster", cluster)
//                      }else {
//
//                          if (map.containsKey("clusterId"))
//                              intent.putExtra("clusterId", map["clusterId"])
//
//                          if (map.containsKey("cluster"))
//                              intent.putExtra("cluster", map["cluster"])
//
//                      }

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

                      if (map.containsKey("catId"))
                          intent.putExtra("catId", map["catId"])

                      if (map.containsKey("category"))
                          intent.putExtra("category", map["category"])

                      startActivity(intent)
                  }
                  binding.progress.visibility=View.GONE
            }
        }
    }
}





