package com.crazyidea.alsalah.ui.tryingggg.compass

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentCompassBinding
import com.crazyidea.alsalah.databinding.FragmentQiblaBinding
import com.crazyidea.alsalah.utils.SensorEventAnnouncer
import com.google.android.material.button.MaterialButton
import kotlin.math.abs

/**
 * Compass/Qibla activity
 */
class CompassScreen : Fragment(R.layout.fragment_compass) {

    private var stopped = false
    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!
    private var sensorManager: SensorManager? = null
    private var orientationSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var magnetometerSensor: Sensor? = null
    private var orientation = 0f
    private var sensorNotFound = false

    private abstract inner class BaseSensorListener : SensorEventListener {
        /*
         * time smoothing constant for low-pass filter 0 ≤ alpha ≤ 1 ; a smaller
         * value basically means more smoothing See:
         * https://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
         */
        private val ALPHA = 0.15f
        private var azimuth: Float = 0f

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        protected fun update(value: Float) {
            // angle between the magnetic north direction
            // 0=North, 90=East, 180=South, 270=West
            val angle = if (stopped) 0f else value + orientation
            if (!stopped) checkIfA11yAnnounceIsNeeded(angle)
            azimuth = lowPass(angle, azimuth)
            binding?.compassView?.angle = azimuth
        }

        /**
         * https://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
         * https://developer.android.com/reference/android/hardware/SensorEvent.html#values
         */
        private fun lowPass(input: Float, output: Float): Float = when {
            abs(180 - input) > 170 -> input
            else -> output + ALPHA * (input - output)
        }
    }

    private val orientationSensorListener = object : BaseSensorListener() {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return
            update(event.values[0])
        }
    }

    private val accelerometerMagneticSensorListener = object : BaseSensorListener() {
        private val acceleration = FloatArray(3)
        private val magneticField = FloatArray(3)
        private var isAccelerationsAvailable = false
        private var isMagneticFieldAvailable = false
        private val rotationMatrix = FloatArray(9)
        private val orientationMatrix = FloatArray(3)

        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return

            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                acceleration[0] = event.values[0]
                acceleration[1] = event.values[1]
                acceleration[2] = event.values[2]
                isAccelerationsAvailable = true
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticField[0] = event.values[0]
                magneticField[1] = event.values[1]
                magneticField[2] = event.values[2]
                isMagneticFieldAvailable = true
            }

            if (isAccelerationsAvailable && isMagneticFieldAvailable &&
                SensorManager.getRotationMatrix(rotationMatrix, null, acceleration, magneticField)
            ) {
                SensorManager.getOrientation(rotationMatrix, orientationMatrix)
                update(Math.toDegrees(orientationMatrix[0].toDouble()).toFloat())
                isAccelerationsAvailable = false
                isMagneticFieldAvailable = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCompassBinding.bind(view)

        binding.byVision.setOnClickListener { checkButtons(binding.byVision) }
        binding.bySunAndMoon.setOnClickListener {
            binding.compassView.showSunMoon(true)
            checkButtons(binding.bySunAndMoon)
        }
        binding.byCompassBtn.setOnClickListener {
            binding.compassView.showSunMoon(false)
            checkButtons(binding.byCompassBtn)
        }
        binding.byCompassBtn.performClick()
        updateCompassOrientation()
    }

    private var stopAnimator: Boolean = false


    fun checkButtons(materialButton: MaterialButton) {
        binding.byCompassBtn.setBackgroundColor(resources.getColor(R.color.white))
        binding.bySunAndMoon.setBackgroundColor(resources.getColor(R.color.white))
        binding.byVision.setBackgroundColor(resources.getColor(R.color.white))
        binding.byVision.setTextColor(resources.getColor(R.color.black))
        binding.bySunAndMoon.setTextColor(resources.getColor(R.color.black))
        binding.byCompassBtn.setTextColor(resources.getColor(R.color.black))
        materialButton.setBackgroundColor(resources.getColor(R.color.text_orange_color))
        materialButton.setTextColor(resources.getColor(R.color.white))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCompassOrientation()
    }

    private fun updateCompassOrientation() {
        orientation = when (activity?.getSystemService<WindowManager>()?.defaultDisplay?.rotation) {
            Surface.ROTATION_0 -> 0f
            Surface.ROTATION_90 -> 90f
            Surface.ROTATION_180 -> 180f
            Surface.ROTATION_270 -> 270f
            else -> 0f
        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager = activity?.getSystemService()
        val sensorManager = sensorManager ?: return
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        if (orientationSensor != null) {
            sensorManager.registerListener(
                orientationSensorListener, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST
            )

        } else if (accelerometerSensor != null && magnetometerSensor != null) {
            sensorManager.registerListener(
                accelerometerMagneticSensorListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
            sensorManager.registerListener(
                accelerometerMagneticSensorListener, magnetometerSensor,
                SensorManager.SENSOR_DELAY_GAME
            )

        } else {
            sensorNotFound = true
        }
    }

    override fun onPause() {
        stopAnimator = true
        if (orientationSensor != null)
            sensorManager?.unregisterListener(orientationSensorListener)
        else if (accelerometerSensor != null && magnetometerSensor != null)
            sensorManager?.unregisterListener(accelerometerMagneticSensorListener)
        super.onPause()
    }

    // Accessibility announcing helpers on when the phone is headed on a specific direction
    private var northAnnouncer = SensorEventAnnouncer(R.string.north)
    private var eastAnnouncer = SensorEventAnnouncer(R.string.east, false)
    private var westAnnouncer = SensorEventAnnouncer(R.string.west, false)
    private var southAnnouncer = SensorEventAnnouncer(R.string.south, false)
    private var qiblaAnnouncer = SensorEventAnnouncer(R.string.qibla, false)
    private fun checkIfA11yAnnounceIsNeeded(angle: Float) {
        val binding = binding ?: return
        northAnnouncer.check(binding.root.context, isNearToDegree(0f, angle))
        eastAnnouncer.check(binding.root.context, isNearToDegree(90f, angle))
        southAnnouncer.check(binding.root.context, isNearToDegree(180f, angle))
        westAnnouncer.check(binding.root.context, isNearToDegree(270f, angle))
        val qiblaHeading = binding.compassView.qiblaHeading?.heading?.toFloat()
        if (qiblaHeading != null) {
            qiblaAnnouncer.check(binding.root.context, isNearToDegree(qiblaHeading, angle))
        }
    }

    companion object {
        fun isNearToDegree(compareTo: Float, degree: Float): Boolean {
            val difference = abs(degree - compareTo)
            return if (difference > 180) 360 - difference < 3f else difference < 3f
        }
    }
}
