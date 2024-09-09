package com.example.poultry2.ui.function

import android.content.Context
import android.graphics.Typeface
import android.print.PrintAttributes.Margins
import android.text.Layout.Alignment
import android.view.Gravity
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.poultry2.R
import com.example.poultry2.ui.function.Theme.resolveColorAttr


object Table {


    fun header(context: Context, text:String, gravity:Int= Gravity.CENTER,
                       firstCell:Boolean=false,
                       textColor:Int=ContextCompat.getColor(context, R.color.header_textColor),
                       span:Int=1,
                       typeface: Int=Typeface.NORMAL, size: Float =12f
    ): TextView {
        var lp: TableRow.LayoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
        if (text.length>35) lp=TableRow.LayoutParams(500,TableRow.LayoutParams.WRAP_CONTENT)
        lp.span = span // define no. of column span will row do

        val margins: Margins = if (firstCell)
            Margins(1,1,1, 1)
        else
            Margins(0,1,1, 1)

        lp.setMargins(margins.leftMils,margins.topMils,margins.rightMils,margins.bottomMils)


        val backColor = ContextCompat.getColor(context, R.color.header_backColor)
        val tv = TextView(context)
        tv.layoutParams=lp
        tv.setTextColor(textColor)
        tv.setBackgroundColor(backColor)
        tv.text=text
        tv.setTypeface(null,typeface)
        tv.gravity = gravity
        tv.textSize = size
        tv.setPadding(20,15,20,15)
        return tv
    }

    private fun headerIcon(context: Context,
                           icon:Int?,
                           span:Int=1,
                           margins: Margins =Margins(0,0,0,0),
                           color:Int=context.resolveColorAttr(android.R.attr.textColorSecondary)
    ): ImageView {
        val lp: TableRow.LayoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
        lp.span = span // define no. of column span will row do
        lp.setMargins(margins.leftMils,margins.topMils,margins.rightMils,margins.bottomMils)
        val backColor=context.resolveColorAttr(android.R.attr.windowBackground)

        val img = ImageView(context)
        img.layoutParams=lp
        img.scaleType=ImageView.ScaleType.CENTER
        img.setBackgroundColor(backColor)
        img.setPadding(10,10,10,10)
        if (icon!=null) {
            img.setImageResource(icon)
            img.setColorFilter(color)
        }

        return img
    }

    fun cell(context: Context, text:String, gravity:Int= Gravity.CENTER,
             textColor:Int=context.resolveColorAttr(android.R.attr.textColorSecondary),
             firstCell:Boolean=false,
             span:Int=1,
             typeface: Int=Typeface.NORMAL, size: Float =14f
    ): TextView {

        var lp: TableRow.LayoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
        if (text.length>35) lp=TableRow.LayoutParams(500,TableRow.LayoutParams.WRAP_CONTENT)

        lp.span = span // define no. of column span will row do

        val margins: Margins = if (firstCell)
            Margins(1,0,1, 1)
        else
            Margins(0,0,1, 1)

        lp.setMargins(margins.leftMils,margins.topMils,margins.rightMils,margins.bottomMils)

        val backColor=context.resolveColorAttr(android.R.attr.windowBackground)

//        val backColor = context.resolveColorAttr(android.R.attr.textColorPrimaryInverse)
        val tv = TextView(context)

        tv.layoutParams=lp
        tv.setTextColor(textColor)
        tv.setBackgroundColor(backColor)
        tv.text=text
        tv.setTypeface(null,typeface)
        tv.gravity = gravity
        tv.textSize = size
        tv.setPadding(15,10,15,10)
        return tv
    }



    fun customCell(context: Context, text:String, gravity:Int= Gravity.CENTER,
             textColor:Int=context.resolveColorAttr(android.R.attr.textColorSecondary),
             span:Int=1,
             typeface: Int=Typeface.NORMAL, size: Float =14f,
                   margins: Margins =Margins(0,0,0,0)
    ): TextView {
        var lp: TableRow.LayoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
        if (text.length>35) lp=TableRow.LayoutParams(500,TableRow.LayoutParams.WRAP_CONTENT)
        lp.span = span // define no. of column span will row do

        lp.setMargins(margins.leftMils,margins.topMils,margins.rightMils,margins.bottomMils)
        val backColor=context.resolveColorAttr(android.R.attr.windowBackground)
        val tv = TextView(context)
        tv.layoutParams=lp
        tv.setTextColor(textColor)
        tv.setBackgroundColor(backColor)
        tv.text=text
        tv.setTypeface(null,typeface)
        tv.gravity = gravity
        tv.textSize = size
        tv.setPadding(15,10,15,10)
        return tv
    }

    fun subCell(context: Context, text:String, gravity:Int= Gravity.CENTER,
                textColor:Int=context.resolveColorAttr(android.R.attr.textColorSecondary),
                firstCell:Boolean=false,
                span:Int=1,
                typeface: Int=Typeface.BOLD, size: Float =14f
    ): TextView {
        var lp: TableRow.LayoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
        if (text.length>35) lp=TableRow.LayoutParams(500,TableRow.LayoutParams.WRAP_CONTENT)
        lp.span = span // define no. of column span will row do

        val margins: Margins = if (firstCell)
            Margins(1,0,1, 1)
        else
            Margins(0,0,1, 1)

        lp.setMargins(margins.leftMils,margins.topMils,margins.rightMils,margins.bottomMils)

        val backColor=context.resolveColorAttr(android.R.attr.windowBackground)


        val tv = TextView(context)
        tv.layoutParams=lp
        tv.setTextColor(textColor)
        tv.setBackgroundColor(backColor)
        tv.text=text
        tv.setTypeface(null,typeface)
        tv.gravity = gravity
        tv.textSize = size
        tv.setPadding(15,10,15,10)
        return tv
    }

    fun createHeader(title:String, headerList:List<String>,table:TableLayout,clickable:Boolean=false){
        val context=table.context
        table.removeAllViews()
//        @ColorInt var color = context.resolveColorAttr(android.R.attr.textColorSecondary)
        var color = ContextCompat.getColor(context, R.color.table_backColor)
        if (Theme.isDarkMode(context))
            color=MyColor.darkenColor(color,0.3f)

        table.setBackgroundColor(color)
        if (title!=""){
            val titleHeader = TableRow(context)
            var span=headerList.size
            if (clickable) span-=1
            titleHeader.addView(
                customCell(context,title,Gravity.START,
                    context.resolveColorAttr(android.R.attr.textColorPrimary),span,
                    Typeface.BOLD,18f,
                    Margins(0,0,0,0)
                )

            )
            if (clickable) {

                titleHeader.addView(headerIcon(context,R.drawable.ic_open))
            }
            
            table.addView(titleHeader)
        }


        val rowHeader = TableRow(context)
        for (i in headerList.indices) {
             if (i==0)
                 rowHeader.addView(header(context,headerList[i],Gravity.CENTER,true))
            else
                 rowHeader.addView(header(context,headerList[i],Gravity.CENTER,false))
        }
        table.addView(rowHeader)
    }


    fun showFilter(map:Map<String,String>,table:TableLayout) {
        val headers= mutableListOf<String>()
        map.forEach{item ->
            headers.add(item.key.uppercase())
        }
        createHeader("FILTERED BY            ",headers,table)

        val context=table.context
        val textColor = context.resolveColorAttr(android.R.attr.textColorSecondary)
        val row = TableRow(context)
        var ctr=0
        map.forEach{ item ->
            if (ctr==0)
                row.addView(
                    cell(context,item.value.uppercase(), Gravity.START,textColor,
                    true)
                )
            else
                row.addView(cell(context,item.value.uppercase(), Gravity.START,textColor))
            ctr++
        }
        table.addView(row)
    }

    fun icon(context: Context,
             icon:Int= R.drawable.ic_open,
             firstCell:Boolean=false,
             span:Int=1,
             color:Int=context.resolveColorAttr(android.R.attr.textColorSecondary)

    ): ImageView {
        val lp: TableRow.LayoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
        lp.span = span // define no. of column span will row do
        val margins: Margins = if (firstCell)
            Margins(1,0,1, 1)
        else
            Margins(0,0,1, 1)

        lp.setMargins(margins.leftMils,margins.topMils,margins.rightMils,margins.bottomMils)
        val backColor=context.resolveColorAttr(android.R.attr.windowBackground)

        val img = ImageView(context)
        img.layoutParams=lp
        img.setBackgroundColor(backColor)
        img.setPadding(8,8,8,8)
        if (icon!=null) {
            img.setImageResource(icon)
            img.setColorFilter(color)
        }
        img.setColorFilter(color)
        return img
    }

    fun  headerTitle(title:String,breakdownTitle:String): String {
        return "$title  /  $breakdownTitle".uppercase()
    }
}