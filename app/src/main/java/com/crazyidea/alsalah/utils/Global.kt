package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.res.Resources
import com.crazyidea.alsalah.data.model.Coordinates
import com.crazyidea.alsalah.data.model.Language
import io.github.cosinekitty.astronomy.Observer

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

fun updateStoredPreference(context: Context) {
    val prefs = GlobalPreferences(context)
    coordinates = Coordinates(prefs.latituide.toDouble(), prefs.longituide.toDouble(), 0.0)
}
