package com.crazyidea.alsalah.notifications

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.BaseRepository

import com.crazyidea.alsalah.utils.sendNotification

class SalahSoon(
    override val title: String,
    override val CHANNEL_ID: String,
    override val context: Context,
    override var repository: BaseRepository? = null
) : AppNotification {

    override fun showNotification() {
        sendNotification(
            context,
            CHANNEL_ID,
            getNotificationTitle(),
            context.getString(R.string.continue_using),
            getSound()
        )
    }

    override fun getSound(): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.before_prayer)
    }

    override fun getNotificationTitle(): String {
        return when (title) {
            "fajr" -> context.getString(R.string.fajr_prayer_soon_notification)
            "zuhr" ->
                context
                    .getString(R.string.zuhr_prayer_soon_notification)
            "asr" ->
                context
                    .getString(R.string.asr_prayer_soon_notification)
            "maghrib" ->
                context
                    .getString(R.string.maghrib_prayer_soon_notification)
            "isha" ->
                context
                    .getString(R.string.isha_prayer_soon_notification)
            else ->
                context
                    .getString(R.string.prayer_notification)
        }
    }
}