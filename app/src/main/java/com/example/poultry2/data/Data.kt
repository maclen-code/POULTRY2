package com.example.poultry2.data

import android.os.Parcelable
import com.example.poultry2.data.inventory.Inventory
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

class Data {
    class Policy(var name:String, var userTypeCodeList:List<String>)
    class UserType(var code:String,var description: String)
    class User(var userType: String,var userCode: String)

    @Parcelize
    class Server(var cid:String,var name:String,var localIp:String,var publicIp:String,
                 var database:String,var password:String,
                 var userType: String,var userCode: String,var isLocal: Boolean,
                 var dbaseVersion:String,var lastSync:String):Parcelable

    class Dates(var from:String,var to:String,var universeFrom:String,
                     var lastMonthFrom:String,var lastMonthTo:String,
                     var lastYearFrom:String,var lastYearTo:String)


    class FilterTransType(var transType:String,var type:String, var isChecked: Boolean)

    class SyncPeriod(var from:LocalDate,var to:LocalDate,var days:Int,
                     var siv:Boolean,var sov:Boolean)

    class Sync(var download:Boolean,var dataName:String,var process:String,var progress:Double,
               var status:String,var error:String,var time:Long=0)

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Parcelize
    class Target(var volumeTarget:Int,var amountTarget: Int):Parcelable

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Parcelize
    class TargetCluster(var clusterId:Int,var cluster: String,
                 var volumeTarget:Int,var amountTarget:Int):Parcelable

    @Parcelize
    class TargetDsp(var rid:String,var volumeTarget:Int,var amountTarget:Int):Parcelable

    //////////////////////////////////////////////////////////////////////////////////////////////

    class SovClusterVolume(var clusterId:Int,var cluster: String, var volume:Double)
    class SovTradeVolume(var tradeCode: String, var volume:Double)

    class SovDspVolume(var rid: String,var dsp: String, var volume:Double)
    class SovChannelVolume(var channel: String, var volume:Double)

    class SovBunitVolume(var bunitId: String,var bunit: String, var volume:Double)

    class SovCategoryVolume(var catId:String,var category: String, var volume:Double)
    class SovCustomerVolume(var customerNo: String, var customer: String, var volume:Double)

    class SovAcctVolume(var acctNo: String, var storeName: String, var volume:Double)


    //////////////////////////////////////////////////////////////////////////////////////////////

    @Parcelize
    class Ordered(var acctNo: String, var storeName:String,var dsp:String,var channel: String,
                  var volume: Double, var totalNet:Double):Parcelable
    @Parcelize
    class NotOrdered(var acctNo: String, var storeName:String,var dsp:String,var channel: String,
                     var lastOrdered:String):Parcelable

    //////////////////////////////////////////////////////////////////////////////////////////////
    class SivSovCluster(var clusterId: Int,var cluster: String,
                        var sivVolume:Double,var sivAmount:Double,
                        var sivVolumeTarget:Int,var sivAmountTarget:Int,
                        var sovVolume:Double,var sovAmount:Double,
                        var sovVolumeTarget:Int,var promoDiscount:Double)

    class SivSovClusterTradeType(var clusterId: Int,var cluster: String,var transType: String,
                                 var tradeType: String,var volume:Double)

    class SovClusterTrade(var clusterId:Int,var cluster: String, var tradeCode:String,
                          var volume:Double,var totalNet:Double, var ordered:Int, var universe:Int,
                          var lastYearVolume:Double,var lastMonthVolume:Double)

    class SovClusterDsp(var clusterId: Int,var cluster: String, var rid:String,var dsp:String,
                        var volume:Double,var volumeTarget:Int,var amountTarget:Int,
                        var totalNet:Double,var ordered:Int, var universe:Int,
                        var lastYearVolume:Double,var lastMonthVolume:Double)

    class SovTradeDsp(var tradeCode: String,var rid:String, var dsp:String,
                      var volume:Double,var totalNet:Double, var ordered:Int, var universe:Int,
                      var lastYearVolume:Double,var lastMonthVolume:Double)

    class SovTradeChannel(var tradeCode: String, var channel:String,
                          var volume:Double,var totalNet:Double, var ordered:Int, var universe:Int,
                          var lastYearVolume:Double,var lastMonthVolume:Double)


    class SovDspBunit(var productType:String, var rid:String,var dsp: String,var bunitId: String,var bunit: String,
                      var volume:Double,var totalNet:Double,var ordered:Int,var universe:Int,
                      var lastYearVolume:Double,var lastMonthVolume:Double)

    class SovDspChannel(var rid:String,var dsp: String,var channel: String,
                        var volume:Double,var totalNet:Double,var ordered:Int,var universe:Int,
                        var lastYearVolume:Double,var lastMonthVolume:Double)


    class SovCategoryDsp(var catId:String,var category: String,var tradeCode: String, var rid:String,
                         var dsp:String, var volume:Double, var totalNet:Double,
                         var ordered:Int, var universe:Int,
                         var lastYearVolume:Double,var lastMonthVolume:Double)

    class SovCategoryChannel(var catId:String,var category: String,var tradeCode: String, var channel:String,
                             var volume:Double, var totalNet:Double,var ordered:Int, var universe:Int,
                             var lastYearVolume:Double,var lastMonthVolume:Double)

    class SovCategory(var catId:String,var category: String,
                     var volume:Double, var totalNet:Double, var ordered:Int,
                     var universe: Int, var lastYearVolume:Double,
                     var lastMonthVolume:Double, var volumeUnit:String)

    class SovProduct(var catId:String,var category: String, var itemCode:String, var itemDesc:String,
                     var volume:Double, var totalNet:Double, var ordered:Int,
                     var universe: Int, var lastYearVolume:Double,
                     var lastMonthVolume:Double, var volumeUnit:String)

    class SovCustomer(var customerNo:String, var customer: String,
                      var volume:Double,var totalNet: Double,
                      var lastYearVolume: Double,var lastMonthVolume: Double)

    class CustomerArSummary(var customerNo:String, var customer: String,var balanceType: String,
                            var a1: Double,var a2: Double,var a3: Double,
                            var a4: Double, var a5: Double,var total: Double)

    class SovAcct(var acctNo: String, var storeName: String,
                  var volume:Double,var totalNet: Double,
                  var lastYearVolume: Double,var lastMonthVolume: Double)

    class AcctArSummary(var acctNo: String, var storeName: String,var balanceType: String,
                        var a1: Double,var a2: Double,var a3: Double,
                        var a4: Double,var a5: Double,var total: Double)

    class ArSummary(var balanceType: String,var a1: Double,var a2: Double,var a3: Double,
                    var a4: Double,var a5: Double,var total: Double)

    class ArInvoice(var agingId:Int,var aging:String,var date:String,var invoiceNo:String,
                    var balanceType:String,var terms:String,var dueDate:String,var balance: Double,
                    var checkNo:String,var checkDate:String?,var isChecked: Boolean)

    class SivSovClusterCategory(var catId: String,var category: String,
                        var sivVolume:Double,var sivAmount:Double,
                        var sovVolume:Double,var sovAmount:Double)


    ///////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashCluster(var clusterId:Int,var cluster: String,
                       var listSovClusterTrade:List<SovClusterTrade>,
                         var listSovClusterDsp:List<SovClusterDsp>)

    /////////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashClusterTrade(var tradeCode: String,
                              var listSovTradeDsp:List<SovTradeDsp>,
                              var listSovTradeChannel:List<SovTradeChannel>)

    ///////////////////////////////////////////////////////////////////////////////////////////


    class SovDashClusterDsp(var rid:String, var dsp: String,
                            var listSovDspTrade:List<SovTradeDsp>,
                            var listSovDspBunit:List<SovDspBunit>,
                            var target:TargetDsp)

    ///////////////////////////////////////////////////////////////////////////////////////////

    class SovDashClusterDspChannel(var tradeCode: String,
                            var listSovDspClusterChannel:List<SovTradeChannel>)

    /////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashClusterDspCategory(var catId: String,var category: String,
                                    var listSovCategoryChannel: List<SovCategoryChannel>)

    //////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashClusterCategory(var catId:String, var category: String,
                                 var listSovCategoryDsp:List<SovCategoryDsp>,
                                 var listSovCategoryChannel: List<SovCategoryChannel>)

    ///////////////////////////////////////////////////////////////////////////////////////////////


    class SovDashTradeDsp(var rid:String, var dsp: String,
                          var listSovDspBunit:List<SovDspBunit>,
                          var listSovDspChannel:List<SovDspChannel>)

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashTradeChannel(var channel:String,
                          var listSovChannelDsp:List<SovDspChannel>,
                          var listSovChannelCategory:List<SovCategoryChannel>)

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashTradeDspChannel(var channel:String,
                                 var listSovChannelCategory:List<SovCategoryChannel>,
                                 var listSovChannel:List<SovDspChannel>)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class SovDashCustomer(var customerNo: String, var customer: String,
                          var sovCustomer: SovCustomer,
                          var listAccountArSummary: List<CustomerArSummary>)

    ///////////////////////////////////////////////////////////////////////////////////////////////


    class SovDashAcct(var acctNo: String, var storeName: String,
                          var sovAcct: SovAcct,
                          var listAcctArSummary: List<AcctArSummary>)

    /////////////////////////////////////////////////////////////////////////////////////////////

    class SovInvoiceProduct(var invoiceNo: String,var date:String, var itemCode:String,
                            var itemDesc:String,var volume: Double,var totalNet: Double)

    class SovInvoiceDate(var invoiceNo: String,var date:String)

    class SovInvoice(var invoiceNo: String,var date:String,
                     var listProduct:List<SovInvoiceProduct>)


    ////////////////////////////////////////////////////////////////////////////////////////////


    class SovBunitCategory(var acctNo:String,var bunitId: String, var bunit: String,
                             var catId: String, var category:String, var volume:Double,
                             var totalNet: Double,
                             var lastYearVolume: Double,var lastMonthVolume: Double)


    ////////////////////////////////////////////////////////////////////////////////////////////
    class ArAging(var agingId:Int,var aging: String,var total:Double)

    /////////////////////////////////////////////////////////////////////////////////////////////

    class SovCustomerAcct(var customerNo:String, var customer: String, var acctNo: String,
                          var storeName: String,
                          var volume:Double,var totalNet: Double,
                          var lastYearVolume: Double,var lastMonthVolume: Double)


    ///////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashProduct(var catId:String,var category: String,
                         var listSovProduct:List<SovProduct>,
                         var sovCategory:SovCategory?)


    /////////////////////////////////////////////////////////////////////////////////////////////////

    class SovCustomerBunit(var customerNo:String, var customer: String, var bunitId: String,
                           var bunit: String,
                           var volume:Double,var totalNet: Double,
                           var lastYearVolume: Double,var lastMonthVolume: Double)
    class SovCustomerCategory(var customerNo:String, var customer: String, var bunitId: String,
                              var bunit: String, var catId:String,
                              var category:String, var volume:Double,
                              var totalNet: Double,
                              var lastYearVolume: Double,var lastMonthVolume: Double)

    ////////////////////////////////////////////////////////////////////////////////////////////

    class SovDashInventory(var catId:String,var category: String,var listInventory:List<Inventory>)
}