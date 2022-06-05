package com.crazyidea.alsalah.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat


internal class PhoneCallListener(ctx: Context, list: ArrayList<String>) :
    PhoneStateListener() {
    var count = 0
    var context: Context
    var list: ArrayList<String>
    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        super.onCallStateChanged(state, incomingNumber)
        Log.v(this.javaClass.simpleName, "List Size ::$list")
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> Log.v(
                this.javaClass.simpleName,
                "Inside Ringing State::"
            )
            TelephonyManager.CALL_STATE_IDLE -> {
                var phone_number: String? = null
                Log.v(this.javaClass.simpleName, "Inside Idle State::")
                if (list.size > count) {
                    phone_number = list[count]
                    count++
                }
                phone_number?.let { nextCalling(it) }
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> Log.v(
                this.javaClass.simpleName,
                "Inside OFFHOOK State::"
            )
            else -> {}
        }
    }

    private fun nextCalling(phone_number: String) {
        val callIntent1 = Intent(Intent.ACTION_CALL)
        callIntent1.data = Uri.parse("tel:$phone_number")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        context.startActivity(callIntent1)
    }

    init {
        context = ctx
        this.list = list
    }
}