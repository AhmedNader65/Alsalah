package com.crazyidea.alsalah.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.LanguagesAdapter
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentChooseLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : Fragment(), LanguagesAdapter.LanguagListner {

    private var _binding: FragmentChooseLanguageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<LanguageViewModel>()

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
        binding.languagesRV.adapter = LanguagesAdapter(createLanguages(), this)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
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

    }
}