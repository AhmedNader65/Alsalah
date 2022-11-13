package com.crazyidea.alsalah.receiver

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import android.widget.VideoView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.notifications.*
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.AzanSettings
import com.crazyidea.alsalah.ui.setting.AzkarSettings
import com.crazyidea.alsalah.utils.SubtitleView
import com.crazyidea.alsalah.utils.setAlarm
import com.crazyidea.alsalah.utils.setLocale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class FridayReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    lateinit var CHANNEL_ID: String
    var fridayNotification = true

    lateinit var locale: Locale
    override fun onReceive(context: Context, intent: Intent?) {

        runBlocking {
            val azkarPref = dataStoreManager.settingsDataStore.data.first()
            fridayNotification = azkarPref[AppSettings.FRIDAY_NOTIFICATIONS] ?: true
            val appPref = dataStoreManager.settingsDataStore.data.first()
            locale = Locale(appPref[AppSettings.APP_LANGUAGE] ?: "ar")
        }
        val context = context.setLocale(locale)
        CHANNEL_ID = "Friday_notifications"
        val text = context.resources.openRawResource(R.raw.friday)
            .bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(text)
        val friday = jsonArray.getJSONObject(Random().nextInt(jsonArray.length()))
        var title = friday.getString("Title")
        var body = friday.getString("Body")
        val notificationTitle = intent?.getStringExtra("friday")
        if (notificationTitle == "friday1") {
            title =
                "قد ورد عن النبي صلى الله عليه وآله وسلم: «مَنْ قَرَأَ سُورَةَ الْكَهْفِ يَوْمَ الْجُمُعَةِ سَطَعَ لَهُ نُورٌ مِنْ تَحْتِ قَدَمِهِ إِلَى عَنَانِ السِّمَاءِ يُضِيءُ بِهِ يَوْمَ الْقِيَامَةِ، وَغُفِرَ لَهُ مَا بَيْنَ الْجُمُعَتَيْنِ»"
            body = "لا تنسوا قراءة سورة الكهف"
        }
        if (intent?.getBooleanExtra("repeatable", false) == true) {
            val calendar = Calendar.getInstance()
            val currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            if (currentHourOfDay < 20) {
                calendar.set(Calendar.HOUR_OF_DAY, currentHourOfDay + 3)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                setAlarm(
                    context,
                    "friday",
                    "friday2",
                    calendar.timeInMillis,
                    repeatable = true
                )
            }
        }

        if (fridayNotification)
            showNotification(
                Friday(
                    title = title,
                    msg = body,
                    CHANNEL_ID,
                    context
                )
            )
    }


    private fun showNotification(appNotification: AppNotification) {
        appNotification.showNotification()
    }


}

