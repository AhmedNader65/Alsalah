package com.crazyidea.alsalah.utils

import android.R.attr.endColor
import android.R.attr.startColor
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.util.*


private const val UPDATE_INTERVAL = 100L
private const val DEBUG = false

class SubtitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), Runnable {

    private var halfWidth: Int = 0
    lateinit var player: MediaPlayer

    private var track: TreeMap<Long, Line>? = null
    var animationStarted = false
    var animationStarted2 = false
    val originalY = translationY
    override fun run() {
        if (track != null) {
            val seconds = player.currentPosition / 1000
            val newText = ((if (DEBUG) "[" + secondsToDuration(seconds) + "] " else "")
                    + getTimedText(player.currentPosition.toLong()))
            if (text != newText && newText.length < 3) {
                if (!animationStarted) {
                    animationStarted = true
                    val pvhAlpha = PropertyValuesHolder.ofFloat(ALPHA, 0F)
                    val pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, translationY - 100)
                    val animation: ObjectAnimator =
                        ObjectAnimator.ofPropertyValuesHolder(this, pvhAlpha, pvhY)
//                    val animation: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0F)
                    animation.duration = 400
                    Timber.e("Animation started $halfWidth")
                    animation.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            alpha = 1f
                            translationY = originalY
                            text = newText
                            animationStarted = false
                            Timber.e("Animation ended")
                        }
                    })
                    animation.start()
                }
            } else if (text != newText) {
                if (!animationStarted2) {
                    text = newText
                    animationStarted2 = true
                    val animation2: ObjectAnimator =
                        ObjectAnimator.ofFloat(this, "translationY", translationY - 100)
                    animation2.duration = 1000
                    Timber.e("Animation2 started $halfWidth")
                    animation2.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            animationStarted2 = false
                            Timber.e("Animation2 ended")
                        }
                    })
                    animation2.start()
                }
            }
        }
        postDelayed(this, UPDATE_INTERVAL)
    }

    private fun getTimedText(currentPosition: Long): String {
        var result = ""
        track?.entries?.forEach {
            if (currentPosition < it.key)
                return@forEach
            if (currentPosition < it.value.to) result = it.value.text
        }
        return result.trim()
    }

    // To display the seconds in the duration format 00:00:00
    fun secondsToDuration(seconds: Int): String? {
        return java.lang.String.format(
            "%02d:%02d:%02d", seconds / 3600,
            seconds % 3600 / 60, seconds % 60, Locale.US
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed(this, UPDATE_INTERVAL)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        halfWidth = MeasureSpec.getSize(widthMeasureSpec) / 2 - 5
        val shader: Shader = LinearGradient(
            0F, lineHeight.toFloat()/2, width.toFloat()/4, lineHeight.toFloat()/2,
            Color.parseColor("#D2AC47"), Color.parseColor("#F7EF8A"),
            Shader.TileMode.MIRROR
        )
        paint.shader = shader
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(this)
    }


    fun setSubSource(ResID: Int, mime: String) {
        if (mime == MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP)
            track = getSubtitleFile(ResID)
        else throw UnsupportedOperationException("Parser only built for SRT subs")
    }

    /////////////Utility Methods:
    //Based on https://github.com/sannies/mp4parser/
    //Apache 2.0 Licence at: https://github.com/sannies/mp4parser/blob/master/LICENSE
    @Throws(IOException::class)
    fun parse(`is`: InputStream): TreeMap<Long, Line> {
        val r = LineNumberReader(InputStreamReader(`is`, "UTF-8"))
        val track = TreeMap<Long, Line>()
        while (r.readLine() != null) /*Read cue number*/ {
            val timeString = r.readLine()
            var lineString = ""
            var s: String
            while (!(r.readLine().also { s = it } == null || s.trim { it <= ' ' } == "")) {
                lineString += """
                $s
                
                """.trimIndent()
            }
            val startTime: Long =
                parse(timeString.split("-->".toRegex()).toTypedArray()[0])
            val endTime: Long =
                parse(timeString.split("-->".toRegex()).toTypedArray()[1])
            track[startTime] = Line(
                startTime, endTime,
                lineString
            )
        }
        return track
    }

    private fun parse(`in`: String): Long {
        val hours = `in`.split(":".toRegex()).toTypedArray()[0].trim { it <= ' ' }.toLong()
        val minutes = `in`.split(":".toRegex()).toTypedArray()[1].trim { it <= ' ' }.toLong()
        val seconds = `in`.split(":".toRegex()).toTypedArray()[2].split(",".toRegex())
            .toTypedArray()[0].trim { it <= ' ' }
            .toLong()
        val millies = `in`.split(":".toRegex()).toTypedArray()[2].split(",".toRegex())
            .toTypedArray()[1].trim { it <= ' ' }
            .toLong()
        return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies
    }

    private fun getSubtitleFile(resId: Int): TreeMap<Long, Line>? {
        var inputStream: InputStream? = null
        try {
            inputStream = resources.openRawResource(resId)
            return parse(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    class Line(var from: Long, var to: Long, var text: String)
}