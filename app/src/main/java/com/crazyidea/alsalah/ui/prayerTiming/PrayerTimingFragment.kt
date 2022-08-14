package com.crazyidea.alsalah.ui.prayerTiming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.PrayerTimeAdapter
import com.crazyidea.alsalah.data.model.PrayersCalculationMethods
import com.crazyidea.alsalah.databinding.FragmentChooseLanguageBinding
import com.crazyidea.alsalah.ui.setting.SalahSettings
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PrayerTimingFragment : Fragment() {

    private var calculationMethod: Int = 0
    private var _binding: FragmentChooseLanguageBinding? = null

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    val viewModel by viewModels<PrayerTimingViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChooseLanguageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = resources.getString(R.string.editPrayerTime)
        binding.languagetext.text = resources.getString(R.string.calculateWay)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.fetchIntegerData(SalahSettings.CALCULATION_METHOD).collect {
                    calculationMethod = it

                    binding.languagesRV.adapter = PrayerTimeAdapter(createPrayerRefrences()) {
                        viewModel.update(SalahSettings.CALCULATION_METHOD, it)
                    }
                }
            }
        }
    }

    private fun createPrayerRefrences(): ArrayList<PrayersCalculationMethods> {
        val prayerRef = ArrayList<PrayersCalculationMethods>()
        prayerRef.add(
            PrayersCalculationMethods(
                5,
                resources.getString(R.string.egyptianMessaha),
                false
            )
        )
        prayerRef.add(PrayersCalculationMethods(4, resources.getString(R.string.mecca), false))
        prayerRef.add(
            PrayersCalculationMethods(
                1,
                resources.getString(R.string.islamicUniversity),
                false
            )
        )
        prayerRef.add(
            PrayersCalculationMethods(
                2,
                resources.getString(R.string.islamicUniversitySouthAmerica),
                false
            )
        )
        prayerRef.add(
            PrayersCalculationMethods(
                3,
                resources.getString(R.string.worlwideIslamicCommunity),
                false
            )
        )
        prayerRef.add(
            PrayersCalculationMethods(
                13,
                resources.getString(R.string.turkishResponsibles),
                false
            )
        )
        prayerRef.add(PrayersCalculationMethods(12, resources.getString(R.string.france), false))
        prayerRef.add(PrayersCalculationMethods(14, resources.getString(R.string.russia), false))
        prayerRef.add(PrayersCalculationMethods(8, resources.getString(R.string.gulf), false))
        prayerRef.add(PrayersCalculationMethods(9, resources.getString(R.string.kuwait), false))
        prayerRef.add(PrayersCalculationMethods(10, resources.getString(R.string.qatar), false))
        prayerRef.add(PrayersCalculationMethods(11, resources.getString(R.string.singapore), false))
//        prayerRef.add(PrayersCalculationMethods(resources.getString(R.string.specific),  false))
        prayerRef.find { it.id == calculationMethod }?.checked = true
        return prayerRef
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}