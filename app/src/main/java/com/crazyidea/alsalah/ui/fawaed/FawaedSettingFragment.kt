package com.crazyidea.alsalah.ui.fawaed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.LanguagesAdapter
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentFawaedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FawaedSettingFragment : Fragment(), LanguagesAdapter.LanguagListner {

    private var _binding: FragmentFawaedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<FawaedSettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFawaedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languagesRV.adapter = LanguagesAdapter(createLanguages(), this)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun createLanguages(): ArrayList<SupportedLanguage> {
        var languages = ArrayList<SupportedLanguage>()
        languages.add(SupportedLanguage(resources.getString(R.string.arabic), "ar", false))
        languages.add(SupportedLanguage(resources.getString(R.string.english), "en", false))
        languages.add(SupportedLanguage(resources.getString(R.string.alll), "all", false))
        return languages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onlangPicked(language: SupportedLanguage) {

    }
}