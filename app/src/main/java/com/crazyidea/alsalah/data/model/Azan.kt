package com.crazyidea.alsalah.data.model

data class Azan(
    val id: Int,
    val Name: String,
    val shortcut: Int,
    var hasline: Boolean = true,
    var checked: Boolean = false,
    var isPlaying: Boolean = false
)

