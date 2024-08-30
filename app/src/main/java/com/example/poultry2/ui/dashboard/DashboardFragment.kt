package com.example.poultry2.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.poultry2.ui.dashboard.global.customer.DashCustomerFragment
import com.example.poultry2.R
import com.example.poultry2.ui.dashboard.sivSov.DashSivSovFragment
import com.example.poultry2.databinding.FragmentDashboardBinding
import com.example.poultry2.ui.dashboard.cluster.DashClusterFragment
import com.example.poultry2.ui.function.Theme
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.global.filter.FilterActivity
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

//        setTabs()
        Filter.updated.observe(viewLifecycleOwner ) {

            binding.tvDateRange.text = Filter.range
            val par="${Utils.formatDoubleToString(Utils.par())}  %"
            binding.tvPar.text=par
            setTabs()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupMenu() {

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu item
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (tabHeader.contains("siv-sov")) {
                    menuInflater.inflate(R.menu.menu_siv_sov, menu)
                }else
                    menuInflater.inflate(R.menu.menu_filter, menu)

                Theme.setMenuTextColor(requireContext(),menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item

                return when (menuItem.itemId) {

                    R.id.menu_filter -> {
                        val filter= arrayOf("server","period","supervisor","record type")
                        val intent = Intent(activity, FilterActivity::class.java)
                        intent.putExtra("filter",filter)
                        startActivity(intent)
                        true
                    }

                    R.id.menu_target -> {
//                        if (Filter.listSupervisor.size==1) {
//                            Filter.listSupervisor[0].isChecked=true
//                            Filter.sno=Filter.listSupervisor[0].sno
//                        }
//
//                        if (Filter.listSupervisor.filter {it.isChecked}.size!=1)
//                            Toast.makeText(requireContext(),
//                                "Please select 1 supervisor", Toast.LENGTH_SHORT).show()
//                        else {
//                            val intent = Intent(requireContext(), SivTargetActivity::class.java)
//                            startActivity(intent)
//                        }
                        true
                    }

                    else -> false}
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun setTabs() {
        if (tabHeader.isEmpty()) {
            tabHeader.add("siv-sov")
            tabHeader.add("cluster")
        }
        if (binding.tabs.tabCount==0) {
            setupMenu()
            tabHeader.forEach {
                val tab: TabLayout.Tab = binding.tabs.newTab()
                tab.text = it
                binding.tabs.addTab(tab)
            }
            binding.tabs.tabGravity = TabLayout.GRAVITY_CENTER
            binding.tabs.tabMode = TabLayout.MODE_SCROLLABLE


            val textColor = requireActivity().resolveColorAttr(android.R.attr.textColorSecondary)
            val selectedColor = requireActivity().resolveColorAttr(android.R.attr.textColorPrimary)

            binding.tabs.setSelectedTabIndicatorColor(selectedColor)
            binding.tabs.setTabTextColors(textColor, selectedColor)

        }




    }
    private fun hideAllFragments(){
        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().hide(fragment).commit()
        }
    }
    private fun showFragment() {
        val tabPosition = binding.tabs.selectedTabPosition
        val tab: TabLayout.Tab = binding.tabs.getTabAt(tabPosition) ?: return
        val name = tab.text.toString().lowercase(Locale.ROOT)

        val transaction = childFragmentManager.beginTransaction()
        var fragment: Fragment?=childFragmentManager.findFragmentByTag(name)
        val args = Bundle()
        if (fragment==null) {
            when (name) {
                "siv-sov" -> fragment = DashSivSovFragment()
                "cluster" -> fragment = DashClusterFragment()
            }
            if (fragment != null) {
                fragment.arguments = args
                transaction.add(R.id.fragment, fragment,name)
                transaction.disallowAddToBackStack()
            }
        }else{
            transaction.show(fragment)

        }
        if (fragment != null) transaction.commit()
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