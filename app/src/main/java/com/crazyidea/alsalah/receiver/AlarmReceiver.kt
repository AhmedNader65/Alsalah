package com.crazyidea.alsalah.receiver

import android.app.Dialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
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
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.notifications.*
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.AzanSettings
import com.crazyidea.alsalah.ui.setting.AzanSettings.WAS_SILENT
import com.crazyidea.alsalah.ui.setting.AzkarSettings
import com.crazyidea.alsalah.utils.SubtitleView
import com.crazyidea.alsalah.utils.setAlarm
import com.crazyidea.alsalah.utils.setLocale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var fajrRepository: FajrListRepository

    @Inject
    lateinit var azkarRepository: AzkarRepository

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    lateinit var CHANNEL_ID: String
    var azanNotification = true
    var azanSound = 1
    var channel: String = ""
    var iqamaNotification = true
    var wasSilent = false
    var setSilent = false
    var mosqueBackground = true
    var beforePrayNotification = true
    var afterPrayerNotification = true
    lateinit var locale: Locale
    override fun onReceive(context: Context, intent: Intent?) {

        runBlocking {
            val azkarPref = dataStoreManager.azkarSettings.data.first()
            afterPrayerNotification = azkarPref[AzkarSettings.AFTER_PRAYER_AZKAR] ?: true
            val azanPref = dataStoreManager.settingsAzan.data.first()
            azanNotification = azanPref[AzanSettings.AZAN_NOTIFICATION] ?: true
            azanSound = azanPref[AzanSettings.AZAN_SOUND] ?: 1
            beforePrayNotification = azanPref[AzanSettings.BEFORE_PRAYER_REMINDER] ?: true
            iqamaNotification = azanPref[AzanSettings.IQAMA_NOTIFICATION] ?: true
            wasSilent = azanPref[AzanSettings.IQAMA_NOTIFICATION] ?: false
            mosqueBackground = azanPref[AzanSettings.AZAN_MOSQUE_BG] ?: true
            channel = azanPref[AzanSettings.AZAN_CHANNEL] ?: ("PRAYER" + Random().nextInt())
            val appPref = dataStoreManager.settingsDataStore.data.first()
            locale = Locale(appPref[AppSettings.APP_LANGUAGE] ?: "ar")
            setSilent = appPref[AppSettings.SILENT_PHONE] ?: false
        }
        val context = context.setLocale(locale)
        CHANNEL_ID = channel

        if (intent?.hasExtra("salah") == true) {
            var overlayEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                overlayEnabled = Settings.canDrawOverlays(context)

            }
            val salah = Salah(
                title = intent.getStringExtra("salah")!!,
                CHANNEL_ID = CHANNEL_ID,
                context = context,
                notifyAzan = azanNotification,
                azanSound = azanSound
            )
            if (!overlayEnabled) {
                showNotification(salah)
            } else {
                if (azanNotification) showDialog(salah)
                salah.callList()
            }
             setAlarm(
                    context,
                    "azkar",
                    context.getString(R.string.taqabal_allah),
                    System.currentTimeMillis().plus(20 * 60000),
                    category = "أذكار بعد السلام من الصلاة المفروضة"
                )

        } else if (intent?.hasExtra("khatma") == true) {
            showNotification(
                Khatma(
                    title = intent.getStringExtra("khatma").toString(),
                    "Khatma_" + intent.getStringExtra("khatma"),
                    context
                )
            )
        } else if (intent?.hasExtra("azkar") == true) {
            val title = intent.getStringExtra("azkar").toString()

            if (title == context.getString(R.string.taqabal_allah)) {
                revertSilent(context)
                if (afterPrayerNotification)
                    showNotification(
                        Azkar(
                            title = title,
                            intent.getStringExtra("zekr_type").toString(),
                            "after_prayer_" + intent.getStringExtra("azkar"),
                            context,
                            azkarRepository
                        )
                    )
            } else
                showNotification(
                    Azkar(
                        title = title,
                        intent.getStringExtra("zekr_type").toString(),
                        "after_prayer_" + intent.getStringExtra("azkar"),
                        context,
                        azkarRepository
                    )
                )
        } else if (intent?.hasExtra("before_prayer") == true) {
            if (beforePrayNotification) showNotification(
                SalahSoon(
                    title = intent.getStringExtra("before_prayer").toString(),
                    CHANNEL_ID = "before_prayer_",
                    context = context
                )
            )
        } else if (intent?.hasExtra("iqama") == true) {
            if (setSilent) setSilent(context)
            if (iqamaNotification) showNotification(
                Eqammah(
                    CHANNEL_ID = "iqama", context = context
                )
            )

        }
    }

    private fun setSilent(context: Context) {
        val notificationManager =
            (context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            )
            context.startActivity(intent)
        } else {
            val mode = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            GlobalScope.launch {
                dataStoreManager.settingsAzan.edit { settings ->
                    settings[WAS_SILENT] = mode.ringerMode == AudioManager.RINGER_MODE_SILENT
                }
            }
            mode.ringerMode = AudioManager.RINGER_MODE_SILENT
        }
    }

    private fun revertSilent(context: Context) {
        val mode = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!wasSilent)
            mode.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    private fun showDialog(salah: Salah) {
        val context = salah.context
        Handler(Looper.getMainLooper()).post {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_azan)
            dialog.setCancelable(false)

            val videoView: VideoView = dialog.findViewById(R.id.videoView)
            val subtitlesView: SubtitleView = dialog.findViewById(R.id.subtitles)
            val closeBtn: ImageButton = dialog.findViewById(R.id.close)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                dialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
            dialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            dialog.show()
            val mp = MediaPlayer.create(context, salah.getSound())
            subtitlesView.player = mp
            subtitlesView.setSubSource(getSubtitles(), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP)

            videoView.setOnPreparedListener {
                val position = 0

                val videoRatio = it.videoWidth / it.videoHeight.toFloat()
                val screenRatio = videoView.width / videoView.height.toFloat()
                val scaleX = videoRatio / screenRatio
                if (scaleX >= 1f) {
                    videoView.scaleX = scaleX
                } else {
                    videoView.scaleY = 1f / scaleX
                }
                it.isLooping = true
                videoView.seekTo(position)
                videoView.addSubtitleSource(
                    context.resources.openRawResource(R.raw.abdelbaset_srt),
                    MediaFormat.createSubtitleFormat("text/srt", Locale("ar").language)
                )

                mp.start()
                videoView.start()

            }
            mp.setOnCompletionListener {
                mp.stop()
                dialog.cancel()
            }
            closeBtn.setOnClickListener {
                dialog.cancel()
                mp.stop()
            }
            try {

                // ID of video file.
                val id: Int = if (mosqueBackground) R.raw.azan_vid2 else R.raw.azan_vid
                val uri: Uri = Uri.parse("android.resource://${context.packageName}/$id")
                Timber.i("Video URI: $uri")
                videoView.setVideoURI(uri)
                videoView.requestFocus()
            } catch (e: Exception) {
                Timber.e("Error Play Raw Video: " + e.message)
                Toast.makeText(
                    context, "Error Play Raw Video: " + e.message, Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun getSubtitles(): Int {
        return when (azanSound) {
            1 -> R.raw.mecca_srt
            2 -> R.raw.madny_srt
            3 -> R.raw.aqsa_srt
            4 -> R.raw.menshawy_srt
            5 -> R.raw.abdelbaset_srt
            6 -> R.raw.azan_srt
            else -> R.raw.mecca_srt
        }

    }


    private fun showNotification(appNotification: AppNotification) {
        appNotification.showNotification()
    }


}

