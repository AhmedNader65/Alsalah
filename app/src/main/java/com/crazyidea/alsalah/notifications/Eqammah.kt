package com.crazyidea.alsalah.notifications

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.BaseRepository

import com.crazyidea.alsalah.utils.sendNotification

class Eqammah(
    override val title: String="",
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
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.eqamah)
    }

    override fun getNotificationTitle(): String {
        return  context
            .getString(R.string.iqama_notification)
    }

}