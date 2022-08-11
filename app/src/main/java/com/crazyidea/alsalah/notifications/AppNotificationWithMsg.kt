package com.crazyidea.alsalah.notifications

interface AppNotificationWithMsg: AppNotification {
    val msg: String
    suspend fun getMsg(): String
}