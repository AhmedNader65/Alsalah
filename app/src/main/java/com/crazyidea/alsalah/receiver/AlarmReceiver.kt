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
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import android.widget.VideoView
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.notifications.*
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.SubtitleView
import com.crazyidea.alsalah.utils.setAlarm
import com.crazyidea.alsalah.utils.setLocale
import dagger.hilt.android.AndroidEntryPoint
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
    lateinit var globalPreferences: GlobalPreferences
    lateinit var CHANNEL_ID: String
    override fun onReceive(context: Context, intent: Intent?) {
        Log.e("receiver", "received")
        val context = context.setLocale()
        CHANNEL_ID = globalPreferences.getPrayerChannelId()

        if (intent?.hasExtra("salah") == true) {
            var overlayEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                overlayEnabled = Settings.canDrawOverlays(context);

            }
            val salah = Salah(
                title = intent.getStringExtra("salah")!!,
                CHANNEL_ID = CHANNEL_ID,
                globalPreferences = globalPreferences,
                context = context
            )
            if (!overlayEnabled) {
                showNotification(salah)
            } else {
                showDialog(salah)
                salah.callList()
            }

            if (globalPreferences.isAfterPrayerNotification()) {
                Timber.e("SETTING AFTER PRAYER ALARM")
                setAlarm(
                    context,
                    "azkar",
                    context.getString(R.string.taqabal_allah),
                    System.currentTimeMillis().plus(20 * 60000),
                    category = "أذكار بعد السلام من الصلاة المفروضة"
                )
            }
        } else if (intent?.hasExtra("khatma") == true) {
            showNotification(
                Khatma(
                    title = intent.getStringExtra("khatma").toString(),
                    "Khatma_" + intent.getStringExtra("khatma"),
                    globalPreferences,
                    context
                )
            )
        } else if (intent?.hasExtra("azkar") == true) {
            showNotification(
                Azkar(
                    title = intent.getStringExtra("azkar").toString(),
                    intent.getStringExtra("zekr_type").toString(),
                    "after_prayer_" + intent.getStringExtra("azkar"),
                    globalPreferences,
                    context,
                    azkarRepository
                )
            )
        } else if (intent?.hasExtra("before_prayer") == true) {
            if (globalPreferences.notifyBeforePrayer())
                showNotification(
                    SalahSoon(
                        title = intent.getStringExtra("before_prayer").toString(),
                        CHANNEL_ID = "before_prayer_",
                        globalPreferences = globalPreferences,
                        context = context
                    )
                )
        } else if (intent?.hasExtra("iqama") == true) {
            if (globalPreferences.notifyIqama())
                showNotification(
                    Eqammah(
                        CHANNEL_ID = "iqama",
                        globalPreferences = globalPreferences,
                        context = context
                    )
                )
        }
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
                dialog.window!!
                    .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                dialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
            dialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            dialog.show()
            val mp = MediaPlayer.create(context, salah.getSound())
            subtitlesView.player = mp
            subtitlesView.setSubSource(getSubtitles(), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);

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
                val id: Int =
                    if (globalPreferences.getAzanBackground()) R.raw.azan_vid2 else R.raw.azan_vid
                val uri: Uri =
                    Uri.parse("android.resource://${context.packageName}/$id")
                Timber.i("Video URI: $uri")
                videoView.setVideoURI(uri)
                videoView.requestFocus()
            } catch (e: Exception) {
                Timber.e("Error Play Raw Video: " + e.message)
                Toast.makeText(
                    context,
                    "Error Play Raw Video: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun getSubtitles(): Int {
        return when (globalPreferences.getAzan()) {
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

