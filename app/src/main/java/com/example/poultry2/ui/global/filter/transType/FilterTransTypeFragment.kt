package com.example.poultry2.ui.global.filter.transType

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.FragmentFilterTransTypeBinding
import com.example.poultry2.ui.global.filter.Filter

class FilterTransTypeFragment : Fragment() {
    private var _binding: FragmentFilterTransTypeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilterTransTypeBinding.inflate(inflater, container, false)

        showItems()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun showItems(){

            if (Filter.listTransType.isEmpty() ) {
                Filter.listTransType.add(Data.FilterTransType("SL","Sales",false))
                Filter.listTransType.add(Data.FilterTransType("DR","Delivery Returns",false))
                Filter.listTransType.add(Data.FilterTransType("GS","Good Stock Returns",false))
                Filter.listTransType.add(Data.FilterTransType("BO","Bad Order Returns",false))
                Filter.transType=""
            }


            val  adapter = FilterTransTypeListAdapter()
            binding.rvList.adapter = adapter
            binding.rvList.layoutManager = LinearLayoutManager(activity?.applicationContext)
            val divider = DividerItemDecoration(activity,DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
            binding.rvList.addItemDecoration(divider)

            adapter.setData(Filter.listTransType)

    }


}

