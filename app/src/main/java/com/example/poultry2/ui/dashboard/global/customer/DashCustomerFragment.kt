package com.example.poultry2.ui.dashboard.global.customer


import android.annotation.SuppressLint

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
import com.example.poultry2.data.ar.ArViewModel
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.databinding.FragmentDashCustomerBinding
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DashCustomerFragment : Fragment(){

    private var _binding: FragmentDashCustomerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashCustomerAdapter
    private var clusterId=-1
    private var tradeCode=""
    private var rid=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashCustomerBinding.inflate(inflater, container, false)

        val args = arguments
        clusterId = args!!.getInt("clusterId",0)
        tradeCode = args.getString("tradeCode","")
        rid = args.getString("rid","")

        adapter= DashCustomerAdapter()
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
                ViewModelProvider(this@DashCustomerFragment)[SovViewModel::class.java]


            val listSovCustomer=vm.sovCustomer(
                Filter.cid,Filter.sno, Filter.dates.from, Filter.dates.to, Filter.transType,
                Filter.dates.lastYearFrom,Filter.dates.lastYearTo,
                Filter.dates.lastMonthFrom,Filter.dates.lastMonthTo,clusterId,tradeCode,rid)

            val arVm =
                ViewModelProvider(this@DashCustomerFragment)[ArViewModel::class.java]
            val listCustomerArSummary=arVm.customerArSummary(Filter.cid,Filter.sno,
                clusterId,tradeCode,rid,"","")

//            if (Filter.dates.from!= LocalDate.now().monthFirstDate()
//                ||  Filter.dates.to.toLocalDate()!= LocalDate.now()) {
//                val daysFrom=Filter.dates.from.toLocalDate().dayOfMonth
//                val daysTo=Filter.dates.to.toLocalDate().dayOfMonth
//                val totalDays=Filter.dates.to.toLocalDate().lengthOfMonth()
//                val period =daysTo-daysFrom+1
//                val percent=period.toDouble()/totalDays
//                listSovCustomerBunit.forEach {
//                    it.volumeTarget=(it.volumeTarget*percent).toInt()
//                }
//            }



            val listDashCustomer= mutableListOf<Data.SovDashCustomer>()
            listSovCustomer
                .groupBy { it.customerNo }
                .map {item ->
                    Data.SovCustomerVolume(item.key,item.value.maxOf { it.customer}, item.value.sumOf { it.volume} )
                }.sortedByDescending { it.volume }.forEach { x->

                    listDashCustomer.add(
                        Data.SovDashCustomer(
                            x.customerNo,x.customer,
                            listSovCustomer.filter { it.customerNo==x.customerNo }[0],
                            listCustomerArSummary.filter { it.customerNo==x.customerNo }
                                .sortedByDescending { it.total}
                        )
                    )
                }


            scopeMainThread.launch {
                adapter.setData(listDashCustomer)
                adapter.onItemClick = {
//                  val intent = Intent(activity, CustomerDashboardActivity::class.java)
//                  intent.putExtra("customerNo", it.customerNo)
//                  intent.putExtra("customer", it.customer)
//                  startActivity(intent)
                }
                binding.progress.visibility=View.GONE
            }
        }
    }



}





