package com.crazyidea.alsalah.notifications

import android.content.Context
import android.net.Uri
import com.crazyidea.alsalah.data.repository.BaseRepository


interface AppNotification {
    val title: String
    val CHANNEL_ID: String
    val context: Context
    var repository: BaseRepository?

    fun showNotification()

    fun getSound(): Uri
    fun getNotificationTitle(): String
}