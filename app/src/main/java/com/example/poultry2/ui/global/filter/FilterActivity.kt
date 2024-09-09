package com.example.poultry2.ui.global.filter


import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.poultry2.R
import com.example.poultry2.databinding.ActivityFilterBinding
import com.example.poultry2.ui.function.Theme.resolveColorAttr
import com.example.poultry2.ui.global.filter.period.FilterPeriodFragment
import com.example.poultry2.ui.global.filter.server.FilterServerFragment
import com.example.poultry2.ui.global.filter.transType.FilterTransTypeFragment
import com.google.android.material.tabs.TabLayout
import java.util.*


class FilterActivity : AppCompatActivity() {
    private var clusterId=-1
    private lateinit var binding: ActivityFilterBinding
    private var range= Filter.range

    private var tempCid=""
    private var tempTransType=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Filter"
        actionbar.setDisplayHomeAsUpEnabled(true)


        val intent = intent
        if (intent.hasExtra("clusterId")) clusterId=intent.getIntExtra("clusterId",-1)

        setTabs()
        showActiveTabFragment()

        tempCid= Filter.cid
        tempTransType= Filter.transType

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    val transaction = supportFragmentManager.beginTransaction()
                    var fragment:Fragment?=null
                    when (tab.text.toString().lowercase(Locale.ROOT)) {
                        "period" -> fragment= FilterPeriodFragment()
                        "server" -> fragment= FilterServerFragment()
                        "record type" -> fragment = FilterTransTypeFragment()
                    }
                    transaction.replace(R.id.fragment, fragment!!)
                    transaction.disallowAddToBackStack()
                    transaction.commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
            // Back is pressed... Finishing the activity
            checkFilterChanges()
            setResult(RESULT_OK)
            finish()
        }
    }



    private fun setTabs(){
        val filter:Array<String> = intent.getStringArrayExtra("filter") as Array<String>
        filter.forEach {
            val tab: TabLayout.Tab = binding.tabs.newTab()
            tab.text = it
            binding.tabs.addTab(tab)
        }
        binding.tabs.tabGravity = TabLayout.GRAVITY_CENTER
        binding.tabs.tabMode = TabLayout.MODE_SCROLLABLE
        val selectedColor =this.resolveColorAttr(android.R.attr.textColorSecondary)
        binding.tabs.setSelectedTabIndicatorColor(selectedColor)
        val textColor = this.resolveColorAttr(android.R.attr.textColorSecondary)
        binding.tabs.setTabTextColors(textColor,selectedColor)
    }

    private fun showActiveTabFragment(){
        val tabPosition = binding.tabs.selectedTabPosition
        val tab: TabLayout.Tab? = binding.tabs.getTabAt(tabPosition)
        if (tab!=null){
            val transaction = supportFragmentManager.beginTransaction()
            var fragment: Fragment? = null
            when (tab.text.toString().lowercase(Locale.ROOT)) {
                "period" -> fragment = FilterPeriodFragment()
                "server" -> fragment = FilterServerFragment()
                "record type" -> fragment = FilterTransTypeFragment()
            }
            transaction.replace(R.id.fragment, fragment!!)
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        checkFilterChanges()
        setResult(RESULT_OK)
        finish()
        return true
    }

    private fun checkFilterChanges(){
      if ( range!= Filter.range)
            Filter.updated.postValue(true)
      else if (tempCid!= Filter.cid)
          Filter.updated.postValue(true)
      else if (tempTransType!= Filter.transType)
          Filter.updated.postValue(true)

    }




}