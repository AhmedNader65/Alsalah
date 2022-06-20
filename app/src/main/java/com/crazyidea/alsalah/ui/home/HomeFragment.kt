package com.crazyidea.alsalah.ui.home

import android.content.Context
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.crazyidea.alsalah.adapter.ArticlesAdapter
import com.crazyidea.alsalah.databinding.FragmentHomeBinding
import com.crazyidea.alsalah.ui.blogDetail.BlogDetailViewModel
import com.crazyidea.alsalah.utils.*
import com.crazyidea.alsalah.workManager.DailyAzanWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

private const val MIN_TIME: Long = 400
private const val MIN_DISTANCE = 1000f

private const val TAG_OUTPUT: String = "DailyAzanWorker"
private const val TAG: String = "HOME FRAGMENT"

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
        getDeviceLocation()
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
        })
        binding.blogItem.adapter = adapter
        binding.khatmaLayout.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToKhatmaFragment())
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

        viewModel.prayers.observe(viewLifecycleOwner) {
            if (it != null) {
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
        _binding = null
    }


    private fun getDeviceLocation() {

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
        globalPreferences.storeLatitude(location.latitude.toString())
        globalPreferences.storeLongitude(location.longitude.toString())
        viewModel.fetchPrayerData(
            location.latitude.toString(),
            location.longitude.toString(),
            globalPreferences.getCalculationMethod(),
            globalPreferences.getSchool(),
            null
        )

        binding.dateLayout.leftArrowIcon.setOnClickListener {
            viewModel.nextDay()
        }

        binding.dateLayout.rightArrowIcon.setOnClickListener {
            viewModel.prevDay()
        }
        locationManager?.removeUpdates(this)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
}