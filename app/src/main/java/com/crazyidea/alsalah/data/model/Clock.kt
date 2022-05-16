package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.utils.amString
import com.crazyidea.alsalah.utils.clockIn24
import com.crazyidea.alsalah.utils.language
import com.crazyidea.alsalah.utils.pmString
import com.crazyidea.alsalah.utils.formatNumber
import java.util.*

data class Clock(val hours: Int, val minutes: Int) {
    constructor(date: Calendar) : this(date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE])

    fun toMinutes() = hours * 60 + minutes

    fun toBasicFormatString(hours: Int = this.hours): String =
        formatNumber("%d:%02d".format(Locale.ENGLISH, hours, minutes))

    fun toFormattedString(forcedIn12: Boolean = false, printAmPm: Boolean = true): String {
        if (clockIn24 && !forcedIn12) return toBasicFormatString()
        val clockString = toBasicFormatString((hours % 12).takeIf { it != 0 } ?: 12)
        if (!printAmPm) return clockString
        return language.clockAmPmOrder.format(clockString, if (hours >= 12) pmString else amString)
    }
    fun toHoursFraction() = toMinutes() / 60.0

    companion object {
        fun fromHoursFraction(input: Double): Clock {
            val value = (input + 0.5 / 60) % 24 // add 0.5 minutes to round
            val hours = value.toInt()
            val minutes = ((value - hours) * 60.0).toInt()
            return Clock(hours, minutes)
        }

        fun fromMinutesCount(minutes: Int) = Clock(minutes / 60, minutes % 60)
    }
}
