package com.example.poultry2.ui.dashboard.cluster.tradeDspDashboard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.poultry2.ui.dashboard.global.ar.DashArFragment
import com.example.poultry2.R
import com.example.poultry2.databinding.ActivityClusterDashboardBinding
import com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterCategory.DashClusterCategoryFragment
import com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterTrade.DashClusterTradeFragment
import com.example.poultry2.ui.dashboard.global.acct.DashAcctFragment
import com.example.poultry2.ui.dashboard.global.customer.DashCustomerFragment
import com.example.poultry2.ui.dashboard.cluster.clusterDashboard.clusterDsp.DashClusterDspFragment
import com.example.poultry2.ui.dashboard.cluster.tradeDashboard.tradeCategory.DashTradeCategoryFragment
import com.example.poultry2.ui.dashboard.cluster.tradeDashboard.tradeChannel.DashTradeChannelFragment
import com.example.poultry2.ui.dashboard.cluster.tradeDashboard.tradeDsp.DashTradeDspFragment
import com.example.poultry2.ui.dashboard.cluster.tradeDspDashboard.tradeDspCategory.DashTradeDspCategoryFragment
import com.example.poultry2.ui.dashboard.cluster.tradeDspDashboard.tradeDspChannel.DashTradeDspChannelFragment
import com.example.poultry2.ui.dashboard.global.product.DashProductFragment
import com.example.poultry2.ui.function.Theme
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.global.filter.FilterActivity
import com.google.android.material.tabs.TabLayout
import java.util.*


class TradeDspDashboardActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityClusterDashboardBinding
    private var clusterId=-1
    private var cluster=""
    private var tradeCode=""
    private var rid=""
    private var dsp=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClusterDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        clusterId=intent.getIntExtra("clusterId",-1)
        cluster=intent.getStringExtra("cluster").toString()
        tradeCode=intent.getStringExtra("tradeCode").toString()
        rid=intent.getStringExtra("rid").toString()
        dsp=intent.getStringExtra("dsp").toString()

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title ="$tradeCode - $dsp"
        actionbar.setDisplayHomeAsUpEnabled(true)

        setupMenu()
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

        Filter.updated.observe(this
        ) {
            binding.tvDateRange.text=Filter.range
            binding.tvPar.text= Utils.formatDoubleToString(Utils.par()) +  " %"
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupMenu() {
        (this).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu itemm
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_filter, menu)
                Theme.setMenuTextColor(this@TradeDspDashboardActivity,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item

                return when (menuItem.itemId) {
                    R.id.menu_filter -> {
                        val filter= arrayOf("period","record type")
                        val intent = Intent(this@TradeDspDashboardActivity,
                            FilterActivity::class.java)
                        intent.putExtra("filter",filter)
                        startActivity(intent)
                        true
                    }

                    else -> false}
            }
        }, this, Lifecycle.State.RESUMED)
    }

    private fun setTabs(){
        val tabHeader= arrayOf("category","channel","product","customer","account","ar")
        tabHeader.forEach {
            val tab: TabLayout.Tab = binding.tabs.newTab()
            tab.text = it
            binding.tabs.addTab(tab)
        }
        binding.tabs.tabGravity = TabLayout.GRAVITY_CENTER
        binding.tabs.tabMode = TabLayout.MODE_SCROLLABLE
        val textColor = this.resolveColorAttr(android.R.attr.textColorSecondary)
        val selectedColor =this.resolveColorAttr(android.R.attr.textColorPrimary)
        binding.tabs.setSelectedTabIndicatorColor(selectedColor)
        binding.tabs.setTabTextColors(textColor,selectedColor)
    }

    private fun hideAllFragments(){
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().hide(fragment).commit()
        }
    }
    private fun showFragment() {
        val tabPosition = binding.tabs.selectedTabPosition
        val tab: TabLayout.Tab = binding.tabs.getTabAt(tabPosition) ?: return
        val name = tab.text.toString().lowercase(Locale.ROOT)

        val transaction = supportFragmentManager.beginTransaction()
        var fragment: Fragment?=supportFragmentManager.findFragmentByTag(name)
        val args = Bundle()
        args.putInt("clusterId",clusterId)
        args.putString("cluster",cluster)
        args.putString("tradeCode",tradeCode)
        args.putString("rid",rid)
        args.putString("dsp",dsp)

        if (fragment==null) {
            when (name) {
                "channel" -> fragment = DashTradeDspChannelFragment()
                "category" -> fragment = DashTradeDspCategoryFragment()
                "product" -> fragment = DashProductFragment()
                "customer" -> fragment = DashCustomerFragment()
                "account" -> fragment = DashAcctFragment()
                "ar" -> fragment = DashArFragment()
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
        val fragment: Fragment?=supportFragmentManager.findFragmentByTag(name)
        val transaction = supportFragmentManager.beginTransaction()
        if (fragment != null) {
            transaction.hide(fragment)
            transaction.commit()
        }
    }

}