package com.crazyidea.alsalah.ui.tryingggg.compass

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.withRotation
import com.crazyidea.alsalah.R
import com.google.android.material.animation.ArgbEvaluatorCompat
import io.github.cosinekitty.astronomy.Ecliptic
import kotlin.math.abs
import kotlin.math.cos

class SolarDraw(context: Context) {

    fun sunColor(progress: Float) =
        ArgbEvaluatorCompat.getInstance().evaluate(progress, 0xFFFFF9C4.toInt(), 0xFFFF9100.toInt())

    fun sun(
        canvas: Canvas, cx: Float, cy: Float, r: Float, color: Int? = null, small: Boolean = false
    ) {
        val drawable = if (small) smallSunDrawable!! else sunDrawable!!
        drawable.setTintList(color?.let(ColorStateList::valueOf))
        drawable.setBounds((cx - r).toInt(), (cy - r).toInt(), (cx + r).toInt(), (cy + r).toInt())
        drawable.draw(canvas)
    }

    private val sunDrawable = AppCompatResources.getDrawable(context,R.drawable.ic_sun_compass)
    private val smallSunDrawable = AppCompatResources.getDrawable(context,R.drawable.ic_sun_small)

    fun moon(
        canvas: Canvas, sun: Ecliptic, moon: Ecliptic, cx: Float, cy: Float, r: Float,
        angle: Float? = null
    ) {
        moonRect.set(cx - r, cy - r, cx + r, cy + r)
        moonDrawable.setBounds( // same as above
            (cx - r).toInt(), (cy - r).toInt(), (cx + r).toInt(), (cy + r).toInt()
        )
        moonDrawable.draw(canvas)
        val phase = (moon.elon - sun.elon).let { it + if (it < 0) 360 else 0 }
        canvas.withRotation(angle ?: if (phase < 180.0) 180f else 0f, cx, cy) {
            val arcWidth = (cos(Math.toRadians(phase)) * r).toFloat()
            moonOval.set(cx - abs(arcWidth), cy - r, cx + abs(arcWidth), cy + r)
            ovalPath.rewind()
            ovalPath.arcTo(moonOval, 90f, if (arcWidth > 0) 180f else -180f)
            ovalPath.arcTo(moonRect, 270f, 180f)
            ovalPath.close()
            drawPath(ovalPath, moonShadowPaint)
        }
    }
    private val moonDrawable = AppCompatResources.getDrawable(context,R.drawable.ic_moon)!!
    private val ovalPath = Path()
    private val moonRect = RectF()
    private val moonOval = RectF()

    private val moonShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = 0x90000000.toInt()
        it.style = Paint.Style.FILL_AND_STROKE
    }

}
