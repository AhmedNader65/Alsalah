package com.crazyidea.alsalah.notifications

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.DefaultAzkarRepository
import com.crazyidea.alsalah.data.repository.BaseRepository

import com.crazyidea.alsalah.utils.sendNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Friday(
    override val title: String,
    override val msg: String,
    override val CHANNEL_ID: String,
    override val context: Context,
    override var repository: BaseRepository? = null
) : AppNotificationWithMsg {
    override fun showNotification() {
        GlobalScope.launch {
            sendNotification(
                context,
                CHANNEL_ID,
                title,
                msg,
            )
        }
    }

    override fun getSound(): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.before_prayer)
    }

    override fun getNotificationTitle(): String {
        return title
    }

    override suspend fun getMsg(): String {
        return msg
    }
}