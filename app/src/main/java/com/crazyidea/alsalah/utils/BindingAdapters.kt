package com.crazyidea.alsalah.utils

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.crazyidea.alsalah.R
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
    text += String.format(Locale(sharedPref.getLocale()),"%d", surah.id)
    text += " - "
    text += context.getString(R.string.surah_ayat)
    text += " "
    text += String.format(Locale(sharedPref.getLocale()),"%d",  surah.numberOfAyahs)
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
    var text = String.format(Locale(sharedPref.getLocale()),"%d", surah.page)

    textView.text = text
}