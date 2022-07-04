package com.crazyidea.alsalah.ui.home

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.crazyidea.alsalah.*
import com.crazyidea.alsalah.adapter.ArticlesAdapter
import com.crazyidea.alsalah.databinding.FragmentHomeBinding
import com.crazyidea.alsalah.ui.blogDetail.BlogDetailViewModel
import com.crazyidea.alsalah.ui.khatma.KhatmaFragmentDirections
import com.crazyidea.alsalah.utils.*
import com.crazyidea.alsalah.workManager.DailyAzanWorker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val MIN_TIME: Long = 400
private const val MIN_DISTANCE = 1000f

private const val TAG_OUTPUT: String = "DailyAzanWorker"
private const val TAG: String = "HOME FRAGMENT"

private const val LOCATION_PERMISSION_INDEX = 0
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 321
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 35

@AndroidEntryPoint
class HomeFragment : Fragment(), LocationListener {

    private lateinit var adapter: ArticlesAdapter
    private var _binding: FragmentHomeBinding? = null


    private val viewModel by viewModels<HomeViewModel>()
    private val blogViewModel by viewModels<BlogDetailViewModel>()
    private var locationManager: LocationManager? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()
        adapter = ArticlesAdapter(arrayListOf(), onReadMore = {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToBlogDetailFragment(
                    it
                )
            )
        }, onFavourite = {
            blogViewModel.postArticleLike(it.id)
        }, onShare = {
            it.share(requireContext())
            blogViewModel.postShareArticle(it.id)
        }, isLoggedIn = globalPreferences.getLogged())
        binding.blogItem.adapter = adapter
        binding.dateLayout.leftArrowIcon.setOnClickListener {
            viewModel.nextDay()
        }

        binding.dateLayout.rightArrowIcon.setOnClickListener {
            viewModel.prevDay()
        }
        binding.khatmaLayout.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToKhatmaFragment())
        }
        binding.startKhatma.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToKhatmaFragment())
        }
        binding.continueReading.setOnClickListener {

            viewModel.khatma.value?.let { it1 ->
                findNavController().navigate(
                    HomeFragmentDirections.actionNavigationHomeToQuranFragment(
                        it1, "khatma"
                    )
                )
            }
        }
        viewModel.nextPrayerId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(viewModel.getStatusBarColor(it, requireContext()))
            }
        })

        setupArticles()
        setupNavigation()
        collectData()
    }

    private fun checkPermissions() {
        if (foregroundLocationPermissionApproved()) {
            Timber.d("foregroundLocationPermiss ionApproved")
            checkDeviceLocationSettingsAndStartApp()
        } else {
            Timber.d("foregroundLocationPermissionNotApproved")
            requestForegroundLocationPermission()
        }
    }


    private fun foregroundLocationPermissionApproved(): Boolean {

        return (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
    }

    private fun requestForegroundLocationPermission() {
        if (foregroundLocationPermissionApproved())
            return
        val permissionsArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val resultCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        Timber.e("result code = $resultCode")
        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Timber.d("onRequestPermissionResult")
        if (requestCode == REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE) {
            if (
                grantResults.isNotEmpty() &&
                grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
            ) {
                Snackbar.make(
                    binding.root,
                    R.string.locationPermission,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.setting) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            } else if (grantResults.isNotEmpty()) {
                checkDeviceLocationSettingsAndStartApp()
            }
        }
    }

    private fun checkDeviceLocationSettingsAndStartApp(resolve: Boolean = true) {
        Timber.d("checkDeviceLocationSettingsAndStartApp")
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                Timber.d("startIntentSenderForResult")
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON,
                        null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.e("Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartApp()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            Timber.d("addOnCompleteListener")
            if (it.isSuccessful) {
                Timber.d("isSuccessful")
                getDeviceLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            getDeviceLocation()
        }
    }

    private fun setupArticles() {
        viewModel.articleData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
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
        viewModel.khatma.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.latestKhatmaLayout.visibility = VISIBLE
                viewModel.getKhatmaAya(it.read)
            } else
                binding.latestKhatmaLayout.visibility = GONE
        }
        blogViewModel.toastLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                blogViewModel.toastLiveData.messageShown()
            }
        }
        viewModel.prayers.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getNextPrayer()
                Log.e("Work manager", "started")
                val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyAzanWorker>()
//            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .addTag(TAG_OUTPUT).build()
                WorkManager.getInstance(requireContext()).enqueue(dailyWorkRequest)

            }
//            WorkManager.getInstance(requireContext()).enqueueUniqueWork(
//                TAG_OUTPUT,
//                ExistingWorkPolicy.REPLACE, dailyWorkRequest
//            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager?.removeUpdates(this)
        _binding = null
    }


    private fun getDeviceLocation() {
        Timber.d("getDeviceLocation")

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        Timber.d("onLocationChanged")
        globalPreferences.storeLatitude(location.latitude.toString())
        globalPreferences.storeLongitude(location.longitude.toString())
        viewModel.fetchPrayerData(
            location.latitude.toString(),
            location.longitude.toString(),
            globalPreferences.getCalculationMethod(),
            globalPreferences.getSchool(),
            null
        )

        locationManager?.removeUpdates(this)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
}