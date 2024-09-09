package com.example.poultry2.ui.dashboard.global.acct.acctDashboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

import com.example.poultry2.R
import com.example.poultry2.databinding.ActivityAccountDashboardBinding
import com.example.poultry2.ui.dashboard.global.acct.acctDashboard.invoice.InvoiceFragment
import com.example.poultry2.ui.dashboard.global.acct.acctDashboard.summary.SalesFragment
import com.example.poultry2.ui.dashboard.global.ar.DashArFragment
import com.example.poultry2.ui.dashboard.global.product.DashProductFragment
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import com.google.android.material.tabs.TabLayout
import java.util.*


class AccountDashboardActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityAccountDashboardBinding
    private var acctNo=""
    private var storeName=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        acctNo=intent.getStringExtra("acctNo").toString()
        storeName=intent.getStringExtra("storeName").toString()

        val actionbar = supportActionBar
        actionbar!!.title =storeName
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
//    private fun setupMenu() {
//        (this).addMenuProvider(object : MenuProvider {
//            override fun onPrepareMenu(menu: Menu) {
//                // Handle for example visibility of menu itemm
//            }
//
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_dash_account, menu)
//                Theme.setMenuTextColor(this@AccountDashboardActivity,menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                // Validate and handle the selected menu item
//
//                return when (menuItem.itemId) {
//                    R.id.menu_filter -> {
//                        val filter= arrayOf("period","record type")
//                        val intent = Intent(this@AccountDashboardActivity,
//                            FilterActivity::class.java)
//                        intent.putExtra("filter",filter)
//                        startActivity(intent)
//                        true
//                    }
//                    R.id.menu_target -> {
//                        val intent = Intent(
//                            this@AccountDashboardActivity,
//                            AccountTargetActivity::class.java
//                        )
//                        intent.putExtra("rid", rid)
//                        intent.putExtra("acctNo", acctNo)
//                        intent.putExtra("storeName", storeName)
//                        startActivity(intent)
//
//                        true
//                    }
//                    R.id.menu_frequency -> {
//
//                        val intent = Intent(this@AccountDashboardActivity,
//                            AccountFreqActivity::class.java)
//                        intent.putExtra("rid", rid)
//                        intent.putExtra("acctNo",acctNo)
//                        intent.putExtra("storeName",storeName)
//                        startActivity(intent)
//
//                        true
//                    }
//                    else -> false}
//            }
//        }, this, Lifecycle.State.RESUMED)
//    }
    private fun setTabs(){
        val tabHeader= arrayOf("summary","product","invoice","ar")
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
        args.putString("acctNo",acctNo)
        args.putString("storeName",storeName)
        if (fragment==null) {
            when (name) {
                "summary" -> fragment = SalesFragment()
                "product" -> fragment = DashProductFragment()
                "invoice" -> fragment = InvoiceFragment()
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