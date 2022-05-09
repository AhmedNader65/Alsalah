package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*
import javax.inject.Inject

class CommonUtils {
    companion object {
        fun setLocale(context: Context, lang: String?) {
            val globalPreferences = GlobalPreferences(context)
            globalPreferences.storeLocale("ar")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val overrideConfiguration: Configuration = context.resources.configuration
                overrideConfiguration.setLocales(LocaleList(Locale(lang)))
                val context = context.createConfigurationContext(overrideConfiguration)
                val resources: Resources = context.resources
            } else {
                val res = context.resources
                // Change locale settings in the app.
                val dm = res.displayMetrics
                val conf = res.configuration
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    conf.setLocale(Locale(lang)) // API 17+ only.
                }
                // Use conf.locale = new Locale(...) if targeting lower versions
                // Use conf.locale = new Locale(...) if targeting lower versions
                res.updateConfiguration(conf, dm)
            }
        }
    }
}