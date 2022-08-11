package com.crazyidea.alsalah.notifications

import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.data.repository.BaseRepository
import com.crazyidea.alsalah.utils.GlobalPreferences

interface AppNotification {
    val title: String
    val CHANNEL_ID: String
    val globalPreferences: GlobalPreferences
    val context: Context
    var repository: BaseRepository?

    fun showNotification()

    fun getSound(): Uri
    fun getNotificationTitle(): String
}