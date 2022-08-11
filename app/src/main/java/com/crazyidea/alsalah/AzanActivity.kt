package com.crazyidea.alsalah

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crazyidea.alsalah.databinding.ActivityAzanBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.setLocale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class AzanActivity : AppCompatActivity() {
    var position = 0

    @Inject
    lateinit var globalPreferences: GlobalPreferences
    override fun attachBaseContext(newBase: Context) {
        runBlocking{
            super.attachBaseContext(ContextWrapper(newBase.setLocale()))
        }
    }

    lateinit var mp: MediaPlayer
    private lateinit var binding: ActivityAzanBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAzanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mp = MediaPlayer.create(this, getAzanSound(globalPreferences, this))
        binding.videoView.setOnPreparedListener {

            val videoRatio = it.videoWidth / it.videoHeight.toFloat()
            val screenRatio = binding.videoView.width / binding.videoView.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                binding.videoView.scaleX = scaleX
            } else {
                binding.videoView.scaleY = 1f / scaleX
            }
            val speed = it.duration.toFloat() / mp.duration.toFloat()
            val myPlayBackParams = PlaybackParams()
            Toast.makeText(this, "${mp.duration} >>> ${it.duration} >>> $speed", Toast.LENGTH_SHORT)
                .show()
            myPlayBackParams.speed = speed //you can set speed here

            it.playbackParams = myPlayBackParams
            binding.videoView.seekTo(position)
            mp.start()
            if (position == 0) {
                binding.videoView.start()
            }
        }
        playVideo()
    }

    private fun getAzanSound(globalPreferences: GlobalPreferences, context: Context): Uri {
        val azanId = globalPreferences.getAzan()
        val azanRes = when (azanId) {
            1 -> R.raw.mecca
            2 -> R.raw.madny
            3 -> R.raw.aqsa
            4 -> R.raw.menshawy
            5 -> R.raw.abdelbaset
            else -> R.raw.azan

        }
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + azanRes)

    }

    private fun playVideo() {
        try {
            // ID of video file.
            val id: Int = R.raw.azan_vid
            val uri: Uri =
                Uri.parse("android.resource://$packageName/$id")
            Timber.i("Video URI: $uri")
            binding.videoView.setVideoURI(uri)
            binding.videoView.requestFocus()
        } catch (e: Exception) {
            Timber.e("Error Play Raw Video: " + e.message)
            Toast.makeText(this, "Error Play Raw Video: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}

