package com.example.poultry2.ui


import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.poultry2.R
import com.example.poultry2.databinding.ActivityMainBinding
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.sync.Sync
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Filter.getServersFromDevice(this)
        Filter.setDates(LocalDate.now().withDayOfMonth(1), LocalDate.now())

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_sync,
                R.id.nav_user,
                R.id.nav_servers
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val btExit = navView.getHeaderView(0).findViewById<ImageButton>(R.id.bt_exit)
        btExit.setOnClickListener {
            finishAndRemoveTask()
        }

        getFilterSupervisor()



//        askForStoragePermission()

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }





//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//        }
//
//    }


//    private val writeStoragePermissionResult =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> }
//
//    private fun askForStoragePermission(): Boolean =
//        if (hasPermissions(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//        ) {
//            true
//        } else {
//
//            writeStoragePermissionResult.launch(
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                )
//            )
//            false
//        }
//
//    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
//        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//    }

    private fun getFilterSupervisor(){
//        val job = Job()
//        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
//        val scopeIO = CoroutineScope(job + Dispatchers.IO)
//        scopeIO.launch {
//            val vm = ViewModelProvider(this@MainActivity)[SovViewModel::class.java]
//            Filter.listSupervisor =
//                vm.getFilterSupervisor(Filter.cid)
//                    .toMutableList()
//        }
    }

}