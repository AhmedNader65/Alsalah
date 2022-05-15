package com.crazyidea.alsalah.ui.menu.compass

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentQiblaBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CompassFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentQiblaBinding? = null
    private var DegreeStart = 0f
    private lateinit var SensorManage: SensorManager
    private val viewModel by viewModels<CompassViewModel>()

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQiblaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SensorManage = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        if (globalPreferences.longituide.length > 3 && globalPreferences.latituide.length > 3)
            DegreeStart = angleFromCoordinate(
                21.422487, 39.826206,
                globalPreferences.latituide.toDouble(),
                globalPreferences.longituide.toDouble()
            ).toFloat()
        Log.e("DegreeStart", "onViewCreated: " + DegreeStart)
        binding.byVision.setOnClickListener { checkButtons(binding.byVision) }
        binding.bySunAndMoon.setOnClickListener { checkButtons(binding.bySunAndMoon) }
        binding.byCompassBtn.setOnClickListener { checkButtons(binding.byCompassBtn) }
        binding.byCompassBtn.performClick()
    }


    private fun angleFromCoordinate(
        lat1: Double, long1: Double, lat2: Double,
        long2: Double
    ): Double {
        val dLon = long2 - long1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon))
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        brng = 360 - brng // count degrees counter-clockwise - remove to make clockwise
        return brng
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        // to stop the listener and save battery
        SensorManage.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(
            this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        // get angle around the z-axis rotated
        val degree = Math.round(event.values[0]).toFloat()
        binding.compassDirection.setText(
            "اتجاه القبلة من الشمال : " + java.lang.Float.toString(
                degree
            ) + " درجه"
        )
        // rotation animation - reverse turn degree degrees
        val ra = RotateAnimation(
            DegreeStart,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        // set the compass animation after the end of the reservation status
        ra.fillAfter = true
        // set how long the animation for the compass image will take place
        ra.duration = 210
        // Start animation of compass image
        binding.compassImage.startAnimation(ra)
        DegreeStart = -degree
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not in use
    }

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
}