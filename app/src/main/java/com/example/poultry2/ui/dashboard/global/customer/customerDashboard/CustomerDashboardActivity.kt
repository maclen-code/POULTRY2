package com.example.poultry2.ui.dashboard.global.customer.customerDashboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

import com.example.poultry2.R
import com.example.poultry2.databinding.ActivityAccountDashboardBinding
import com.example.poultry2.ui.dashboard.global.acct.acctDashboard.invoice.InvoiceFragment
import com.example.poultry2.ui.dashboard.global.acct.acctDashboard.summary.SalesFragment
import com.example.poultry2.ui.dashboard.global.ar.DashArFragment
import com.example.poultry2.ui.dashboard.global.customer.customerDashboard.summary.CustomerSummaryFragment
import com.example.poultry2.ui.dashboard.global.product.DashProductFragment
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import com.google.android.material.tabs.TabLayout
import java.util.*


class CustomerDashboardActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityAccountDashboardBinding
    private var customerNo=""
    private var customer=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        customerNo=intent.getStringExtra("customerNo").toString()
        customer=intent.getStringExtra("customer").toString()

        val actionbar = supportActionBar
        actionbar!!.title =customer
        actionbar.setDisplayHomeAsUpEnabled(true)

//        setupMenu()
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

    private fun setTabs(){
        val tabHeader= arrayOf("summary","product","ar")
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
        args.putString("customerNo",customerNo)
        args.putString("customer",customer)
        if (fragment==null) {
            when (name) {
                "summary" -> fragment = CustomerSummaryFragment()
                "product" -> fragment = DashProductFragment()
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