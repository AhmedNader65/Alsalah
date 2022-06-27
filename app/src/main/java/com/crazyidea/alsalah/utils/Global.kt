package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.Coordinates
import com.crazyidea.alsalah.data.model.Language
import com.crazyidea.alsalah.adapter.BaseViewHolder
import com.crazyidea.alsalah.adapter.SimpleRecyclerAdapter
import com.crazyidea.alsalah.data.model.Articles
import io.github.cosinekitty.astronomy.Observer
import java.text.Normalizer
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
internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun ViewGroup.inflater(layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun <T : Any> RecyclerView.withSimpleAdapter(
    dataList: List<T>, @LayoutRes layoutID: Int,
    onBindView: BaseViewHolder<T>.(data: T) -> Unit
): SimpleRecyclerAdapter<T> {
    val recyclerAdapter = SimpleRecyclerAdapter(dataList, layoutID, onBindView)
    adapter = recyclerAdapter
    return recyclerAdapter
}

fun updateStoredPreference(context: Context) {
    val prefs = GlobalPreferences(context)
    coordinates = Coordinates(prefs.getLatitude().toDouble(), prefs.getLongitude().toDouble(), 0.0)
}

fun ignoreCaseOpt(ignoreCase: Boolean) =
    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()

fun SpannableStringBuilder?.indexesOf(pat: String, ignoreCase: Boolean = true): List<Int> =
    pat.toRegex(ignoreCaseOpt(ignoreCase))
        .findAll(this ?: "")
        .map { it.range.first }
        .toList()

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale("ar")).firstDayOfWeek
    val daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek is not DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        return rhs + lhs
    }
    return daysOfWeek
}


fun Articles.share(context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, title)
    intent.putExtra(
        Intent.EXTRA_TEXT,
        context.getString(
            R.string.share_text,
            title,
            "https://play.google.com/store/apps/details?id=${context.applicationInfo.packageName}"
        )
    )
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

fun String.getJuzName(context: Context): String {

    return when (this.toInt()) {
        1 -> context.getString(R.string.juz1)
        2 -> context.getString(R.string.juz2)
        3 -> context.getString(R.string.juz3)
        4 -> context.getString(R.string.juz4)
        5 -> context.getString(R.string.juz5)
        6 -> context.getString(R.string.juz6)
        7 -> context.getString(R.string.juz7)
        8 -> context.getString(R.string.juz8)
        9 -> context.getString(R.string.juz9)
        10 -> context.getString(R.string.juz10)
        11 -> context.getString(R.string.juz11)
        12 -> context.getString(R.string.juz12)
        13 -> context.getString(R.string.juz13)
        14 -> context.getString(R.string.juz14)
        15 -> context.getString(R.string.juz15)
        16 -> context.getString(R.string.juz16)
        17 -> context.getString(R.string.juz17)
        18 -> context.getString(R.string.juz18)
        19 -> context.getString(R.string.juz19)
        20 -> context.getString(R.string.juz20)
        21 -> context.getString(R.string.juz21)
        22 -> context.getString(R.string.juz22)
        23 -> context.getString(R.string.juz23)
        24 -> context.getString(R.string.juz24)
        25 -> context.getString(R.string.juz25)
        26 -> context.getString(R.string.juz26)
        27 -> context.getString(R.string.juz27)
        28 -> context.getString(R.string.juz28)
        29 -> context.getString(R.string.juz29)
        else -> context.getString(R.string.juz30)
    }
}

fun String.withoutDiacritics(): String {

    var input = Normalizer.normalize(this, Normalizer.Form.NFKD).replace("\\p{M}".toRegex(), "")
    input = input.replace("ٱ","ا")
    return input
}
