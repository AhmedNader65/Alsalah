package com.crazyidea.alsalah.notifications

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import com.crazyidea.alsalah.AzanActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.repository.BaseRepository
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.sendNotification
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import timber.log.Timber

class Salah(
    override val title: String,
    override val CHANNEL_ID: String,
    override val globalPreferences: GlobalPreferences,
    override val context: Context,
    override var repository: BaseRepository? = null
) : AppNotification {
    private var listOfNumbers: List<String>? = null

    private var phoneListener: PhoneListener? = null
    var count = 0

    override fun showNotification() {
        sendNotification(
            context,
            CHANNEL_ID,
            getNotificationTitle(),
            context.getString(R.string.continue_using),
            getSound(),
            Intent(context, AzanActivity::class.java)
        )
        callList()
    }

    fun callList() {
        if (title == "fajr") {
            GlobalScope.async {
                val listOfContacts = (repository as FajrListRepository).getFajrList()
                listOfNumbers = listOfContacts.map { it.number }
            }
            val telephonyManager: TelephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyManager.registerTelephonyCallback(
                    context.mainExecutor,
                    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                        override fun onCallStateChanged(state: Int) {
                            listOfNumbers?.let {
                                checkState(context, state, listOfNumbers)
                            }
                        }
                    })
            } else {
                phoneListener = PhoneListener { checkState(context, it, listOfNumbers) }
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    override fun getSound(): Uri {
        val azanId = globalPreferences.getAzan()
        val azanRes = when (azanId) {
            1 -> R.raw.mecca
            2 -> R.raw.madny
            3 -> R.raw.aqsa
            4 -> R.raw.menshawy
            5 -> R.raw.abdelbaset
            6 -> R.raw.azan
            else -> R.raw.azan

        }
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + azanRes)

    }

    override fun getNotificationTitle(): String {
        return when (title) {
            "fajr" -> {
                context.getString(R.string.fajr_prayer_notification)
            }
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


    private fun checkState(context: Context, state: Int, listOfNumbers: List<String>?) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> Timber.tag(this.javaClass.simpleName)
                .v("Inside Ringing State::")
            TelephonyManager.CALL_STATE_IDLE -> {
                var phone_number: String? = null
                Timber.tag(this.javaClass.simpleName).v("Inside Idle State::")
                if (listOfNumbers?.size!! > count) {
                    phone_number = listOfNumbers[count]
                    count++
                }
                phone_number?.let { nextCalling(context, it) }
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> Timber.tag(this.javaClass.simpleName)
                .v("Inside OFFHOOK State::")
            else -> {}
        }
    }

    private fun nextCalling(context: Context, phone_number: String) {

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone_number"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }
}

class PhoneListener(val checkState: (state: Int) -> Unit) : PhoneStateListener() {
    @Deprecated("Deprecated in Java")
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        Timber.tag("sate changed").e("here2")
        checkState(state)
    }
}