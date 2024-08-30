package com.example.poultry2.ui.server

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ActivityServerEntryBinding
import com.example.poultry2.ui.function.Access
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.AdminValidationDialog
import com.example.poultry2.ui.global.filter.Filter
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale


class ServerEntryActivity : AppCompatActivity(), AdminValidationDialog.DialogListener  {
    private lateinit var binding: ActivityServerEntryBinding
    private var cid=""
    private lateinit var server:Data.Server

    private lateinit var userTypeListAdapter: UserTypeListAdapter
    private var selectedUserType: Data.UserType?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityServerEntryBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val actionbar = supportActionBar
        actionbar!!.title = "New Server"
        actionbar.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent.hasExtra("cid")) {
            cid = intent.getStringExtra("cid").toString()
            server=Filter.listServer.filter { it.cid==cid }[0]
            actionbar.title = "Update Server"
            displayServer(server)
        }
        setupMenu()
        iniUserTypeAdapter()
    }

    private fun iniUserTypeAdapter(){
        userTypeListAdapter= UserTypeListAdapter(this, R.layout.item_dropdown)
        binding.actUserType.setAdapter(userTypeListAdapter)
        binding.actUserType.setOnItemClickListener { _, _, position, _ ->
            selectedUserType= userTypeListAdapter.getItem(position)
            binding.actUserType.setText(selectedUserType!!.description)
        }
        userTypeListAdapter.setList(Access.listUserType)
    }

    private fun displayServer(server: Data.Server) {
        try {
            binding.etCid.setText(server.cid)
            binding.etName.setText(server.name)
            binding.etLocalIp.setText(server.localIp)
            binding.etPublicIp.setText(server.publicIp)
            binding.etDatabase.setText(server.database)
            binding.etPassword.setText(server.password)

            selectedUserType=Access.listUserType.first { it.code==server.userType}
            binding.actUserType.setText(selectedUserType!!.description)

            binding.etUserCode.setText(server.userCode)
        }catch (_:Exception) {

        }
    }

    private fun setupMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
                val item = menu.findItem(R.id.menu_delete)
                if (cid =="") item.isVisible = false //hide it
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_delete, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return when (menuItem.itemId) {
                    R.id.menu_delete-> {
                        delete()
                        true
                    }
                    else -> false}
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun delete(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Server")
        builder.setMessage("Are you sure?")
            .setCancelable(false)
            .setPositiveButton("ok") { _, _ ->
                Filter.listServer.remove(server)
                if (Filter.listServer.isNotEmpty()) {
                    Filter.cid = Filter.listServer[0].cid
                    Filter.saveServerToDevice(this)
                }
                else {
                    this.getSharedPreferences("servers", 0).edit().clear().apply();
                }
                Filter.updated.postValue(true)
                finish()

            }
            .setNegativeButton("cancel") { _, _ ->
            }
        val alert = builder.create()
        alert.show()

    }

    fun onImportClick(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        Intent.createChooser(intent, "Choose a file")
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data

            try {
                intent!!.data!!.let {
                    val resolver = this.contentResolver
                    resolver.openInputStream(it)
                }?.let { it ->
                    val r = BufferedReader(InputStreamReader(it))
                    var strServer =""
                    while (true) {
                        val line: String = r.readLine() ?: break
                        strServer += line
                    }
                    val gson = Gson()
                    val server: Data.Server = gson.fromJson(strServer, Data.Server::class.java)
                    server.name=server.name.uppercase()
                    server.userCode=server.userCode.uppercase()
                    displayServer(server)
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onSaveClick(view: View) {
        if (!isValidInput()) return
        val f = AdminValidationDialog()
        val args = Bundle()
        args.putString("source", "activity")
        f.arguments = args
        f.isCancelable=false
        f.showNow(
            this.supportFragmentManager,
            "AdminValidationDialog"
        )
    }

    override fun onFinishValidationDialog(pin:String) {
        if (Utils.validateAdminPin(pin)) {
            save()
        }else
            Toast.makeText(this,"Invalid pin",Toast.LENGTH_SHORT).show()
    }

    private fun isValidInput():Boolean{
        val err: String
        if (binding.etCid.text.toString()=="" || binding.etCid.text.toString().length!=2)
            err="Invalid company id"
        else if (binding.etName.text.toString()=="")
            err="Invalid server name"
        else if (binding.etLocalIp.text.toString()=="")
            err="Invalid local ip"
        else if (binding.etPublicIp.text.toString()=="")
            err="Invalid public ip"
        else if (binding.etDatabase.text.toString()=="")
            err="Invalid database"
        else if (binding.etPassword.text.toString()=="")
            err="Invalid password"
        else if (selectedUserType==null)
            err="Invalid user type"
        else if (selectedUserType!!.code!="MG" && binding.etUserCode.text.toString()=="")
            err="Invalid user code"
        else
            return true


        Toast.makeText(this,err,Toast.LENGTH_SHORT).show()
        return false
    }

    private fun save(){
        val server=getServer()
        if (Filter.listServer.none { it.cid == server.cid }) {
            //new
            Filter.listServer.add(server)
        }else {
            Filter.listServer.replaceAll { if (it.cid == server.cid) server else it }
        }

        Filter.saveServerToDevice(this)
        Filter.updated.postValue(true)
        finish()
    }

    private fun getServer():Data.Server{
        return Data.Server (
            binding.etCid.text.toString(),
                    binding.etName.text.toString().uppercase(Locale.ROOT),
                    binding.etLocalIp.text.toString(),
                    binding.etPublicIp.text.toString(),
                    binding.etDatabase.text.toString(),
                    binding.etPassword.text.toString(),
                    selectedUserType!!.code,
                    binding.etUserCode.text.toString().uppercase(),
                    false,"", ""
                )
    }

}
