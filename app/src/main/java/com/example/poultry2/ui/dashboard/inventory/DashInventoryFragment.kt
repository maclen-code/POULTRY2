package com.example.poultry2.ui.dashboard.inventory


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
import com.example.poultry2.data.inventory.InventoryViewModel
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.FragmentDashCustomerBinding
import com.example.poultry2.ui.dashboard.global.uba.notOrdered.NotOrderedActivity
import com.example.poultry2.ui.dashboard.global.uba.ordered.OrderedActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashInventoryFragment : Fragment(){

    private var _binding: FragmentDashCustomerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashInventoryAdapter
    private var clusterId=-1
    private var cluster=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashCustomerBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId = args!!.getInt("clusterId",-1)
        cluster = args.getString("cluster","")

        adapter= DashInventoryAdapter()
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
                ViewModelProvider(this@DashInventoryFragment)[InventoryViewModel::class.java]

            val listInventory=vm.inventoryProduct(Filter.cid)

            val listCategoryInventory= mutableListOf<Data.SovDashInventory>()
            listInventory
                .groupBy { it.catId }
                .map { item ->
                    Data.SovCategoryVolume(
                        item.key, item.value.maxOf { it.category },
                        0.0
                    )
                }.forEach { c->
                    listCategoryInventory.add(
                        Data.SovDashInventory(c.catId,c.category,
                            listInventory.filter { it.catId==c.catId })
                    )
                }


            scopeMainThread.launch {
                  adapter.setData(listCategoryInventory)
                  adapter.onItemClick = {
//                      val intent = Intent(activity, ChannelActivity::class.java)
//                      intent.putExtra("channel", it.channel )
//                      intent.putExtra("rid", rid)
//                      intent.putExtra("dsp", dsp)
//                      startActivity(intent)
                  }
                  binding.progress.visibility=View.GONE
            }
        }
    }
}





