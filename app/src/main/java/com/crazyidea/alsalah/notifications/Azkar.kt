package com.crazyidea.alsalah.notifications

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.DefaultAzkarRepository
import com.crazyidea.alsalah.data.repository.BaseRepository
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.sendNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Azkar(
    override val title: String,
    override val msg: String,
    override val CHANNEL_ID: String,
    override val globalPreferences: GlobalPreferences,
    override val context: Context,
    override var repository: BaseRepository? = null
) : AppNotificationWithMsg {
    override fun showNotification() {
        GlobalScope.launch {
            sendNotification(
                context,
                CHANNEL_ID,
                title,
                getMsg(),
            )
        }
    }

    override fun getSound(): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.before_prayer)
    }

    override fun getNotificationTitle(): String {
        return context
            .getString(R.string.iqama_notification)
    }

    override suspend fun getMsg(): String {
        return withContext(Dispatchers.IO) {
            (repository as DefaultAzkarRepository).getRandomAzkar(
                msg
            ).content
        }
    }
}