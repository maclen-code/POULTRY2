package com.example.poultry.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.poultry.databinding.FragmentDashboardBinding
import com.example.poultry.ui.function.Theme.resolveColorAttr
import com.example.poultry.ui.function.Utils
import com.example.poultry.ui.global.filter.Filter
import com.google.android.material.tabs.TabLayout
import java.util.Locale


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val tabHeader= mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        hideAllFragments()
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                showFragment()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    val name = tab.text.toString().lowercase(Locale.ROOT)
                    hideFragment(name)
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        setTabs()
        Filter.updated.observe(viewLifecycleOwner ) {

             binding.tvDateRange.text = Filter.range
            val par="${Utils.formatDoubleToString(Utils.par())}  %"
            binding.tvPar.text=par
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setTabs() {
//        if (tabHeader.isEmpty()) {
//            if (Access.granted("SIV DASH TAB")) tabHeader.add("siv-sov")
//            tabHeader.add("bunit")
//            tabHeader.add("category")
//            tabHeader.add("product")
//            if (Access.granted("DSP DASH TAB")) tabHeader.add("dsp")
//            tabHeader.add("channel")
//            tabHeader.add("customer")
//            tabHeader.add("account")
//            tabHeader.add("inventory")
//            tabHeader.add("ar")
//        }

        tabHeader.forEach {
            val tab: TabLayout.Tab = binding.tabs.newTab()
            tab.text = it
            binding.tabs.addTab(tab)
        }
        binding.tabs.tabGravity = TabLayout.GRAVITY_CENTER
        binding.tabs.tabMode = TabLayout.MODE_SCROLLABLE


        val textColor = requireActivity().resolveColorAttr(android.R.attr.textColorSecondary)
        val selectedColor =requireActivity().resolveColorAttr(android.R.attr.textColorPrimary)

        binding.tabs.setSelectedTabIndicatorColor(selectedColor)
        binding.tabs.setTabTextColors(textColor,selectedColor)

    }
    private fun hideAllFragments(){
        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().hide(fragment).commit()
        }
    }
    private fun showFragment() {
//        val tabPosition = binding.tabs.selectedTabPosition
//        val tab: TabLayout.Tab = binding.tabs.getTabAt(tabPosition) ?: return
//        val name = tab.text.toString().lowercase(Locale.ROOT)
//
//        val transaction = childFragmentManager.beginTransaction()
//        var fragment: Fragment?=childFragmentManager.findFragmentByTag(name)
//        val args = Bundle()
//        if (fragment==null) {
//            when (name) {
//                "siv-sov" -> fragment = DashSivSovFragment()
//                "bunit" -> fragment = DashBunitFragment()
//                "category" -> fragment = DashCategoryFragment()
//                "product" -> fragment = DashProductFragment()
//                "dsp" -> fragment = DashDspFragment()
//                "channel" -> fragment = DashChannelFragment()
//                "customer" -> fragment = DashCustomerFragment()
//                "account" -> fragment = DashAccountFragment()
//                "inventory" -> fragment = DashInventoryFragment()
//                "ar" -> fragment = DashArFragment()
//            }
//            if (fragment != null) {
//                fragment.arguments = args
//                transaction.add(R.id.fragment, fragment,name)
//                transaction.disallowAddToBackStack()
//            }
//        }else{
//            transaction.show(fragment)
//
//        }
//        if (fragment != null) transaction.commit()
    }
    private fun hideFragment(name:String){
        val fragment: Fragment?=childFragmentManager.findFragmentByTag(name)
        val transaction = childFragmentManager.beginTransaction()
        if (fragment != null) {
            transaction.hide(fragment)
            transaction.commit()
        }
    }

}