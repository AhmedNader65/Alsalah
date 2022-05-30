package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.crazyidea.alsalah.data.model.Language
import java.util.*


val Resources.isRtl get() = configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

fun setLocale(context: Context, lang: String) {
    val globalPreferences = GlobalPreferences(context)
    globalPreferences.storeLocale("ar")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val overrideConfiguration: Configuration = context.resources.configuration
        overrideConfiguration.setLocales(LocaleList(Locale(lang)))
        val resources: Resources = context.resources
    } else {
        val res = context.resources
        // Change locale settings in the app.
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(lang)) // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm)
    }
    Locale.setDefault(Locale(lang))
}
fun formatNumber(number: Double): String {
    if (isArabicDigitSelected) return number.toString()
    return formatNumber(number.toString()).replace(".", "٫") // U+066B, Arabic Decimal Separator
}

fun formatNumber(number: Int, digits: CharArray = preferredDigits): String =
    formatNumber(number.toString(), digits)

fun formatNumber(number: String, digits: CharArray = preferredDigits): String {
    if (isArabicDigitSelected) return number
    return number.map { digits.getOrNull(Character.getNumericValue(it)) ?: it }
        .joinToString("")
}

val isArabicDigitSelected: Boolean get() = preferredDigits === Language.ARABIC_DIGITS


@ColorInt
fun Context.themeColor(@AttrRes attrRes: Int): Int = TypedValue()
    .apply { theme.resolveAttribute(attrRes, this, true) }
    .data


