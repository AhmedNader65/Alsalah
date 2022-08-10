package com.crazyidea.alsalah

import android.app.Application
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import android.widget.VideoView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.crazyidea.alsalah.notifications.Salah
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.SubtitleView
import com.crazyidea.alsalah.workManager.RefreshDataWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    override fun onCreate() {
        super.onCreate()
        delayedInit()
        // This will initialise Timber
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
        showDialog(Salah(
                title = "asr",
        CHANNEL_ID = "Salah_asr",
        globalPreferences = GlobalPreferences(this),
        context = this
        ))
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
//                val speed = it.duration.toFloat() / mp.duration.toFloat()
//                val myPlayBackParams = PlaybackParams()
//                Toast.makeText(
//                    context,
//                    "${mp.duration} >>> ${it.duration} >>> $speed",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//                myPlayBackParams.speed = speed //you can set speed here
//
//                it.playbackParams = myPlayBackParams
                it.isLooping = true
                videoView.seekTo(position)
                videoView.addSubtitleSource(context.resources.openRawResource(R.raw.abdelbaset_srt),
                    MediaFormat.createSubtitleFormat("text/srt", Locale("ar").language))

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
                    if (GlobalPreferences(this).getAzanBackground()) R.raw.azan_vid2 else R.raw.azan_vid
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

        val azanId = GlobalPreferences(this).getAzan()
        return  when (azanId) {
            1 -> R.raw.mecca_srt
            2 -> R.raw.madny_srt
            3 -> R.raw.aqsa
            4 -> R.raw.menshawy_srt
            5 -> R.raw.abdelbaset_srt
            6 -> R.raw.azan_srt
            else -> R.raw.mecca_srt

        }

    }


    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }


    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest
                = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest)
    }

}
