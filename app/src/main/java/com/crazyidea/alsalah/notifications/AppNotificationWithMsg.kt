package com.crazyidea.alsalah.notifications

import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.crazyidea.alsalah.utils.GlobalPreferences

interface AppNotificationWithMsg: AppNotification {
    val msg: String
    suspend fun getMsg(): String
}