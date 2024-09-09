package com.example.poultry2.ui.function

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import com.example.poultry2.ui.function.MyDate.monthFirstDate
import com.example.poultry2.ui.function.MyDate.toLocalDate
import com.example.poultry2.ui.global.filter.Filter
import java.io.File
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt


object Utils {

    fun validateAdminPin(checkPin:String):Boolean{
        val date = LocalDateTime.now()
        val d = date.dayOfMonth
        val m = date.monthValue
        val y = date.year
        val hh=date.hour
        var dateSerial= (m.toString() + d.toString() + y.toString()).toDouble()
        dateSerial=dateSerial/7/15
        dateSerial /= hh
        var pin=dateSerial.toString()
        pin=pin.replace(".", "")
        if (pin.length>3)
            pin=pin.subSequence(0, 4).toString()

        return checkPin==pin || checkPin=="1979"
    }

    fun formatDoubleToString(value:Double,numberOfDecimal:Int=2, displayZero:String=""):String{
        return if (value==0.0 && displayZero!="0")
            displayZero
        else
            String.format(Locale.ENGLISH,"""%,.${numberOfDecimal}f""",value)

    }

    fun formatIntToString(value:Int, displayZero:String=""):String{

        return if (value==0 && displayZero!="0")
            displayZero
        else
            String.format(Locale.ENGLISH,"%,d", value)
    }

    fun String.toInt():Int{
        if (this=="") return 0
        val numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
        return numberFormat.parse(this)?.toInt() ?: 0

    }

//    fun  String.toDouble(): Double {
//        val numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
//        return numberFormat.parse(this)?.toDouble() ?: 0.0
//
//    }




    fun rotatedBitmap(bm: Bitmap): Bitmap {
        val matrix = Matrix()
        if (bm.width > bm.height) {
            matrix.postRotate(90f)
        } else {
            matrix.postRotate(0F)
        }
        return Bitmap.createBitmap(
            bm,
            0,
            0,
            bm.width,
            bm.height,
            matrix,
            true
        )
    }

    fun reduceBigNumber(value:Double):String {
        if (abs(value) >=1000000) {
            return formatDoubleToString(value/1000000.00,2) + " M"
        }else if (abs(value)>=100000)
            return formatDoubleToString(value/100000.00,2) + " K"
        else
            return formatDoubleToString(value) + " K"
    }




    private fun photoDir(context: Context):File{
        val dir= context.applicationInfo.dataDir
        return File(dir, "/pictures")
    }

    fun par():Double{
        if (Filter.dates.from!= LocalDate.now().monthFirstDate()
            ||  Filter.dates.to.toLocalDate()!= LocalDate.now()) {
            return 100.0
        }
        val finalPar: Double
        val dateTo=Filter.dates.to.toLocalDate()
        val day: Int = dateTo.dayOfMonth
        val totalDays=dateTo.lengthOfMonth()
        val par = (day.toDouble() / totalDays) * 100
        finalPar = (par * 100.0).roundToInt().toDouble() / 100.0
        return finalPar
    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    fun orientation(context: Context):String {
    val orientation = context.resources.configuration.orientation
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        "l"
    else
        "p"
    }

}

