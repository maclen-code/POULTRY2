package com.example.poultry2.ui.global.filter.supervisor

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.poultry2.R
import com.example.poultry2.databinding.FragmentFilterSupervisorBinding
import com.example.poultry2.ui.global.filter.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FilterSupervisorFragment : Fragment() {
    private var _binding: FragmentFilterSupervisorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilterSupervisorBinding.inflate(inflater, container, false)

        showItems()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun showItems(){
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {

//            if (Filter.listSupervisor.isEmpty()) {
//                val vm = ViewModelProvider(this@FilterSupervisorFragment)[SovViewModel::class.java]
//                Filter.listSupervisor =
//                    vm.getFilterSupervisor(Filter.cid)
//                        .toMutableList()
//            }

            scopeMainThread.launch {
                val adapter = FilterSupervisorListAdapter()
                binding.rvList.adapter = adapter
                binding.rvList.layoutManager = LinearLayoutManager(activity?.applicationContext)
                val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
                divider.setDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.divider
                    )!!
                )
                binding.rvList.addItemDecoration(divider)
                adapter.setData(Filter.listSupervisor)
            }
        }
    }
}

