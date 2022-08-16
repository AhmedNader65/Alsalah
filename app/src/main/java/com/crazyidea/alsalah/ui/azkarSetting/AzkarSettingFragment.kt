package com.crazyidea.alsalah.ui.azkarSetting

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
import com.crazyidea.alsalah.adapter.AzkarLanguagesRadioAdapter
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentAzkarSettingBinding
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.AzanSettings
import com.crazyidea.alsalah.ui.setting.AzkarSettings

import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AzkarSettingFragment : Fragment(), AzkarLanguagesRadioAdapter.LanguagListner {

    private var _binding: FragmentAzkarSettingBinding? = null

    private var hour: Int = 10
    private var minute: Int = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<AzkarSettingViewModel>()
    var status = true

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzkarSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    var language: String = "ar"
    var notifySleeping: Boolean = true
    var notifyEvening: Boolean = true
    var notifyMorning: Boolean = true
    var notifyAfterPrayer: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = AzkarLanguagesRadioAdapter(createLanguages(), this)
        binding.languagesRV.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchData()
                    .collect { preferences ->
                        notifySleeping = preferences[AzkarSettings.SLEEPING_AZKAR] ?: true
                        notifyEvening = preferences[AzkarSettings.EVENING_AZKAR] ?: true
                        notifyMorning = preferences[AzkarSettings.MORNING_AZKAR] ?: true
                        notifyAfterPrayer = preferences[AzkarSettings.AFTER_PRAYER_AZKAR] ?: true
                        language = preferences[AzkarSettings.LANGUAGE] ?: "ar"
                        adapter.updateSelectedLanguage(language)

                        binding.afterPrayerAzkarSwitch.isChecked = notifyAfterPrayer
                        binding.morningAzkarSwitch.isChecked = notifyMorning
                        binding.nightAzkarSwitch.isChecked = notifyEvening
                        binding.sleepAzkarSwitch.isChecked = notifySleeping
                    }
            }
        }
        binding.upDownImg.setOnClickListener {
            status = !status
            checkRecyclerView()
        }
        viewLifecycleOwner.lifecycleScope
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.afterPrayerAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.update(AzkarSettings.AFTER_PRAYER_AZKAR, isChecked)
        }
        binding.morningAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.update(AzkarSettings.MORNING_AZKAR, isChecked)
        }
        binding.nightAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.update(AzkarSettings.EVENING_AZKAR, isChecked)
        }
        binding.sleepAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.update(AzkarSettings.SLEEPING_AZKAR, isChecked)
        }
        binding.notificationTimeText.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(hour)
                    .setMinute(minute)
                    .setTitleText(resources.getString(R.string.notification_time))
                    .build()

            picker.show(childFragmentManager, "tag")
            picker.addOnPositiveButtonClickListener {
                this.hour = picker.hour
                this.minute = picker.minute
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                viewModel.update(AzkarSettings.SLEEPING_AZKAR_TIME, calendar.timeInMillis)
                binding.notificationTimeText.text = getTime(picker.hour, picker.minute)
            }
            picker.addOnNegativeButtonClickListener {
                // call back code
            }
            picker.addOnCancelListener {
                // call back code
            }
            picker.addOnDismissListener {
                // call back code
            }
        }

    }

    private fun getTime(hr: Int, min: Int): String? {
        val tme = Time(hr, min, 0) //seconds by default set to zero
        val formatter: Format
        formatter = SimpleDateFormat("h:mm a", Locale(language))
        return formatter.format(tme)
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
        val languages = ArrayList<SupportedLanguage>()
        languages.add(SupportedLanguage(resources.getString(R.string.arabic), "ar"))
        languages.add(SupportedLanguage(resources.getString(R.string.english), "en"))
        languages.add(SupportedLanguage(resources.getString(R.string.frecnh), "fr"))
        languages.add(SupportedLanguage(resources.getString(R.string.turkish), "tr"))
        languages.add(SupportedLanguage(resources.getString(R.string.dutch), "du"))
        languages.add(SupportedLanguage(resources.getString(R.string.spanish), "sp"))
        languages.add(SupportedLanguage(resources.getString(R.string.indonisian), "in"))
        languages.add(SupportedLanguage(resources.getString(R.string.urdu), "ur"))
        languages.add(SupportedLanguage(resources.getString(R.string.igorish), "ig"))
        return languages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onlangPicked(language: SupportedLanguage) {
        viewModel.update(AzkarSettings.LANGUAGE, language.shortcut)

    }
}