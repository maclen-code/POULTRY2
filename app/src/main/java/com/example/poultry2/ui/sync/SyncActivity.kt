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
import com.example.poultry2.data.inventory.Inventory
import com.example.poultry2.data.inventory.InventoryViewModel

import com.example.poultry2.data.siv.Siv
import com.example.poultry2.data.siv.SivViewModel
import com.example.poultry2.data.sivTarget.SivTarget
import com.example.poultry2.data.sivTarget.SivTargetViewModel
import com.example.poultry2.data.sov.Sov
import com.example.poultry2.data.sov.SovViewModel
import com.example.poultry2.data.sovPromoDisc.SovPromoDisc
import com.example.poultry2.data.sovPromoDisc.SovPromoDiscViewModel
import com.example.poultry2.data.sovSmis.SovSmis
import com.example.poultry2.data.sovSmis.SovSmisViewModel
import com.example.poultry2.databinding.ActivitySyncBinding
import com.example.poultry2.ui.function.MyDate
import com.example.poultry2.ui.function.MyDate.millisToTime
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
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.system.measureTimeMillis


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
        val totalTime=Sync.listData.sumOf { it.time }
        binding.tvTime.text=totalTime.millisToTime()
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val scopeIO = CoroutineScope(job + Dispatchers.IO)
        scopeIO.launch {
            Sync.listData.forEach { t ->
                if (t.progress<100.0) {
                    when (t.dataName) {
                        "Siv Target" -> {
                            if (t.download){
                                val time = measureTimeMillis {
                                    downloadSivTargetHistory(t)
                                }
                                syncItemCompletedTime(t,time)
                            }
                            else{
                                val time = measureTimeMillis {
                                    uploadSivTarget(t)
                                }
                                syncItemCompletedTime(t,time)
                            }
                        }
                        "Dsp Target" -> {
                            if (t.download)
                            {
                                val time = measureTimeMillis {
                                    downloadDspTargetHistory(t)
                                }
                                syncItemCompletedTime(t,time)
                            }
                            else{
                                val time = measureTimeMillis {
                                    uploadDspTarget(t)
                                }
                                syncItemCompletedTime(t,time)
                            }
                        }
                        "Account Target" -> {
                            if (t.download) {
                                val time = measureTimeMillis {
                                    downloadAccountTargetHistory(t)
                                }
                                syncItemCompletedTime(t,time)
                            }

//                            else
//                                uploadAccountTarget(t)
                        }
                        "Siv" -> {
                            val time = measureTimeMillis {
                                downloadSivHistory(t)
                            }
                            syncItemCompletedTime(t,time)
                        }
                        "Sov" -> {
                            val time = measureTimeMillis {
                                downloadSovHistory( t)
                            }
                            syncItemCompletedTime(t,time)
                        }
                        "Sov SMIS" -> {
                            val time = measureTimeMillis {
                                downloadSovSmisHistory( t)
                            }
                            syncItemCompletedTime(t,time)
                        }
                        "Sov Promo" -> {
                            val time = measureTimeMillis {
                                downloadSovPromoHistory( t)
                            }
                            syncItemCompletedTime(t,time)
                        }
//                        "Address" -> downloadAddress( t)
                        "AR" -> {
                            val time = measureTimeMillis {
                                downloadAr( t)
                            }
                            syncItemCompletedTime(t,time)

                        }
                        "Inventory" -> {
                            val time = measureTimeMillis {
                                downloadInventory(t)
                            }
                            syncItemCompletedTime(t,time)

                        }
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
        if (t.progress>0)
            t.status+=" ...wait"
        else
            t.status="fetching data from server..."

        Handler(Looper.getMainLooper()).post {
            adapter.notifyItemChanged(i)
        }
        delay(1000)
    }
    @SuppressLint("SetTextI18n")

    private fun syncItemProgress(t: Data.Sync, ctr: Int,max:Int){
        val currentProgress:Double=itemProgress.toDouble() + (ctr.toDouble()/max)
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
        delay(1)
    }

    private suspend fun syncItemCompletedTime( t: Data.Sync,time:Long){
        val i=Sync.listData.indexOf(t)
        t.time=time
        t.status="${t.status} / ${t.time.millisToTime()}"
        Handler(Looper.getMainLooper()).post {
            adapter.notifyItemChanged(i)
            val totalTime=Sync.listData.sumOf { it.time }
            binding.tvTime.text=totalTime.millisToTime()
        }
        delay(1000)
    }

    private suspend fun syncError(t: Data.Sync,error:String){
        val i=Sync.listData.indexOf(t)
        Handler(Looper.getMainLooper()).post {
            t.process="error"
            t.status="Found Error"
            t.error=error
            adapter.notifyItemChanged(i)
        }
        delay(1)
    }

    private suspend fun downloadSivHistory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val periodCount=Sync.listPeriod.size
        itemMaxProgress=periodCount

        Sync.listPeriod.filter { it.siv }.sortedBy { it.from }.forEach { p->
            val cnt = downloadSiv(
                t,
                p.from.monthFirstDate(),
                p.to.toDateString()
            )
            total += cnt
            itemProgress+=1
        }
        itemProgress=itemMaxProgress
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
                            rs.getString("tradeCode"),
                            rs.getString("tradeType"),
                            rs.getInt("clusterId"),
                            rs.getString("cluster"),
                            rs.getString("date"),
                            rs.getString("itemId"),
                            rs.getString("itemCode"),
                            rs.getString("itemDesc"),
                            rs.getString("bUnitId"),
                            rs.getString("bUnit"),
                            rs.getString("catId"),
                            rs.getString("category"),
                            rs.getDouble("volume"),
                            rs.getString("volumeUnit"),
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
        val periodCount=Sync.listPeriod.size
        itemMaxProgress=periodCount
        Sync.listPeriod.filter { it.siv }.sortedBy { it.from }.forEach { p->
            val cnt = downloadSivTarget(
                t,
                p.from.toDateString(),
                p.to.monthLastDate()
            )
            total += cnt
            itemProgress+=1
        }
        itemProgress=itemMaxProgress
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

                        val item = SivTarget(
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

    private suspend fun uploadSivTarget(t: Data.Sync) {
        syncItemGetData(t)
        val vm= ViewModelProvider(this)[SivTargetViewModel::class.java]
        val list=vm.upload(server.cid)

        val max=list.count()
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        if (conn != null) {
            try {
                conn.autoCommit=false
                val cs=conn.prepareCall("{Call spm_poultry_Upload_Siv_Target(?,?,?,?)}")
                var ctr = 0
                list.forEach { item ->
                    cs.setInt("@clusterId", item.clusterId)
                    cs.setInt("@VolumeTarget", item.volumeTarget)
                    cs.setInt("@AmountTarget", item.amountTarget)
                    cs.setString("@Date", item.date)
                    cs.addBatch()
                    ctr += 1
                    delay(1)
                    syncItemProgress(t,ctr,max)
                }
                cs.executeBatch()
                conn.commit()
                list.forEach { item ->
                    vm.uploadSuccess(Filter.cid,item.clusterId,item.date)
                }
                syncItemCompleted(t,max)

            } catch (e: Exception) {
                syncError(t,e.message!!)
                conn.rollback()
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
        }
    }

    private suspend fun downloadSovHistory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val days=10L
        itemMaxProgress= Sync.listPeriod.sumOf { it.days}/days.toInt()
        if (itemMaxProgress<1) itemMaxProgress=1
        Sync.listPeriod.sortedBy { it.from }.forEach { p->
            var start=p.from.monthFirstDate().toLocalDate()
            var end=p.from.plusDays(days-1)
            while (start<=p.to) {
                if (end>p.to) end=p.to

                val cnt = downloadSov(
                    t,
                    start.toDateString(),
                    end.toDateString()
                )
                total += cnt

                itemProgress+=1
                start=end.plusDays(1)
                end=start.plusDays(days)
            }
        }
        itemProgress=itemMaxProgress
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
                            rs.getString("transType"),
                            rs.getString("cid"),
                            rs.getString("tradeCode"),
                            rs.getString("tradeType"),
                            rs.getInt("clusterId"),
                            rs.getString("cluster"),
                            rs.getString("rid"),
                            rs.getString("dsp"),
                            rs.getString("customerNo"),
                            rs.getString("customer"),
                            rs.getString("acctNo"),
                            rs.getString("storeName"),
                            rs.getString("channel"),
                            rs.getString("date"),
                            rs.getString("invoiceNo"),
                            rs.getString("itemId"),
                            rs.getString("itemCode"),
                            rs.getString("itemDesc"),
                            rs.getString("bUnitId"),
                            rs.getString("bUnit"),
                            rs.getString("catId"),
                            rs.getString("category"),
                            rs.getDouble("volume"),
                            rs.getString("volumeUnit"),
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

    private suspend fun downloadSovSmisHistory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val days=10L
        itemMaxProgress= Sync.listPeriod.sumOf { it.days}/days.toInt()
        if (itemMaxProgress<1) itemMaxProgress=1
        Sync.listPeriod.sortedBy { it.from }.forEach { p->
            var start=p.from.monthFirstDate().toLocalDate()
            var end=p.from.plusDays(days-1)
            while (start<=p.to) {
                if (end>p.to) end=p.to


                val cnt = downloadSovSmis(
                    t,
                    start.toDateString(),
                    end.toDateString()
                )

                total += cnt

                itemProgress+=1
                start=end.plusDays(1)
                end=start.plusDays(days)
            }
        }
        itemProgress=itemMaxProgress
        syncItemCompleted(t,total)
    }

    private suspend fun downloadSovSmis( t: Data.Sync,dateFrom:String,dateTo:String):Int {
        syncItemGetData(t)
        val sp = "{Call spm_Poultry_Download_Sov_Smis (?,?,?,?)}"
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
                    val vm= ViewModelProvider(this)[SovSmisViewModel::class.java]
                    vm.deletePeriod(server.cid,dateFrom,dateTo)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")
                        val item = SovSmis( 0,
                            rs.getString("transType"),
                            rs.getString("cid"),
                            rs.getString("tradeCode"),
                            rs.getString("tradeType"),
                            rs.getInt("clusterId"),
                            rs.getString("cluster"),
                            rs.getString("rid"),
                            rs.getString("dsp"),
                            rs.getString("customerNo"),
                            rs.getString("customer"),
                            rs.getString("acctNo"),
                            rs.getString("storeName"),
                            rs.getString("channel"),
                            rs.getString("date"),
                            rs.getString("bUnitId"),
                            rs.getString("bUnit"),
                            rs.getDouble("volume"),
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
        itemMaxProgress=1
        itemProgress=0
        var total=0
        val days=10L
        itemMaxProgress= Sync.listPeriod.sumOf { it.days}/days.toInt()
        if (itemMaxProgress<1) itemMaxProgress=1
        Sync.listPeriod.sortedBy { it.from }.forEach { p->
            var start=p.from.monthFirstDate().toLocalDate()
            var end=p.from.plusDays(days-1)
            while (start<=p.to) {
                if (end>p.to) end=p.to

                val cnt = downloadSovPromo(
                    t,
                    start.toDateString(),
                    end.toDateString()
                )
                total += cnt

                itemProgress+=1
                start=end.plusDays(1)
                end=start.plusDays(days)
            }
        }
        itemProgress=itemMaxProgress
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
                            rs.getInt("clusterId"),
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
                            rs.getString("Name"),
                            rs.getString("GLevel")
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
        val periodCount=Sync.listPeriod.size
        itemMaxProgress=periodCount
        Sync.listPeriod.sortedBy { it.from }.forEach { p->
            val cnt = downloadDspTarget(
                t,
                p.from.toDateString(),
                p.to.monthLastDate()
            )
            total += cnt
            itemProgress+=1
        }
        itemProgress=itemMaxProgress
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

    private suspend fun uploadDspTarget(t: Data.Sync) {
        syncItemGetData(t)
        val vm= ViewModelProvider(this)[DspTargetViewModel::class.java]
        val list=vm.upload(server.cid)
        val max=list.count()
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        if (conn != null) {
            try {
                conn.autoCommit=false
                val cs=conn.prepareCall("{Call spm_poultry_Upload_Dsp_Target(?,?,?,?)}")
                var ctr = 0
                list.forEach { item ->
                    cs.setString("@rid", item.rid.takeLast(2))
                    cs.setString("@Date", item.date)
                    cs.setInt("@VolumeTarget", item.volumeTarget)
                    cs.setInt("@AmountTarget", item.amountTarget)
                    cs.addBatch()
                    ctr += 1
                    delay(1)
                    syncItemProgress(t,ctr,max)
                }
                cs.executeBatch()
                conn.commit()
                list.forEach { item ->
                    vm.uploadSuccess(Filter.cid,item.rid,item.date)
                }
                syncItemCompleted(t,max)

            } catch (e: Exception) {
                syncError(t,e.message!!)
                conn.rollback()
            } finally {
                conn.close()
            }
        }else {
            syncError(t,"Check connection")
        }
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
                            rs.getString("tradeCode"),
                            rs.getString("tradeType"),
                            rs.getInt("clusterId"),
                            rs.getString("cluster"),
                            rs.getString("rid"),
                            rs.getString("dsp"),
                            rs.getString("customerNo"),
                            rs.getString("customer"),
                            rs.getString("acctNo"),
                            rs.getString("storeName"),
                            rs.getString("channel"),
                            rs.getString("date"),
                            rs.getString("invoiceNo"),
                            rs.getString("terms"),
                            rs.getString("balanceType"),
                            rs.getInt("daysDue"),
                            rs.getInt("agingId"),
                            rs.getString("aging"),
                            rs.getString("dueDate"),
                            rs.getDouble("balance"),
                            rs.getString("checkNo"),
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
        val periodCount=Sync.listPeriod.size
        itemMaxProgress=periodCount
        Sync.listPeriod.sortedBy { it.from }.forEach { p->
            val cnt = downloadAccountTarget(
                t,
                p.from.toDateString(),
                p.to.monthLastDate()
            )
            total += cnt
            itemProgress+=1
        }
        itemProgress=itemMaxProgress
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

    private suspend fun downloadInventory( t: Data.Sync) {
        itemMaxProgress=1
        itemProgress=0
        syncItemGetData(t)
        val sp = "{Call spm_poultry_Download_Inventory}"
        val sql = MSSQL()
        var conn: Connection? = null
        if (sql.conn(server)!=null)  conn=sql.conn(server)!!
        var max = 0
        if (conn != null) {
            try {
                val cs: CallableStatement = conn.prepareCall(sp)
                cs.execute()
                var ctr = 0
                val rs = cs.executeQuery()

                if (rs != null) {
                    val vm= ViewModelProvider(this)[InventoryViewModel::class.java]
                    vm.deleteAll(server.cid)
                    while (rs.next()) {
                        if (max==0) max=rs.getInt("TotalRows")

                        val item = Inventory(0,
                            rs.getString("cid"),
                            rs.getString("itemId"),
                            rs.getString("itemCode"),
                            rs.getString("itemDesc"),
                            rs.getString("bUnitId"),
                            rs.getString("bUnit"),
                            rs.getString("catId"),
                            rs.getString("category"),
                            rs.getString("lastActualDate"),
                            rs.getDouble("actual"),
                            rs.getDouble("purchase"),
                            rs.getDouble("goodStocks"),
                            rs.getDouble("routeReturn"),
                            rs.getDouble("received"),
                            rs.getDouble("routeIssue"),
                            rs.getDouble("salesPresell"),
                            rs.getDouble("pullOut"),
                            rs.getDouble("warehouseBo"),
                            rs.getDouble("onHand")
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
}