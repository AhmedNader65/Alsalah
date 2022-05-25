package com.crazyidea.alsalah.ui.prayerTiming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.LanguagesAdapter
import com.crazyidea.alsalah.adapter.PrayerTimeAdapter
import com.crazyidea.alsalah.data.model.PrayerTimeRefrence
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentChooseLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrayerTimingFragment : Fragment(), PrayerTimeAdapter.PrayerTimeListner {

    private var _binding: FragmentChooseLanguageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<PrayerTimingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChooseLanguageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text=resources.getString(R.string.editPrayerTime)
        binding.languagetext.text=resources.getString(R.string.calculateWay)
        binding.languagesRV.adapter = PrayerTimeAdapter(createPrayerRefrences(), this)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun createPrayerRefrences(): ArrayList<PrayerTimeRefrence> {
        var prayerRef = ArrayList<PrayerTimeRefrence>()
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.mecca),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.islamicUniversity),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.islamicUniversitySouthAmerica),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.egyptianMessaha),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.worlwideIslamicCommunity),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.spanish),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.turkishResponsibles),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.france),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.russia),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.russia),  false))
        prayerRef.add(PrayerTimeRefrence(resources.getString(R.string.specific),  false))
        return prayerRef
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPrayerTimePicked(prayerTimeRefrence: PrayerTimeRefrence) {

    }
}