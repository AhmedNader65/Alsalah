package com.crazyidea.alsalah.ui.fawaed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.DataStoreCollector
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.LanguagesAdapter
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentFawaedBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FawaedSettingFragment : Fragment(), LanguagesAdapter.LanguagListner {

    private var _binding: FragmentFawaedBinding? = null

    @Inject
    lateinit var globalPreferences: GlobalPreferences

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
        binding.notificationsSwitch.isChecked = DataStoreCollector.showArticlesNotifications
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveNotificationPref(isChecked)
        }
    }

    private fun createLanguages(): ArrayList<SupportedLanguage> {
        val languages = ArrayList<SupportedLanguage>()
        languages.add(
            SupportedLanguage(
                resources.getString(R.string.arabic),
                "ar",
                DataStoreCollector.articlesLanguage
            )
        )
        languages.add(
            SupportedLanguage(
                resources.getString(R.string.english),
                "en",
                DataStoreCollector.articlesLanguage
            )
        )
        languages.add(
            SupportedLanguage(
                resources.getString(R.string.alll),
                "all",
                DataStoreCollector.articlesLanguage
            )
        )
        return languages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onlangPicked(language: SupportedLanguage) {

        Log.e("TAG", "saving: ${language.shortcut}")
        viewModel.saveLanguage(language.shortcut)
    }
}