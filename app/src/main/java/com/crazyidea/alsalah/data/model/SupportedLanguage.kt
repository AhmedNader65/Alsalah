package com.crazyidea.alsalah.data.model

import com.crazyidea.alsalah.App

data class SupportedLanguage(
    val Name: String,
    val shortcut: String,
    val selected: String = App.instance.getAppLocale().language
) {
    var checked: Boolean = selected == shortcut
}

