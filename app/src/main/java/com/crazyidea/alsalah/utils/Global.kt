package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.res.Resources
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.crazyidea.alsalah.data.model.Coordinates
import com.crazyidea.alsalah.data.model.Language
import io.github.cosinekitty.astronomy.Observer
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

val Number.dp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.density
val Number.sp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.scaledDensity


var preferredDigits = Language.PERSIAN_DIGITS
    private set
var clockIn24 = DEFAULT_WIDGET_IN_24
    private set
var language = Language.FA
    private set
var coordinates: Coordinates? = null
    private set
var amString = DEFAULT_AM
    private set
var pmString = DEFAULT_PM
    private set
var spacedComma = "، "
    private set
fun Coordinates.toObserver() = Observer(this.latitude, this.longitude, this.elevation)
internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))
internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun updateStoredPreference(context: Context) {
    val prefs = GlobalPreferences(context)
    coordinates = Coordinates(prefs.latituide.toDouble(), prefs.longituide.toDouble(), 0.0)
}
fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale("ar")).firstDayOfWeek
    val daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek is not DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        return  rhs + lhs
    }
    return daysOfWeek
}
