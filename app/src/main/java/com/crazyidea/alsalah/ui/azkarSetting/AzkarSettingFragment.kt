package com.crazyidea.alsalah.ui.azkarSetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.AzkarLanguagesRadioAdapter
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentAzkarSettingBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AzkarSettingFragment : Fragment(), AzkarLanguagesRadioAdapter.LanguagListner {

    private var _binding: FragmentAzkarSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<AzkarSettingViewModel>()
    var status = true

    @Inject
    lateinit var globalPreferences: GlobalPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzkarSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languagesRV.adapter = AzkarLanguagesRadioAdapter(createLanguages(), this)
        binding.upDownImg.setOnClickListener {
            status = !status
            checkRecyclerView()
        }


    }

    private fun checkRecyclerView() {
        if (status) {
            binding.languagesRV.visibility = View.VISIBLE
            binding.upDownImg.rotation = 0f
        } else {
            binding.languagesRV.visibility = View.GONE
            binding.upDownImg.rotation = 180f

        }

    }

    private fun createLanguages(): ArrayList<SupportedLanguage> {
        var languages = ArrayList<SupportedLanguage>()
        languages.add(SupportedLanguage(resources.getString(R.string.arabic), "ar", false))
        languages.add(SupportedLanguage(resources.getString(R.string.english), "en", false))
        languages.add(SupportedLanguage(resources.getString(R.string.frecnh), "fr", false))
        languages.add(SupportedLanguage(resources.getString(R.string.turkish), "tr", false))
        languages.add(SupportedLanguage(resources.getString(R.string.dutch), "du", false))
        languages.add(SupportedLanguage(resources.getString(R.string.spanish), "sp", false))
        languages.add(SupportedLanguage(resources.getString(R.string.indonisian), "in", false))
        languages.add(SupportedLanguage(resources.getString(R.string.urdu), "ur", false))
        languages.add(SupportedLanguage(resources.getString(R.string.igorish), "ig", false))
        return languages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onlangPicked(language: SupportedLanguage) {
        globalPreferences.storeAzkarLanguage(language.shortcut)

    }
}