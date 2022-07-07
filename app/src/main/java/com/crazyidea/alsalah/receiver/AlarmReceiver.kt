package com.crazyidea.alsalah.receiver

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
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
            Timber.e("HERE")
            sendNotification(
                context,
                CHANNEL_ID,
                getTitle(context, intent.getStringExtra("salah")) as String,
                context.getString(R.string.continue_using),
                getAzanSound(globalPreferences, context)
            )

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
                    azkarRepository.getRandomAzkar(intent.getStringExtra("zekr_type").toString()).content
                )
            }
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


    private fun getTitle(context: Context, stringExtra: String?): CharSequence? {
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
