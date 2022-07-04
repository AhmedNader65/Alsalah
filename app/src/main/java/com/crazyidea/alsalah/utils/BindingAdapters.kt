package com.crazyidea.alsalah.utils

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.data.room.entity.Surah
import java.util.*


@BindingAdapter("setupImage", "clickedID", requireAll = false)
fun bindPrayerHeaderImage(imageView: ImageView, prayerId: Int, clickedID: Int) {
    var myMethod = prayerId
    if (clickedID == 0)
        myMethod = prayerId
    else
        myMethod = clickedID
    when (myMethod) {
        1 -> imageView.setImageResource(R.drawable.fajr_pic)
        2 -> imageView.setImageResource(R.drawable.shorok_pic)
        3 -> imageView.setImageResource(R.drawable.zuhr_pic)
        4 -> imageView.setImageResource(R.drawable.asr_pic)
        5 -> imageView.setImageResource(R.drawable.maghrib_pic)
        6 -> imageView.setImageResource(R.drawable.isha_pic)
    }
}

@BindingAdapter("setupSurahInfo")
fun bindSurahInfo(textView: TextView, surah: Surah) {
    val context = textView.context
    val sharedPref = GlobalPreferences(context)
    var text = context.getString(R.string.surah_number)
    text += " "
    text += String.format(Locale(sharedPref.getLocale()), "%d", surah.id)
    text += " - "
    text += context.getString(R.string.surah_ayat)
    text += " "
    text += String.format(Locale(sharedPref.getLocale()), "%d", surah.numberOfAyahs)
    text += " - "
    text += if (surah.revelationType == "Meccan") context.getString(R.string.meccan) else context.getString(
        R.string.madanya
    )
    textView.text = text
}

@BindingAdapter("setupSurahPage")
fun bindSurahPage(textView: TextView, surah: Surah) {
    val context = textView.context
    val sharedPref = GlobalPreferences(context)
    var text = String.format(Locale(sharedPref.getLocale()), "%d", surah.page)

    textView.text = text
}

@BindingAdapter("setupJuzName")
fun bindJuzName(textView: TextView, juz: String) {
    val context = textView.context
    var text = juz.getJuzName(context)
    textView.text = text
}

@BindingAdapter("num", "type")
fun bindKhatmaExpectingResult(textView: TextView, num: Int, type: Int) {
    val context = textView.context
    val text = if (type == 0)
        context.getString(R.string.number_of_pages, num)
    else
        context.getString(R.string.number_of_days, num)

    textView.text = text
}

@BindingAdapter("setupKhatmaName")
fun bindKhatmaExpectingResult(textView: TextView, item: Khatma) {
    val context = textView.context
    val text =
        context.getString(R.string.khatma)
    val type = when (item.type) {
        "review" -> context.getString(R.string.review)
        "memorise" -> context.getString(R.string.memorise)
        "think" -> context.getString(R.string.thinking)
        else -> context.getString(R.string.reading)
    }
    textView.text = text + " $type #${item.name}"
}

@BindingAdapter("mainKhatmaType")
fun bindMainKhatmaType(textView: TextView, item: Khatma?) {
    val context = textView.context
    val text =
        context.getString(R.string.intention)
    val type = when (item?.type) {
        "review" -> context.getString(R.string.review)
        "memorise" -> context.getString(R.string.memorise)
        "think" -> context.getString(R.string.thinking)
        else -> context.getString(R.string.reading)
    }
    textView.text = text + " $type"
}

@BindingAdapter("setupKhatmaRemainging")
fun bindKhatmaRemainging(textView: TextView, item: Khatma) {
    val context = textView.context
    val text = if (item.days == 0) {
        context.getString(R.string.number_of_days, (604 - item.read) / item.pages_num)
    } else {
        context.getString(R.string.number_of_days, item.days)
    }
    textView.text = text
}

@BindingAdapter("setupKhatmaWerd")
fun setupKhatmaWerd(textView: TextView, item: Khatma) {
    val context = textView.context

    val todayCalendar = Calendar.getInstance()

    val differenceMillis = todayCalendar.timeInMillis - item.time!!
    val daysDifference = (differenceMillis / (1000 * 60 * 60 * 24)).toInt()
    val text = if (item.pages_num == 0) {
        val pages = (604 - item.read) / (item.days - daysDifference + 1)
        pages.toString()
    } else {
        item.pages_num.toString()
    }
    textView.text = text
}

@BindingAdapter("setupKhatmaDetails")
fun bindKhatmaDetails(textView: TextView, item: Khatma) {
    val context = textView.context
    val text =
        context.getString(R.string.khatma_details, 604 - item.read)

    textView.text = text
}

@BindingAdapter("setupMainKhatmaSteps")
fun bindMainKhatmaSteps(textView: TextView, item: Khatma?) {
    item?.let {
        val context = textView.context
        val text =
            context.getString(R.string.reading_steps, item.pages_num)

        textView.text = text
    }
}

@BindingAdapter("setupMainKhatmaRemaining")
fun bindMainKhatmaRemaining(textView: TextView, item: Khatma?) {
    item?.let {
        val context = textView.context
        if (item.pages_num != 0) {
            val text =
                context.getString(R.string.ending_in, (604 - item.read) / item.pages_num)
            textView.text = text
        }
    }

}

@BindingAdapter("setupMainKhatmaRemaining")
fun bindKhatmaLocation(textView: TextView, item: Khatma?) {
    item?.let {
        val context = textView.context
        if (item.pages_num != 0) {
            val text =
                context.getString(R.string.ending_in, (604 - item.read) / item.pages_num)
            textView.text = text
        }
    }

}

@BindingAdapter("setupKhatmaProgress")
fun bindKhatmaProgress(progressBar: ProgressBar, item: Khatma?) {
    item?.let {
        val perc = (item.read / 604.0) * 100
        progressBar.secondaryProgress = perc.toInt()
    }

}

@BindingAdapter("setupAyah")
fun setupAyah(textView: TextView, item: Ayat?) {
    val sharedPref = GlobalPreferences(textView.context)
    item?.let {
        textView.text =
            "${item.surah} : ${textView.context.getString(R.string.ayah)} ${
                String.format(
                    Locale(
                        sharedPref.getLocale()
                    ), "%d", item.number
                )
            }"
    }
}