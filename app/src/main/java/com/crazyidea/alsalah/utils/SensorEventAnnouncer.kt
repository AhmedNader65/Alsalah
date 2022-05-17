package com.crazyidea.alsalah.utils

import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.getSystemService

const val STATE_NOT_QEBLA = 0
const val STATE_QEBLA = 1
const val STATE_OTHER = -1

// a11y related state machine that starts in true state and announce if a transition to true happens
class SensorEventAnnouncer(@StringRes private val text: Int, initialState: Boolean = true) {

    private var state = initialState
    private var lastAnnounce = -1L

    fun check(context: Context, newState: Boolean, qebla: Boolean = false): Int {
        if (newState && !state) {
            val now = System.currentTimeMillis()
            if (qebla) {
                if (now - lastAnnounce > 2000L) { // 2 seconds
                    Log.e("true qebla", "yes")
                    // https://stackoverflow.com/a/29423018
                    context.getSystemService<AudioManager>()
                        ?.playSoundEffect(AudioManager.FX_KEY_CLICK)
                    lastAnnounce = now
                    return STATE_QEBLA
                } else {
                    Log.e("true qebla", "no")
                    return STATE_NOT_QEBLA
                }
            }
        }
        state = newState
        return STATE_OTHER
    }
}