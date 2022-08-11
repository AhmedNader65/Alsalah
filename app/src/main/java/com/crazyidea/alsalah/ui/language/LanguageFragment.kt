package com.crazyidea.alsalah.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.LanguagesAdapter
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentChooseLanguageBinding
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.SettingViewModel
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : Fragment(), LanguagesAdapter.LanguagListner {

    private var _binding: FragmentChooseLanguageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SettingViewModel>()

    @Inject
    lateinit var globalPreferences: GlobalPreferences
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
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.getLanguage().collect {
                    binding.languagesRV.adapter = LanguagesAdapter(createLanguages(it), this@LanguageFragment)
                }
            }
        }
    }

    private fun createLanguages(selectedLanguage:String): ArrayList<SupportedLanguage> {
        val languages = ArrayList<SupportedLanguage>()
        languages.add(SupportedLanguage(resources.getString(R.string.arabic), "ar",selectedLanguage=="ar"))
        languages.add(SupportedLanguage(resources.getString(R.string.english), "en",selectedLanguage=="en"))
        languages.add(SupportedLanguage(resources.getString(R.string.frecnh), "fr",selectedLanguage=="fr"))
        languages.add(SupportedLanguage(resources.getString(R.string.turkish), "tr",selectedLanguage=="tr"))
        languages.add(SupportedLanguage(resources.getString(R.string.dutch), "du",selectedLanguage=="du"))
        languages.add(SupportedLanguage(resources.getString(R.string.spanish), "sp",selectedLanguage=="sp"))
        languages.add(SupportedLanguage(resources.getString(R.string.indonisian), "in",selectedLanguage=="in"))
        languages.add(SupportedLanguage(resources.getString(R.string.urdu), "ur",selectedLanguage=="ur"))
        languages.add(SupportedLanguage(resources.getString(R.string.igorish), "ig",selectedLanguage=="ig"))
        return languages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onlangPicked(language: SupportedLanguage) {
        viewModel.update(AppSettings.APP_LANGUAGE, language.shortcut)
//        globalPreferences.storeLocale(language.shortcut)
        (activity as MainActivity).restartActivity()
    }
}