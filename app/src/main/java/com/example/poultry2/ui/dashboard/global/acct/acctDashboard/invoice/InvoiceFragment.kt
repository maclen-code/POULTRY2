package com.example.poultry2.ui.dashboard.global.acct.acctDashboard.invoice


import android.annotation.SuppressLint

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class InvoiceFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!
    private var acctNo=""
    private var customerNo=""
    private lateinit var adapter: InvoiceAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        val args = arguments
        if (args!!.containsKey("acctNo")) acctNo = args.getString("acctNo").toString()
        if (args.containsKey("customerNo")) customerNo = args.getString("customerNo").toString()

        adapter= InvoiceAdapter()
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
                ViewModelProvider(this@InvoiceFragment)[SovViewModel::class.java]



            val listInvoiceProduct=vm.sovInvoiceProduct( acctNo,customerNo, Filter.dates.from, Filter.dates.to)

            val listGroup= mutableListOf<Data.SovInvoice>()
            listInvoiceProduct
                .groupBy { it.invoiceNo }
                .map {item ->
                    Data.SovInvoiceDate(item.key, item.value.maxOf { it.date} )
                }.sortedByDescending { it.date }.forEach { i->
                    listGroup.add(
                        Data.SovInvoice(i.invoiceNo,i.date,
                            listInvoiceProduct.filter { it.invoiceNo==i.invoiceNo })
                    )
                }
            scopeMainThread.launch {
                adapter.setData(listGroup)
                binding.progress.visibility=View.GONE
            }
        }
    }



}





