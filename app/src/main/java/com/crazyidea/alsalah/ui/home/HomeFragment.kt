package com.crazyidea.alsalah.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.crazyidea.alsalah.*
import com.crazyidea.alsalah.adapter.ArticlesAdapter
import com.crazyidea.alsalah.databinding.FragmentHomeBinding
import com.crazyidea.alsalah.ui.blogDetail.BlogDetailViewModel
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.SalahSettings
import com.crazyidea.alsalah.utils.*
import com.crazyidea.alsalah.workManager.DailyAzanWorker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

private const val MIN_TIME: Long = 400
private const val MIN_DISTANCE = 1000f

private const val TAG_OUTPUT: String = "DailyAzanWorker"
private const val TAG: String = "HOME FRAGMENT"

private const val LOCATION_PERMISSION_INDEX = 0
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 321
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 35

@AndroidEntryPoint
class HomeFragment : Fragment(), LocationListener {

    private var calculationMethod: Int = 0
    private var school: Int = 0
    private var poleCalc: Int = 0
    private var fajrMargin: Int = 0
    private var shorokMargin: Int = 0
    private var dhuhrMargin: Int = 0
    private var asrMargin: Int = 0
    private var maghribMargin: Int = 0
    private var ishaMargin: Int = 0

    private lateinit var adapter: ArticlesAdapter
    private var _binding: FragmentHomeBinding? = null


    private val viewModel by viewModels<HomeViewModel>()
    private val blogViewModel by viewModels<BlogDetailViewModel>()
    private var locationManager: LocationManager? = null

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
        binding.dateLayout.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)){
                showDOARationaleInfo()
            }

        }
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
        }, isLoggedIn = DataStoreCollector.loggedIn)
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
                window.statusBarColor = viewModel.getStatusBarColor(it, requireContext())
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
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchData().collect {
                    calculationMethod = it[SalahSettings.CALCULATION_METHOD] ?: 0
                    school = it[SalahSettings.SCHOOL] ?: 0
                    poleCalc = it[SalahSettings.POLE_CALCULATION] ?: 0
                    fajrMargin = it[SalahSettings.FAJR_MARGIN] ?: 0
                    shorokMargin = it[SalahSettings.SHOROK_MARGIN] ?: 0
                    dhuhrMargin = it[SalahSettings.DHUHR_MARGIN] ?: 0
                    asrMargin = it[SalahSettings.ASR_MARGIN] ?: 0
                    maghribMargin = it[SalahSettings.MAGHRIB_MARGIN] ?: 0
                    ishaMargin = it[SalahSettings.ISHA_MARGIN] ?: 0
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager?.removeUpdates(this)
        _binding = null
    }

    fun showDOARationaleInfo() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setView(
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_alert, null)
        )
            // if the dialog is cancelable
            .setCancelable(true)

        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
        val settingsBtn = alert.findViewById<Button>(R.id.action_btn)
        val closeBtn = alert.findViewById<ImageView>(R.id.close)
        val msg = alert.findViewById<TextView>(R.id.msg)
        msg.text = getString(R.string.display_over_apps_azan)
        closeBtn.setOnClickListener {
            alert.cancel()
        }
        settingsBtn.setOnClickListener {
            alert.cancel()
            requestOverlayPermission()
        }

    }

    private fun requestOverlayPermission() {

        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireActivity().packageName)
        )
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//        }
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
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        Timber.d("onLocationChanged")
        viewModel.update(AppSettings.LATITUDE,location.latitude)
        viewModel.update(AppSettings.LONGITUDE,location.longitude)
        viewModel.fetchPrayerData(
            location.latitude.toString(),
            location.longitude.toString(),
            calculationMethod,
            school,
            "0,$fajrMargin,$shorokMargin,$dhuhrMargin,$asrMargin,$maghribMargin,$ishaMargin,0",
            poleCalc
        )

        locationManager?.removeUpdates(this)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
}