package com.example.poultry.ui.function

import android.R
import android.content.Context
import android.content.res.Configuration
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Menu
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.example.poultry.ui.function.Theme.resolveColorAttr

object Theme {

    fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(this, colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }

    fun setMenuTextColor(context: Context,menu:Menu){

        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            spanString.setSpan(
                ForegroundColorSpan( context.resolveColorAttr(R.attr.textColorSecondary)),
                0,
                spanString.length,
                0
            ) //fix the color to white
            item.setTitle(spanString)
        }
    }

    fun isDarkMode(context: Context): Boolean {
        val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }
}