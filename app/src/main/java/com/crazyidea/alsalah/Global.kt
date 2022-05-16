package com.crazyidea.alsalah

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ShapeDrawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.crazyidea.alsalah.data.model.Coordinates
import com.crazyidea.alsalah.data.model.Language
import com.crazyidea.alsalah.utils.GlobalPreferences
import io.github.cosinekitty.astronomy.Observer

val Number.dp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.density
val Number.sp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.scaledDensity


private val monthNameEmptyList = List(12) { "" }
var persianMonths = monthNameEmptyList
    private set
var islamicMonths = monthNameEmptyList
    private set
var gregorianMonths = monthNameEmptyList
    private set
var nepaliMonths = monthNameEmptyList
    private set
private val weekDaysEmptyList = List(7) { "" }
var weekDays = weekDaysEmptyList
    private set
var weekDaysInitials = weekDaysEmptyList
    private set
var preferredDigits = Language.PERSIAN_DIGITS
    private set
var clockIn24 = DEFAULT_WIDGET_IN_24
    private set
var isForcedIranTimeEnabled = DEFAULT_IRAN_TIME
    private set
var isNotifyDateOnLockScreen = DEFAULT_NOTIFY_DATE_LOCK_SCREEN
    private set
var isWidgetClock = DEFAULT_WIDGET_CLOCK
    private set
var isNotifyDate = DEFAULT_NOTIFY_DATE
    private set
var notificationAthan = DEFAULT_NOTIFICATION_ATHAN
    private set
var enableNewInterface = false
    private set
var language = Language.FA
    private set
var easternGregorianArabicMonths = false
    private set
var coordinates: Coordinates? = null
    private set
var isShowWeekOfYearEnabled = false
    private set
var isCenterAlignWidgets = true
    private set
var weekStartOffset = 0
    private set
var weekEnds = BooleanArray(7)
    private set
var isShowDeviceCalendarEvents = false
    private set
var whatToShowOnWidgets = emptySet<String>()
    private set
var isAstronomicalExtraFeaturesEnabled = false
    private set
var isTalkBackEnabled = false
    private set
var isHighTextContrastEnabled = false
    private set
var shiftWorkTitles = emptyMap<String, String>()
    private set
var shiftWorkRecurs = true
    private set
var shiftWorkPeriod = 0
    private set
var isIranHolidaysEnabled = true
    private set
var amString = DEFAULT_AM
    private set
var pmString = DEFAULT_PM
    private set
var spacedAndInDates = " و "
    private set
var spacedColon = ": "
    private set
var spacedComma = "، "
    private set
var holidayString = DEFAULT_HOLIDAY
    private set
var numericalDatePreferred = false
    private set
var calendarTypesTitleAbbr = emptyList<String>()
    private set
private var secondaryCalendarEnabled = false

fun Coordinates.toObserver() = Observer(this.latitude, this.longitude, this.elevation)

@ColorInt
fun Context.resolveColor(@AttrRes attribute: Int) = TypedValue().let {
    theme.resolveAttribute(attribute, it, true)
    ContextCompat.getColor(this, it.resourceId)
}

fun updateStoredPreference(context: Context) {
    val prefs = GlobalPreferences(context)
    coordinates = Coordinates(prefs.latituide.toDouble(), prefs.longituide.toDouble(), 0.0)
}
