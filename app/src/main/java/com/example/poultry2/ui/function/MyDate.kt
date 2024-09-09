package com.example.poultry2.ui.function

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date

object MyDate {



    fun LocalDate.monthFirstDate(): String {
        return this.withDayOfMonth(1).toDateString()
    }

    fun LocalDate.monthLastDate(): String {
        return this.with(TemporalAdjusters.lastDayOfMonth()).toDateString()
    }

    fun LocalDate.toDateString(format:String="yyyy-MM-dd"):String{
        return this.format(DateTimeFormatter.ofPattern(format))
    }

    fun String.toDate():Date{
        return java.sql.Date.valueOf(this)
    }
    fun String.toLocalDate(format:String="yyyy-MM-dd"): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(format)
        return LocalDate.parse(this, formatter)
    }

    fun String.toLocalDateTime(): LocalDateTime {
        val dateTime=this.replace(" ","T")
        return LocalDateTime.parse(dateTime)
    }

    fun LocalDateTime.toDateTimeString(format:String="yyyy-MM-dd"):String{
        return this.format(DateTimeFormatter.ofPattern(format))
    }

    fun formatDateTimeStringToLong(dateTime: String): Long {
        return Instant.parse(dateTime).toEpochMilli()
    }

    fun formatLongToLocalDateTime(long:Long): LocalDateTime {
        return Instant.ofEpochMilli(long)
            .atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////


    private fun LocalDate.weekStartDate(startWeek:LocalDate):LocalDate{
             return this.with(TemporalAdjusters.previousOrSame(startWeek.dayOfWeek))
    }

    @Parcelize
    class Week(var weekNo:Int,var weekFirstDate:LocalDate,var date:LocalDate): Parcelable

    fun currentWeek(startWeek:LocalDate, date:LocalDate): Week{
        if (startWeek==date)  return Week(1,date,date)
        var weekNo: Int
        var weekFirstDate=date
        if (startWeek <date) {
            var startDate= startWeek
            //moving forward
            weekNo=0
            while (startDate <= date) {
                if (startDate.dayOfWeek ==startWeek.dayOfWeek)  {
                    weekNo += 1
                    weekFirstDate=startDate
                }
                startDate = startDate.plusDays(1)
            }
            weekNo %= 4
            weekNo = if (weekNo>0) weekNo else 4
        }else{
            var startDate=date.weekStartDate(startWeek)
            weekNo=0
            while (startDate < startWeek) {
                if (startDate.dayOfWeek ==startWeek.dayOfWeek) {
                    weekNo += 1
                    weekFirstDate = startDate
                }
                startDate = startDate.plusDays(1)
            }
            weekNo =5-(weekNo%4)
            if (weekNo==5) weekNo=1

        }

        return Week(weekNo,weekFirstDate,date)
    }

    fun Week.minusWeek(noOfWeeks:Int) :Week {
        val cw=Week(this.weekNo,this.weekFirstDate,date)
        var w=noOfWeeks
        while (w>0) {
            cw.weekFirstDate = cw.weekFirstDate.minusDays(7)
            cw.weekNo--
            if (cw.weekNo==0)cw.weekNo=1
            w--
        }

        return cw
    }


    fun monthsBetween(start: String?, end: String?): Int {
        val startDate= start?.toDate()
        val endDate= end?.toDate()
        require(!(startDate == null || endDate == null)) { "Both startDate and endDate must be provided" }
        val startCalendar: Calendar = Calendar.getInstance()
        startCalendar.time = startDate
        val startDateTotalMonths: Int = (12 * startCalendar.get(Calendar.YEAR)
                + startCalendar.get(Calendar.MONTH))
        val endCalendar: Calendar = Calendar.getInstance()
        endCalendar.time = endDate
        val endDateTotalMonths: Int = (12 * endCalendar.get(Calendar.YEAR)
                + endCalendar.get(Calendar.MONTH))
        return endDateTotalMonths - startDateTotalMonths
    }

    fun Long.millisToTime():String{
        val minutes = this / 1000 / 60
        val seconds = this / 1000 % 60

        return if (minutes>0 && seconds>0)
            "$minutes min , $seconds sec"
        else if (minutes>0 && seconds ==0L)
            "$minutes min"
        else if (minutes==0L && seconds >=0L)
            "$seconds sec"
        else
            "-"
    }


}