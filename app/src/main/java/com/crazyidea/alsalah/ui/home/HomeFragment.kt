package com.crazyidea.alsalah.ui.home

import android.Manifest
import android.app.AlertDialog
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.IslamicCalendar
import android.icu.util.ULocale
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
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.crazyidea.alsalah.databinding.FragmentHomeBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.PermissionHelper
import com.crazyidea.alsalah.utils.PermissionListener
import com.crazyidea.alsalah.workManager.DailyAzanWorker
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


private const val TAG_OUTPUT: String = "DailyAzanWorker"
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
    var city = ""
    var lat = ""
    var lng = ""

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.dateLayout.model = viewModel
        binding.lifecycleOwner = this
        permissionHelper = PermissionHelper(this, this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.khatmaLayout.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToKhatmaFragment())
        }

        permissionHelper.checkForMultiplePermissions(
            arrayOf(
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
        val iCalendar = UmmalquraCalendar()
// name of month
        val locale = ULocale("ar@calendar=islamic")

// name of month
// full date
//        val df2 = SimpleDateFormat("MMMM yyyy", locale)
//        val df1 = SimpleDateFormat("EEEE", locale)
//        binding.dateLayout.dayNum.text = iCalendar.get(IslamicCalendar.DAY_OF_MONTH).toString()
//        binding.dateLayout.monthYear.text = df2.format(iCalendar.time)
//        binding.dateLayout.today.text = df1.format(iCalendar.time)
        setupNavigation()
        collectData()
    }

    private fun setupNavigation() {
        binding.readAfterPrayersNowBtn.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationAzkarDetails(
                    "أذكار بعد السلام من الصلاة المفروضة"
                )
            )
        }
        binding.readAzkarBtn.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationAzkarDetails(
                    "اخرى"
                )
            )
        }
        binding.qeblaLayout.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToCompassFragment2()
            )
        }
        binding.dateLayout.calendarIcon.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToCalendarFragment()
            )
        }
    }

    private fun collectData() {
        viewModel.prayerData.observe(viewLifecycleOwner) {
            val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyAzanWorker>()
//            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(TAG_OUTPUT).build()
            WorkManager.getInstance(requireContext()).enqueueUniqueWork(
                TAG_OUTPUT,
                ExistingWorkPolicy.KEEP, dailyWorkRequest
            )
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

                        globalPreferences.storeLatituide(it.latitude.toString())
                        globalPreferences.storeLongituide(it.longitude.toString())
                        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
                        val cityName: String = addresses[0].locality
                        viewModel.fetchPrayerData(
                            cityName,
                            calendar,
                            it.latitude.toString(),
                            it.longitude.toString(),
                            5,
                            null
                        )


                        binding.dateLayout.leftArrowIcon.setOnClickListener { ttt ->
                            viewModel.nextDay()
                            viewModel.fetchPrayerData(
                                cityName,
                                viewModel.gor,
                                it.latitude.toString(),
                                it.longitude.toString(),
                                5,
                                null
                            )

                        }

                        binding.dateLayout.rightArrowIcon.setOnClickListener { ttt ->
                            viewModel.prevDay()
                            viewModel.fetchPrayerData(
                                cityName,
                                viewModel.gor,
                                it.latitude.toString(),
                                it.longitude.toString(),
                                5,
                                null
                            )

                        }
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