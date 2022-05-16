package com.crazyidea.alsalah.ui.menu.compass

import android.content.Context.SENSOR_SERVICE
import android.hardware.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentQiblaBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


const val KAABA_LAT = 21.422487
const val KAABA_LNG = 39.826206

@AndroidEntryPoint
class CompassFragment : Fragment(), SensorEventListener {

    private var currentDegree = 0f
    private var currentDegreeNeedle = 0f
    private var _binding: FragmentQiblaBinding? = null
    private lateinit var sensorManage: SensorManager
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
        sensorManage = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        binding.byVision.setOnClickListener { checkButtons(binding.byVision) }
        binding.bySunAndMoon.setOnClickListener { checkButtons(binding.bySunAndMoon) }
        binding.byCompassBtn.setOnClickListener { checkButtons(binding.byCompassBtn) }
        binding.byCompassBtn.performClick()
    }


    private fun angleFromCoordinate(
        lat2: Double,
        long2: Double
    ): Double {
        val dLon = long2 - KAABA_LNG
        val y = sin(dLon) * cos(lat2)
        val x = cos(KAABA_LAT) * sin(lat2) - (sin(KAABA_LAT)
                * cos(lat2) * cos(dLon))
        var brng = atan2(y, x)
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
        sensorManage.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // code for system's orientation sensor registered listeners
        sensorManage.registerListener(
            this, sensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        // get angle around the z-axis rotated
        var degree = event.values[0].roundToInt().toFloat()

        val destinationLoc = Location("service Provider")
        val userLoc = Location("service Provider")
        destinationLoc.latitude = 21.422487 //kaaba latitude setting

        destinationLoc.longitude = 39.826206 //kaaba longitude setting

        destinationLoc.latitude =
            globalPreferences.latituide.toDouble() //kaaba latitude setting

        destinationLoc.longitude =
            globalPreferences.longituide.toDouble() //kaaba longitude setting

        var bearTo: Float = userLoc.bearingTo(destinationLoc)


        val geoField = GeomagneticField(
            java.lang.Double.valueOf(userLoc.latitude).toFloat(), java.lang.Double
                .valueOf(userLoc.longitude).toFloat(),
            java.lang.Double.valueOf(userLoc.altitude).toFloat(),
            System.currentTimeMillis()
        )

        degree -= geoField.declination // converts magnetic north into true north


        if (bearTo < 0) {
            bearTo += 360
        }

//This is where we choose to point it
        var direction: Float = bearTo - degree


// If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction += 360
        }

        val raQibla = RotateAnimation(
            currentDegreeNeedle,
            direction,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        raQibla.duration = 210
        raQibla.fillAfter = true

        binding.middleCompass.startAnimation(raQibla)

        currentDegreeNeedle = direction
        if (currentDegreeNeedle < 2 || currentDegreeNeedle > 358) {
            binding.compassIndicator.visibility = VISIBLE
            binding.compassIndicator.strokeColor = getColor(requireContext(),R.color.green)
        } else
            binding.compassIndicator.visibility = GONE
// create a rotation animation (reverse turn degree degrees)
        val ra = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

// how long the animation will take place
        ra.duration = 210;


// set the animation after the end of the reservation status
        ra.fillAfter = true;

// Start the animation
        binding.compassImage.startAnimation(ra);

        currentDegree = -degree;
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