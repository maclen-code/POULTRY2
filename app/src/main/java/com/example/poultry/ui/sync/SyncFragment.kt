package com.example.poultry.ui.sync



import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.poultry.R
import com.example.poultry.data.AppDatabase
import com.example.poultry.data.AppDatabase.Companion.getDBVersion
import com.example.poultry.data.Data
import com.example.poultry.databinding.FragmentSyncBinding
import com.example.poultry.ui.function.MyDate.monthFirstDate
import com.example.poultry.ui.function.MyDate.monthLastDate
import com.example.poultry.ui.function.MyDate.toDateString
import com.example.poultry.ui.function.MyDate.toLocalDate
import com.example.poultry.ui.function.Theme.resolveColorAttr
import com.example.poultry.ui.function.Utils
import com.example.poultry.ui.global.AdminValidationDialog
import com.example.poultry.ui.global.SelectPeriodDialog
import com.example.poultry.ui.global.filter.Filter
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate


class SyncFragment : Fragment(), SelectPeriodDialog.DialogListener,AdminValidationDialog.DialogListener{

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!
    private var dbaseVersion=""

    private lateinit var selectedServer: Data.Server
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSyncBinding.inflate(inflater, container, false)

        iniSelectServerAdapter()
        setupMenu()

        binding.btSync.setOnClickListener {
            if (Filter.listServer.isEmpty())
                Toast.makeText(requireContext(),"server not found", Toast.LENGTH_SHORT).show()
            else {
                Filter.cid=selectedServer.cid
                Filter.user=Data.User(selectedServer.userType,selectedServer.userCode)

                selectedServer.dbaseVersion=dbaseVersion
                selectedServer.isLocal = binding.rbOffice.isChecked
                Filter.listServer.replaceAll { server -> if (server.cid == selectedServer.cid) selectedServer else server }
                val gson = Gson()
                val jsonString = gson.toJson(Filter.listServer)
                val sh = activity?.getSharedPreferences("servers", Context.MODE_PRIVATE)
                if (sh != null) {
                    with(sh.edit()) {
                        putString("listServer", jsonString)
                        apply()
                    }
                }
                Sync.cid=selectedServer.cid
                val intent = Intent(activity, SyncActivity::class.java)
                resultLauncher.launch(intent)
            }
        }

        binding.rbLastSync.setOnClickListener {
            val now = LocalDate.now()
            val lsDate = selectedServer.lastSync.toLocalDate()
            Sync.dateFrom=lsDate.toDateString()
            Sync.dateTo=now.toDateString()
            val period="${Sync.dateFrom} - ${Sync.dateTo}"
            binding.tvPeriod.text=period
            Sync.mode=1
        }

        binding.rbLastYear.setOnClickListener {
            val now = LocalDate.now()
            Sync.dateFrom = now.minusMonths(12).monthFirstDate()
            Sync.dateTo = now.monthLastDate()
            val period="${Sync.dateFrom} - ${Sync.dateTo}"
            binding.tvPeriod.text=period
            Sync.mode=2
        }

        binding.rbPeriod.setOnClickListener {
            val selectPeriodDialog= SelectPeriodDialog()
            val args = Bundle()
            args.putString("source", "fragment")
            selectPeriodDialog.arguments = args
            selectPeriodDialog.show(
                childFragmentManager, "selectPeriod")
        }

        return binding.root
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedServer = if (Build.VERSION.SDK_INT >= 33) {
                data!!.getParcelableExtra("server", Data.Server::class.java)!!
            } else {
                data!!.getParcelableExtra("server")!!
            }
            updateStatus()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu itemm
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_reset, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item

                return when (menuItem.itemId) {
                    R.id.menu_reset -> {
                        resetDbase()
                        true
                    }


                    else -> false}
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun iniSelectServerAdapter(){
        val dropdownServerListAdapter= DropdownServerListAdapter(requireContext(), R.layout.item_dropdown)
        binding.actServer.setAdapter(dropdownServerListAdapter)
        binding.actServer.setOnItemClickListener { _, _, position, _ ->
            selectedServer= dropdownServerListAdapter.getItem(position)
            binding.actServer.setText(selectedServer.name)
            updateStatus()
        }

        dropdownServerListAdapter.setList(Filter.listServer)
        if (Filter.listServer.isNotEmpty()) {
            selectedServer = if (Filter.cid!="")
                Filter.listServer.filter { it.cid==Filter.cid }[0]
            else
                Filter.listServer[0]

            binding.actServer.setText(selectedServer.name)
            val job = Job()
            val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
            val scopeIO = CoroutineScope(job + Dispatchers.IO)
            scopeIO.launch {
                dbaseVersion=AppDatabase.getDatabase(requireContext(),this).getDBVersion().toString()
                scopeMainThread.launch {
                    updateStatus()
                }
            }
        }
    }

    private fun updateStatus(){
        binding.rbLastSync.isChecked=true
        Sync.mode=1
        binding.rbLastSync.isEnabled=true
        binding.rbPeriod.isEnabled=true
        if (selectedServer.dbaseVersion!=dbaseVersion)  selectedServer.lastSync=""
        if (selectedServer.isLocal)
            binding.rbOffice.isChecked=true
        else
            binding.rbOnsite.isChecked=true

        var status="Initial Sync"
        var lastSync="-"
        var statusColor = requireContext().resolveColorAttr(android.R.attr.textColorSecondary)
        var btText="Sync"
        if (Sync.listData.any { it.progress<100.0 } && Sync.cid==selectedServer.cid) {
            status="Incomplete"
            btText="Retry"
            statusColor= ContextCompat.getColor(requireContext(), R.color.textWarning)
        }else {
            val now = LocalDate.now()
            if (selectedServer.lastSync.isNotEmpty()) {
//                Sync.initial=false
                Sync.mode=1
                val lsDate = selectedServer.lastSync.toLocalDate()
                lastSync=lsDate.toDateString("MMM dd, yyyy")
                Sync.dateFrom=lsDate.toDateString()
                Sync.dateTo=now.toDateString()
                if (now.toDateString()==lsDate.toDateString())  {
                    status="Updated"
                    statusColor= ContextCompat.getColor(requireContext(), R.color.green)
                }else
                    status="Out of Sync"
            }else {
                Sync.dateFrom = now.minusMonths(14).monthFirstDate()
                Sync.dateTo = now.monthLastDate()
                binding.rbLastYear.isChecked=true
                Sync.mode=2
                binding.rbLastSync.isEnabled=false
                binding.rbPeriod.isEnabled=false
            }
        }
        val period="${Sync.dateFrom} - ${Sync.dateTo}"
        binding.tvPeriod.text=period
        binding.tvStatus.text=status
        binding.tvStatus.setTextColor(statusColor)
        binding.tvLastSync.text=lastSync
        binding.btSync.text=btText
    }

    override fun onFinishSelectPeriodDialog(m: Int, y: Int) {
        val date= LocalDate.of(y, m, 1)
        Sync.dateFrom=date.minusMonths(2).toDateString()
        Sync.dateTo=date.monthLastDate()
        val period="${Sync.dateFrom} - ${Sync.dateTo}"
        binding.tvPeriod.text=period
        Sync.mode=3
    }

    private fun resetDbase(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Reset Database")
        builder.setMessage("Unsynchronized data will also be deleted, are you sure?")
            .setCancelable(true)
            .setPositiveButton("ok") { _, _ ->

                val f = AdminValidationDialog()
                val args = Bundle()
                args.putString("source", "fragment")
                f.arguments = args
                f.isCancelable=false
                f.showNow(
                    this.childFragmentManager,
                    "AdminValidationDialog"
                )

            }
            .setNegativeButton("cancel") { _, _ ->
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onFinishValidationDialog(pin: String) {
        if (Utils.validateAdminPin(pin)) {
            val job = Job()
            val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
            val scopeIO = CoroutineScope(job + Dispatchers.IO)
            scopeIO.launch {
                val dbase=AppDatabase.getDatabase(requireContext(),this)
                dbase.clearAllTables()
                dbase.resetDbaseDao().clearPrimaryKeyIndex()
                Filter.listServer.forEach {
                    it.lastSync=""
                }
                scopeMainThread.launch {
                    updateStatus()
                }
            }
        }else
            Toast.makeText(requireContext(),"Invalid pin",Toast.LENGTH_SHORT).show()
    }
}





