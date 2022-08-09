package com.crazyidea.alsalah.receiver

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.crazyidea.alsalah.AzanActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.sendNotification
import com.crazyidea.alsalah.utils.setAlarm
import com.crazyidea.alsalah.utils.setLocale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    private var listOfNumbers: List<String>? = null

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
            if (intent.getStringExtra("salah") == "fajr") {
                GlobalScope.async {
                    val listOfContacts = fajrRepository.getFajrList()
                    listOfNumbers = listOfContacts.map { it.number }
                }
                val telephonyManager: TelephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.e("sdk is", "here")
                    telephonyManager.registerTelephonyCallback(
                        context.mainExecutor,
                        object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                            override fun onCallStateChanged(state: Int) {
                                listOfNumbers?.let {
                                    Log.e("listOfNumbers ", "${it.size}")
                                    checkState(context, state, listOfNumbers)
                                }
                            }
                        })
                } else {
                    Log.e("sdk is", "here2")
                    phoneListener = PhoneListener { checkState(context, it, listOfNumbers) }
                    telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
                }
            }

            Handler(Looper.getMainLooper()).post {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_azan)
                dialog.setCancelable(false)

                val videoView: VideoView = dialog.findViewById(R.id.videoView);
                val closeBtn: ImageButton = dialog.findViewById(R.id.close);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dialog.getWindow()!!
                        .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                } else {
                    dialog.getWindow()!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                }
                dialog.getWindow()!!.setLayout(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )


                dialog.show()
                val mp = MediaPlayer.create(context, getAzanSound(globalPreferences, context))
                videoView.setOnPreparedListener {
                    var position = 0

                    val videoRatio = it.videoWidth / it.videoHeight.toFloat()
                    val screenRatio = videoView.width / videoView.height.toFloat()
                    val scaleX = videoRatio / screenRatio
                    if (scaleX >= 1f) {
                        videoView.scaleX = scaleX
                    } else {
                        videoView.scaleY = 1f / scaleX
                    }
                    val speed = it.duration.toFloat() / mp.duration.toFloat()
                    val myPlayBackParams = PlaybackParams()
                    Toast.makeText(
                        context,
                        "${mp.duration} >>> ${it.duration} >>> $speed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    myPlayBackParams.speed = speed //you can set speed here

                    it.playbackParams = myPlayBackParams
                    videoView.seekTo(position)
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
                    val id: Int = R.raw.azan_vid
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
            var overlayEnabled = false
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                overlayEnabled = Settings.canDrawOverlays(context);

            }
            if (!overlayEnabled) {
                sendNotification(
                    context,
                    CHANNEL_ID,
                    getPrayerTitle(context, intent.getStringExtra("salah")) as String,
                    context.getString(R.string.continue_using),
                    getAzanSound(globalPreferences, context),
                    Intent(context, AzanActivity::class.java)
                )
            } else {
                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()
                sendNotification(
                    context,
                    CHANNEL_ID,
                    getPrayerTitle(context, intent.getStringExtra("salah")) as String,
                    context.getString(R.string.continue_using)
                )
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
            sendNotification(
                context,
                "Khatma_" + intent.getStringExtra("khatma"),
                intent.getStringExtra("khatma").toString(),
                context.getString(R.string.khatma_reminder)
            )
        } else if (intent?.hasExtra("azkar") == true) {
            Timber.e(intent.getStringExtra("zekr_type").toString())
            GlobalScope.launch {
                sendNotification(
                    context,
                    "after_prayer_" + intent.getStringExtra("azkar"),
                    intent.getStringExtra("azkar").toString(),
                    azkarRepository.getRandomAzkar(
                        intent.getStringExtra("zekr_type").toString()
                    ).content
                )
            }
        } else if (intent?.hasExtra("before_prayer") == true) {
            if (globalPreferences.notifyBeforePrayer())
                sendNotification(
                    context,
                    "before_prayer_",
                    getBeforePrayer(context, intent.getStringExtra("before_prayer")) as String,
                    context.getString(R.string.continue_using),
                    getBeforeAzanSound(context),
                )
        }else if (intent?.hasExtra("iqama") == true) {
            if (globalPreferences.notifyIqama())
                sendNotification(
                    context,
                    "iqama",
                    getIqamaTitle(context) as String,
                    context.getString(R.string.continue_using),
                    getBeforeAzanSound(context),
                )
        }
    }

    class PhoneListener(val checkState: (state: Int) -> Unit) : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            Log.e("sate changed", "here2")
            checkState(state)
        }
    }

    private var phoneListener: PhoneListener? = null
    var count = 0
    private fun checkState(context: Context, state: Int, listOfNumbers: List<String>?) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> Log.v(
                this.javaClass.simpleName,
                "Inside Ringing State::"
            )
            TelephonyManager.CALL_STATE_IDLE -> {
                var phone_number: String? = null
                Log.v(this.javaClass.simpleName, "Inside Idle State::")
                if (listOfNumbers?.size!! > count) {
                    phone_number = listOfNumbers[count]
                    count++
                }
                phone_number?.let { nextCalling(context, it) }
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> Log.v(
                this.javaClass.simpleName,
                "Inside OFFHOOK State::"
            )
            else -> {}
        }
    }


    private fun getPrayerTitle(context: Context, stringExtra: String?): CharSequence? {
        return when (stringExtra) {
            "fajr" -> context.getString(R.string.fajr_prayer_notification)
            "zuhr" ->
                context
                    .getString(R.string.zuhr_prayer_notification)
            "asr" ->
                context
                    .getString(R.string.asr_prayer_notification)
            "maghrib" ->
                context
                    .getString(R.string.maghrib_prayer_notification)
            "isha" ->
                context
                    .getString(R.string.isha_prayer_notification)
            else ->
                context
                    .getString(R.string.prayer_notification)
        }
    }
    private fun getIqamaTitle(context: Context): CharSequence {

        return  context
            .getString(R.string.iqama_notification)
    }

    private fun getBeforePrayer(context: Context, stringExtra: String?): CharSequence? {
        return when (stringExtra) {
            "fajr" -> context.getString(R.string.fajr_prayer_soon_notification)
            "zuhr" ->
                context
                    .getString(R.string.zuhr_prayer_soon_notification)
            "asr" ->
                context
                    .getString(R.string.asr_prayer_soon_notification)
            "maghrib" ->
                context
                    .getString(R.string.maghrib_prayer_soon_notification)
            "isha" ->
                context
                    .getString(R.string.isha_prayer_soon_notification)
            else ->
                context
                    .getString(R.string.prayer_notification)
        }
    }

    private fun nextCalling(context: Context, phone_number: String) {

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone_number"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }

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

private fun getBeforeAzanSound(context: Context): Uri {
    return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.before_prayer)

}
