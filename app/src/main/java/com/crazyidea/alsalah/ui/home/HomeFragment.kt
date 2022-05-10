package com.crazyidea.alsalah.ui.home

import android.Manifest
import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crazyidea.alsalah.data.model.Status
import com.crazyidea.alsalah.databinding.FragmentHomeBinding
import com.crazyidea.alsalah.utils.PermissionHelper
import com.crazyidea.alsalah.utils.PermissionListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


private const val TAG: String = "HOME FRAGMENT"

@AndroidEntryPoint
class HomeFragment : Fragment(), PermissionListener {

    private lateinit var permissionHelper: PermissionHelper
    private var _binding: FragmentHomeBinding? = null

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = this
        permissionHelper = PermissionHelper(this, this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        permissionHelper.launchPermissionDialogForMultiplePermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        collectData()
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.prayerData.collect { it ->
                    when (it.status) {
                        Status.SUCCESS -> {
                        }
                        Status.LOADING -> {

                        }
                        Status.ERROR -> {

                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun shouldShowRationaleInfo() {

        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage("Location permission is Required")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
                permissionHelper.launchPermissionDialogForMultiplePermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            // negative button text and action
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Location Permission")
        // show alert dialog
        alert.show()

    }

    override fun isPermissionGranted(isGranted: Boolean) {
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    lastKnownLocation?.let {
                        val geocoder = Geocoder(requireContext(), Locale("en"))
                        val addresses: List<Address> = geocoder.getFromLocation(
                            it.latitude,
                            it.longitude,
                            1
                        )
                        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
                        val cityName: String = addresses[0].locality
                        viewModel.fetchPrayerData(
                            cityName,
                            calendar.get(Calendar.DAY_OF_MONTH),
                            (calendar.get(Calendar.MONTH) + 1).toString(),
                            calendar.get(Calendar.YEAR).toString(),
                            it.latitude.toString(),
                            it.longitude.toString(),
                            5,
                            null
                        )
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)

                }
            }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}