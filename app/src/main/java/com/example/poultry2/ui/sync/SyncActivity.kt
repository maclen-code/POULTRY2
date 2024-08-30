package com.example.poultry2.ui.sync

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.ColumnInfo
import com.example.poultry2.data.Data
import com.example.poultry2.data.MSSQL
import com.example.poultry2.data.accountTarget.AccountTarget
import com.example.poultry2.data.accountTarget.AccountTargetViewModel
import com.example.poultry2.data.address.Address
import com.example.poultry2.data.address.AddressViewModel
import com.example.poultry2.data.ar.Ar
import com.example.poultry2.data.ar.ArViewModel
import com.example.poultry2.data.dspTarget.DspTarget
import com.example.poultry2.data.dspTarget.DspTargetViewModel

import com.example.poultry2.data.siv.Siv
import com.example.poultry2.data.siv.SivViewModel
import com.example.poultry2.data.sivTarget.SivTarget
import com.example.poultry2.data.sivTarget.SivTargetViewModel
import com.example.poultry2.data.sov.Sov
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.data.sovPromoDisc.SovPromoDisc
import com.example.poultry2.data.sovPromoDisc.SovPromoDiscViewModel
import com.example.poultry2.databinding.ActivitySyncBinding
import com.example.poultry2.ui.function.MyDate
import com.example.poultry2.ui.function.MyDate.monthFirstDate
import com.example.poultry2.ui.function.MyDate.monthLastDate
import com.example.poultry2.ui.function.MyDate.toDateString
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.global.filter.Filter
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.CallableStatement
import java.sql.Connection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


class SyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySyncBinding
//    private var lastSync: Date?=null
    private lateinit var  server: Data.Server
    private var processing=false
//    private var dateFrom=""
//    private var dateTo=""
    private lateinit var  adapter: SyncAdapter

    private var itemProgress=0
    private var itemMaxProgress=0
    private var itemAllProgress=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title ="Sync"

        server=Filter.listServer.filter { it.cid==Sync.cid }[0]
        binding.tvServer.text=server.name

        adapter = SyncAdapter(this)
        binding.rvList.adapter = adapter
        binding.rvList.itemAnimator?.changeDuration = 0

        var spanCount=5
        if (Utils.orientation(this)=="p") spanCount=3
        binding.rvList.setLayoutManager(GridLayoutManager(this, spanCount))

        val date= LocalDate.now()
        binding.tvToday.text=date.format(DateTimeFormatter.ofPattern("yyyy MMMM dd"))

        val ls= server.lastSync
        if (ls!="") {
            val lsDate = LocalDate.parse(ls)
            binding.tvLastSync.text = lsDate.format(DateTimeFormatter.ofPattern("yyyy MMMM dd"))
            binding.tvLastSync.tag=lsDate.toDateString()
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            /* override back pressing */
            override fun handleOnBackPressed() {
                if (!processing) {
                    val i = Intent()
                    i.putExtra("server", server)
                    setResult(RESULT_OK, i)
                    finish()
                }
            }
        })

        prepareSyncList()



        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let { it ->
            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //take action when network connection is gained
                    val completed= Sync.listData.filter { it.progress==100.0 }.size
                    val totalList= Sync.listData.size
                    if (!processing && completed>0 && completed<totalList) sync()

                }

                @SuppressLint("SetTextI18n")
                override fun onLost(network: Network) {
                    //take action when network connection is lost

                }
            })
        }

        processing=true

        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        sync()
                    }
                }
            }, 500
        )

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val newOrientation: Int = newConfig.orientation
        var spanCount=5
        if (newOrientation == Configuration.ORIENTATION_PORTRAIT) spanCount=3
        binding.rvList.setLayoutManager(GridLayoutManager(this, spanCount))
    }

    @SuppressLint("SetTextI18n")
    private fun prepareSyncList(){
        Sync.createSyncList()

        val completed= Sync.listData.filter { it.progress==100.0 }.size
        val totalList= Sync.listData.size
        itemAllProgress=0
        if (completed==totalList){
            Sync.listData.forEach { s->
                s.process="pending"
                s.progress=0.0
                s.status=""
            }
        }else {
            itemAllProgress=((completed.toDouble() / totalList) * 100).toInt()
        }
        binding.tvProgress.text=Utils.formatIntToString(itemAllProgress,"0.0") + " %"
        binding.pbAll.progress=itemAllProgress
        adapter.setData(Sync.listData)
        adapter.onItemClick = {
            Toast.makeText(this,it.error,Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sync(){
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val completed=Sync.listData.filter { it.progress==100.0 }.size
        val totalList=Sync.listData.size
        if (Sync.cid!=server.cid || completed==totalList ) {
            var i=0
            Sync.listData.forEach { s->
                s.progress=0.0
                s.status="pending"
                i++
                adapter.notifyItemChanged(i)
            }
        }
        Sync.cid=server.cid
        binding.tvLog.text = ""

        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            Sync.listData.forEach { t ->
                if (t.progress<100.0) {
                    when (t.dataName) {
                        "Siv Target" -> {
                            if (t.download)
                                downloadSivTargetHistory(t)
//                            else
//                                uploadSivTarget(t)
                        }
                        "Dsp Target" -> {
                            if (t.download)
                                downloadDspTargetHistory(t)
//                            else
//                                uploadDspTarget(t)
                        }
                        "Account Target" -> {
                            if (t.download)
                                downloadAccountTargetHistory(t)
//                            else
//                                uploadAccountTarget(t)
                        }
                        "Siv" -> downloadSivHistory( t)
                        "Sov" -> downloadSovHistory( t)
                        "Sov Promo" -> downloadSovPromoHistory( t)
                        "Address" -> downloadAddress( t)
                        "AR" -> downloadAr( t)
                    }
                }
            }

            scopeMainThread.launch {

                processing=false

                val date= LocalDate.now()
                binding.tvLastSync.text=date.toDateString("yyyy MMMM dd")

                server.lastSync=date.toDateString()
                Filter.listServer.replaceAll { s -> if (s.cid == server.cid) server else s }
                val gson = Gson()
                val jsonString = gson.toJson(Filter.listServer)
                val sh = this@SyncActivity.getSharedPreferences("servers", Context.MODE_PRIVATE)
                if (sh != null) {
                    with(sh.edit()) {
                        putString("listServer", jsonString)
                        apply()
                    }
                }
                Filter.setDates(LocalDate.now().withDayOfMonth(1), LocalDate.now())
                Filter.cid=server.cid

                Filter.user=Data.User(server.userType,server.userCode)
                Filter.listSupervisor.clear()
                Filter.sno=""
                Filter.listTransType.clear()
                Filter.transType=""
                Filter.updated.postValue(true)
               this@SyncActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun syncItemGetData(t: Data.Sync){
        if (t.status=="error") return
        val i=Sync.listData.indexOf(t)
        t.process="get"
        t.status="processing..."
        Handler(Looper.getMainLooper()).post {
            adapter.notifyItemChanged(i)
        }
        delay(10)
    }
    @SuppressLint("SetTextI18n")

    private fun syncItemProgress(t: Data.Sync, ctr: Int,max:Int){
        val currentProgress:Double=itemProgress.toDouble()+ (ctr.toDouble()/max)
        val i=Sync.listData.indexOf(t)
        val progress=(currentProgress/itemMaxProgress.toDouble())*100
        t.process="processing"
        t.progress= progress
        t.status= Utils.formatDoubleToString(progress) + " %"


        val currentAllProgress:Double=itemAllProgress.toDouble() +
                (currentProgress/itemMaxProgress.toDouble())

        val allProgress=(currentAllProgress /Sync.listData.size)*100

        Handler(Looper.getMainLooper()).post {
            adapter.notifyItemChanged(i)
            binding.tvProgress.text=Utils.formatDoubleToString(allProgress) + " %"
            binding.pbAll.progress=allProgress.toInt()
            binding.pbAll.invalidate()
        }
    }
    @SuppressLint("SetTextI18n")
    private suspend fun syncItemCompleted( t: Data.Sync, record: Int){
        val i=Sync.listData.indexOf(t)
        if (t.process!="error") {
            t.process="done"
            if (record==0) t.status="no record" else t.status= Utils.formatIntToString(record)
            t.progress=100.0
        }
        itemAllProgress+=1
        val allProgress=(itemAllProgress.toDouble() /Sync.listData.size)*100
        Handler(Looper.getMainLooper()).post {
            adapter.notifyItemChanged(i)
            binding.tvProgress.text=Utils.formatDoubleToString(allProgress) + " %"
            binding.pbAll.progress=allProgress.toInt()
        }
        delay(10)
    }
    private suspend fun syncError(t: Data.Sync,error:String){
        val i=Sync.listData.indexOf(t)
        Handler(Looper.getMainLooper()).post {
            t.process="error"
            t.status="Found Error"
            t.error=error
            adapter.notifyItemChanged(i)
        }
        delay(10)
    }
    private suspend fun downloadSivHistory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val now=LocalDate.now()
        val firstDay=now.monthFirstDate().toLocalDate()
        var refreshDate=firstDay
        if (now.dayOfMonth<6) refreshDate=firstDay.minusMonths(1)

        val from=Sync.dateFrom.toLocalDate()
        var startDate =Sync.dateFrom.toLocalDate().monthFirstDate().toLocalDate()
        if (from>=firstDay && from.dayOfMonth<6) startDate = startDate.minusMonths(1)

        val months= MyDate.monthsBetween(Sync.dateFrom,Sync.dateTo)
        itemMaxProgress=months+1
        while (startDate <= Sync.dateTo.toLocalDate()) {
            val vm = ViewModelProvider(this)[SivViewModel::class.java]
            val cid = vm.getCid(
                server.cid,
                startDate.toDateString(),
                startDate.monthLastDate()
            )
            if (startDate>=refreshDate || cid == null) {
                val cnt = downloadSiv(
                    t,
                    startDate.toDateString(),
                    startDate.monthLastDate()
                )
                total += cnt
                if (cnt == -1) return
            }
            startDate = startDate.plusMonths(1)
            itemProgress+=1
        }
        syncItemCompleted(t,total)
    }
    private suspend fun downloadSiv( t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)
        val sp = "{Call spm_Poultry_Download_Siv (?,?,?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.setString("@dateFrom", dateFrom)
                cs.setString("@dateTo", dateTo)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[SivViewModel::class.java]
                    vm.deletePeriod(server.cid,dateFrom,dateTo)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = Siv( 0,
                            rs.getString("cid"),
                            rs.getString("tradeCode").uppercase(Locale.ROOT),
                            rs.getString("tradeType").uppercase(Locale.ROOT),
                            rs.getInt("clusterId"),
                            rs.getString("cluster").uppercase(Locale.ROOT),
                            rs.getString("date"),
                            rs.getString("sno"),
                            rs.getString("supervisor").uppercase(Locale.ROOT),
                            rs.getString("invoiceNo"),
                            rs.getString("itemId"),
                            rs.getString("itemCode").uppercase(Locale.ROOT),
                            rs.getString("itemDesc").uppercase(Locale.ROOT),
                            rs.getString("bUnitId"),
                            rs.getString("bUnit").uppercase(Locale.ROOT),
                            rs.getString("catId"),
                            rs.getString("category").uppercase(Locale.ROOT),
                            rs.getInt("Qty"),
                            rs.getString("retailUnit").uppercase(Locale.ROOT),
                            rs.getDouble("volume"),
                            rs.getString("volumeUnit").uppercase(Locale.ROOT),
                            rs.getDouble("totalNet")
                        )

                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                }
            } catch (e: Exception) {
                syncError(t,e.message!!)
                return -1
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
            return -1
        }
        return max
    }
    private suspend fun downloadSivTargetHistory(t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val now=LocalDate.now()
        val firstDay=now.monthFirstDate().toLocalDate()
        var refreshDate=firstDay
        if (now.dayOfMonth<6) refreshDate=firstDay.minusMonths(1)
        var startDate =Sync.dateFrom.toLocalDate()
        val months= MyDate.monthsBetween(Sync.dateFrom,Sync.dateTo)
        itemMaxProgress=months+1
        while (startDate <= Sync.dateTo.toLocalDate()) {
            var endDate=startDate.monthLastDate()
            if (endDate.toLocalDate()>Sync.dateTo.toLocalDate()) endDate=Sync.dateTo
            val vm = ViewModelProvider(this)[SivTargetViewModel::class.java]
            val cid = vm.getCid(
                server.cid,
                startDate.toDateString(),
                startDate.monthLastDate()
            )
            if (startDate>=refreshDate || cid == null) {
                val cnt = downloadSivTarget(
                    t,
                    startDate.toDateString(),
                    endDate
                )
                total += cnt
                if (cnt == -1) return
            }
            startDate = startDate.plusMonths(1)
            itemProgress+=1
        }

        syncItemCompleted(t,total)

    }
    private suspend fun downloadSivTarget( t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)
        val sp = "{Call spm_poultry_Download_Siv_Target (?,?,?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.setString("@dateFrom", dateFrom)
                cs.setString("@dateTo", dateTo)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[SivTargetViewModel::class.java]
                    if (Sync.mode!=1) vm.deletePeriod(server.cid,dateFrom,dateTo)

                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = SivTarget(rs.getString("sno"),
                            rs.getString("cid"),
                            rs.getInt("clusterId"),
                            rs.getString("date"),
                            rs.getInt("VolumeTarget"),
                            rs.getInt("AmountTarget"),
                            true
                        )
                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                }
            } catch (e: Exception) {
                syncError(t,e.message!!)
                return -1
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
            return -1
        }
        return max
    }
    private suspend fun downloadSovHistory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val now=LocalDate.now()
        val firstDay=now.monthFirstDate().toLocalDate()
        var refreshDate=firstDay
        if (now.dayOfMonth<6) refreshDate=firstDay.minusMonths(1)

        val from=Sync.dateFrom.toLocalDate()
        var startDate =Sync.dateFrom.toLocalDate().monthFirstDate().toLocalDate()
        if (from>=firstDay && from.dayOfMonth<6) startDate = startDate.minusMonths(1)

        val months= MyDate.monthsBetween(Sync.dateFrom,Sync.dateTo)
        itemMaxProgress=months+1
        while (startDate <= Sync.dateTo.toLocalDate()) {
            val vm = ViewModelProvider(this)[SovViewModel::class.java]
            val cid = vm.getCid(
                server.cid,
                startDate.toDateString(),
                startDate.monthLastDate()
            )
            if (startDate>=refreshDate || cid == null) {
                val cnt = downloadSov(
                    t,
                    startDate.toDateString(),
                    startDate.monthLastDate()
                )
                total += cnt
                if (cnt == -1) return
            }

            startDate = startDate.plusMonths(1)
            itemProgress+=1
        }
        syncItemCompleted(t,total)
    }
    private suspend fun downloadSov( t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)
        val sp = "{Call spm_Poultry_Download_Sov (?,?,?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.setString("@dateFrom", dateFrom)
                cs.setString("@dateTo", dateTo)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[SovViewModel::class.java]
                    vm.deletePeriod(server.cid,dateFrom,dateTo)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = Sov( 0,
                            rs.getString("productType").uppercase(Locale.ROOT),
                            rs.getString("transType").uppercase(Locale.ROOT),
                            rs.getString("transId"),
                            rs.getString("cid"),
                            rs.getString("sno"),
                            rs.getString("supervisor").uppercase(Locale.ROOT),
                            rs.getString("tradeCode").uppercase(Locale.ROOT),
                            rs.getString("tradeType").uppercase(Locale.ROOT),
                            rs.getInt("clusterId"),
                            rs.getString("cluster").uppercase(Locale.ROOT),
                            rs.getString("rid"),
                            rs.getString("dsp").uppercase(Locale.ROOT),
                            rs.getString("customerNo"),
                            rs.getString("customer").uppercase(Locale.ROOT),
                            rs.getString("acctNo"),
                            rs.getString("storeName").uppercase(Locale.ROOT),
                            rs.getString("address").uppercase(Locale.ROOT),
                            rs.getString("barangay").uppercase(Locale.ROOT),
                            rs.getString("city").uppercase(Locale.ROOT),
                            rs.getString("channel").uppercase(Locale.ROOT),
                            rs.getString("date"),
                            rs.getString("invoiceNo"),
                            rs.getString("itemId"),
                            rs.getString("itemCode").uppercase(Locale.ROOT),
                            rs.getString("itemDesc").uppercase(Locale.ROOT),
                            rs.getString("bUnitId"),
                            rs.getString("bUnit").uppercase(Locale.ROOT),
                            rs.getString("catId"),
                            rs.getString("category").uppercase(Locale.ROOT),
                            rs.getInt("Qty"),
                            rs.getString("retailUnit").uppercase(Locale.ROOT),
                            rs.getDouble("volume"),
                            rs.getString("volumeUnit").uppercase(Locale.ROOT),
                            rs.getDouble("totalNet")
                        )

                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                }
            } catch (e: Exception) {
                syncError(t,e.message!!)
                return -1
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
            return -1
        }
        return max
    }

    private suspend fun downloadSovPromoHistory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val now=LocalDate.now()
        val firstDay=now.monthFirstDate().toLocalDate()
        var refreshDate=firstDay
        if (now.dayOfMonth<6) refreshDate=firstDay.minusMonths(1)

        val from=Sync.dateFrom.toLocalDate()
        var startDate =Sync.dateFrom.toLocalDate().monthFirstDate().toLocalDate()
        if (from>=firstDay && from.dayOfMonth<6) startDate = startDate.minusMonths(1)

        val months= MyDate.monthsBetween(Sync.dateFrom,Sync.dateTo)
        itemMaxProgress=months+1
        while (startDate <= Sync.dateTo.toLocalDate()) {
            val vm = ViewModelProvider(this)[SovPromoDiscViewModel::class.java]
            val cid = vm.getCid(
                server.cid,
                startDate.toDateString(),
                startDate.monthLastDate()
            )
            if (startDate>=refreshDate || cid == null) {
                val cnt = downloadSovPromo(
                    t,
                    startDate.toDateString(),
                    startDate.monthLastDate()
                )
                total += cnt
                if (cnt == -1) return
            }

            startDate = startDate.plusMonths(1)
            itemProgress+=1
        }
        syncItemCompleted(t,total)
    }
    private suspend fun downloadSovPromo( t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)
        val sp = "{Call spm_Poultry_Download_Sov_PromoDisc (?,?,?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.setString("@dateFrom", dateFrom)
                cs.setString("@dateTo", dateTo)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[SovPromoDiscViewModel::class.java]
                    vm.deletePeriod(server.cid,dateFrom,dateTo)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = SovPromoDisc( 0,
                            rs.getString("cid"),
                            rs.getString("sno"),
                            rs.getInt("clusterId"),
                            rs.getString("cluster").uppercase(Locale.ROOT),
                            rs.getString("date"),
                            rs.getDouble("promoDiscount")
                        )

                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                }
            } catch (e: Exception) {
                syncError(t,e.message!!)
                return -1
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
            return -1
        }
        return max
    }
    private suspend fun downloadAddress( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        syncItemGetData(t)
        val sp = "{Call spm_Poultry_Download_Address_List (?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setBoolean("@initial", Sync.mode==2)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[AddressViewModel::class.java]
                    if (Sync.mode==2) vm.deleteAll()
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = Address( rs.getString("Id"),
                            rs.getString("Name").uppercase(Locale.ROOT),
                            rs.getString("GLevel").uppercase(Locale.ROOT)
                        )
                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                    syncItemCompleted(t,max)
                }
            } catch (e: Exception) {
                syncError(t,e.message!!)
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
        }
    }

    private suspend fun downloadDspTargetHistory(t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val now=LocalDate.now()
        val firstDay=now.monthFirstDate().toLocalDate()
        var refreshDate=firstDay
        if (now.dayOfMonth<6) refreshDate=firstDay.minusMonths(1)
        var startDate =Sync.dateFrom.toLocalDate()
        val months= MyDate.monthsBetween(Sync.dateFrom,Sync.dateTo)
        itemMaxProgress=months+1
        while (startDate <= Sync.dateTo.toLocalDate()) {
            var endDate=startDate.monthLastDate()
            if (endDate.toLocalDate()>Sync.dateTo.toLocalDate()) endDate=Sync.dateTo
            val vm = ViewModelProvider(this)[DspTargetViewModel::class.java]
            val cid = vm.getCid(
                server.cid,
                startDate.toDateString(),
                startDate.monthLastDate()
            )
            if (startDate>=refreshDate || cid == null) {
                val cnt = downloadDspTarget(
                    t,
                    startDate.toDateString(),
                    endDate
                )
                total += cnt
                if (cnt == -1) return
            }
            startDate = startDate.plusMonths(1)
            itemProgress+=1
        }

        syncItemCompleted(t,total)

    }
    private suspend fun downloadDspTarget(t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)
        val sp = "{Call spm_poultry_Download_Dsp_Target (?,?,?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.setString("@dateFrom", dateFrom)
                cs.setString("@dateTo", dateTo)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[DspTargetViewModel::class.java]
                    if (Sync.mode!=1) vm.deletePeriod(server.cid,dateFrom,dateTo)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = DspTarget(
                            rs.getString("rid"),
                            rs.getString("cid"),
                            rs.getString("sno"),
                            rs.getInt("clusterId"),
                            rs.getString("date"),
                            rs.getInt("VolumeTarget"),
                            rs.getInt("AmountTarget"),
                            true
                        )
                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                }

            } catch (e: Exception) {
                syncError(t,e.message!!)
                return -1
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
            return -1
        }
        return max
    }

    private suspend fun downloadAr(t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        syncItemGetData(t)
        val sp = "{Call spm_poultry_Download_Ar (?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[ArViewModel::class.java]
                    vm.deleteAll(server.cid)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = Ar(0,
                            rs.getString("cid"),
                            rs.getString("sno"),
                            rs.getString("supervisor").uppercase(Locale.ROOT),
                            rs.getString("tradeCode").uppercase(Locale.ROOT),
                            rs.getString("tradeType").uppercase(Locale.ROOT),
                            rs.getInt("clusterId"),
                            rs.getString("cluster").uppercase(Locale.ROOT),
                            rs.getString("rid"),
                            rs.getString("dsp").uppercase(Locale.ROOT),
                            rs.getString("customerNo"),
                            rs.getString("customer").uppercase(Locale.ROOT),
                            rs.getString("acctNo"),
                            rs.getString("storeName").uppercase(Locale.ROOT),
                            rs.getString("address").uppercase(Locale.ROOT),
                            rs.getString("barangay").uppercase(Locale.ROOT),
                            rs.getString("city").uppercase(Locale.ROOT),
                            rs.getString("channel").uppercase(Locale.ROOT),
                            rs.getString("date"),
                            rs.getString("invoiceNo"),
                            rs.getString("terms").uppercase(Locale.ROOT),
                            rs.getString("balanceType").uppercase(Locale.ROOT),
                            rs.getInt("daysDue"),
                            rs.getInt("agingId"),
                            rs.getString("aging").uppercase(Locale.ROOT),
                            rs.getString("dueDate"),
                            rs.getDouble("balance"),
                            rs.getString("checkNo").uppercase(Locale.ROOT),
                            rs.getString("checkDate")
                        )

                        vm.insert(item)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                    syncItemCompleted(t,max)
                }

            } catch (e: Exception) {
                syncError(t,e.message!!)
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
        }
    }

    private suspend fun downloadAccountTargetHistory(t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val now=LocalDate.now()
        val firstDay=now.monthFirstDate().toLocalDate()
        var refreshDate=firstDay
        if (now.dayOfMonth<6) refreshDate=firstDay.minusMonths(1)
        var startDate =Sync.dateFrom.toLocalDate()
        val months= MyDate.monthsBetween(Sync.dateFrom,Sync.dateTo)
        itemMaxProgress=months+1
        while (startDate <= Sync.dateTo.toLocalDate()) {
            var endDate=startDate.monthLastDate()
            if (endDate.toLocalDate()>Sync.dateTo.toLocalDate()) endDate=Sync.dateTo
            val vm = ViewModelProvider(this)[AccountTargetViewModel::class.java]

            val cid = vm.getCid(
                server.cid,
                startDate.toDateString(),
                startDate.monthLastDate()
            )
            if (startDate>=refreshDate || cid == null) {
                val cnt = downloadAccountTarget(
                    t,
                    startDate.toDateString(),
                    endDate
                )


                total += cnt
                if (cnt == -1) return
            }
            startDate = startDate.plusMonths(1)
            itemProgress+=1
        }
        syncItemCompleted(t,total)

    }

    private suspend fun downloadAccountTarget(t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)

        val sp = "{Call spm_poultry_Download_Account_Target (?,?,?,?)}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.setString("@UserType", Filter.user.userType)
                cs.setString("@UserCode", Filter.user.userCode)
                cs.setString("@dateFrom", dateFrom)
                cs.setString("@dateTo", dateTo)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()
                if (rs != null) {
                    val vm= ViewModelProvider(this)[AccountTargetViewModel::class.java]
                    if (Sync.mode!=1) vm.deletePeriod(server.cid,dateFrom,dateTo)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")


                        val accountTarget = AccountTarget(
                            rs.getString("cid"),
                            rs.getString("acctNo"),
                            rs.getString("customerNo"),
                            rs.getString("date"),
                            rs.getInt("VolumeTarget"),
                            rs.getInt("AmountTarget"),
                            true
                        )
                        vm.insert(accountTarget)

                        ctr += 1
                        delay(1) // Simulate a more complex calculation
                        syncItemProgress(t,ctr,max)
                    }
                }

            } catch (e: Exception) {
                syncError(t,e.message!!)
                return -1
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
            return -1
        }
        return max
    }
}