package com.example.poultry2.ui.dashboard.global.ar


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
import com.example.poultry2.data.ar.ArViewModel
import com.example.poultry2.databinding.FragmentDashBinding
import com.example.poultry2.ui.dashboard.global.ar.arAccount.ArAccountActivity
import com.example.poultry2.ui.dashboard.global.ar.arInvoice.ArInvoiceActivity
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DashArFragment : Fragment(){

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!
    private var clusterId=-1
    private var cluster=""
    private var tradeCode=""
    private var rid=""
    private var dsp=""
    private var channel=""
    private var acctNo=""
    private var storeName=""
    private var customerNo=""
    private var customer=""
    private lateinit var adapter: DashArAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashBinding.inflate(inflater, container, false)

        val args = arguments
        if (args!!.containsKey("clusterId"))
            clusterId = args.getInt("clusterId",0)
        if (args.containsKey("cluster"))
            cluster = args.getString("cluster","")
        if (args.containsKey("tradeCode"))
            tradeCode = args.getString("tradeCode","")
        if (args.containsKey("rid")) rid = args.getString("rid").toString()
        if (args.containsKey("rid")) dsp = args.getString("dsp").toString()
        if (args.containsKey("channel")) channel = args.getString("channel").toString()
        if (args.containsKey("acctNo")) acctNo = args.getString("acctNo").toString()
        if (args.containsKey("storeName")) storeName = args.getString("storeName").toString()
        if (args.containsKey("customerNo")) customerNo = args.getString("customerNo").toString()
        if (args.containsKey("customer")) customer = args.getString("customer").toString()

        adapter= DashArAdapter()
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
                ViewModelProvider(this@DashArFragment)[ArViewModel::class.java]

            val listArSummary=vm.arSummary(Filter.cid,Filter.sno,clusterId,tradeCode,rid,channel,
                acctNo,customerNo)
            scopeMainThread.launch {
                adapter.setData(listArSummary)
                adapter.onItemClick = {

                    if (acctNo=="" || customerNo!="") {
                        val intent = Intent(activity, ArAccountActivity::class.java)
                        intent.putExtra("balanceType", it.balanceType)
                        intent.putExtra("clusterId", clusterId)
                        intent.putExtra("tradeCode", tradeCode)
                        intent.putExtra("cluster", cluster)
                        intent.putExtra("rid", rid)
                        intent.putExtra("dsp", dsp)
                        intent.putExtra("channel", channel)
                        intent.putExtra("customerNo", customerNo)
                        startActivity(intent)
                    }else {
                        val intent = Intent(activity, ArInvoiceActivity::class.java)
                        intent.putExtra("balanceType", it.balanceType)
                        intent.putExtra("acctNo", acctNo)
                        intent.putExtra("storeName", storeName)
                        startActivity(intent)
                    }
                }
                binding.progress.visibility=View.GONE
            }
        }
    }


}





