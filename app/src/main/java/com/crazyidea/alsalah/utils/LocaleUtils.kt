package com.crazyidea.alsalah.utils

import android.content.res.Resources
import android.view.View
import com.crazyidea.alsalah.data.model.Language
import com.crazyidea.alsalah.preferredDigits


val Resources.isRtl get() = configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

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
