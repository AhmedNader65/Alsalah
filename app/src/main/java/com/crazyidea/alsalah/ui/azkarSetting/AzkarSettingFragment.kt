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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
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
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.afterPrayerAzkarSwitch.isChecked = globalPreferences.isAfterPrayerNotification()
        binding.morningAzkarSwitch.isChecked = globalPreferences.isMorningNotification()
        binding.nightAzkarSwitch.isChecked = globalPreferences.isEveningNotification()
        binding.sleepAzkarSwitch.isChecked = globalPreferences.isSleepingNotification()
        binding.afterPrayerAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            globalPreferences.storeAfterPrayerNotification(isChecked)
        }
        binding.morningAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            globalPreferences.storeMorningNotification(isChecked)
        }
        binding.nightAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            globalPreferences.storeEveningNotification(isChecked)
        }
        binding.sleepAzkarSwitch.setOnCheckedChangeListener { _, isChecked ->
            globalPreferences.storeSleepingNotification(isChecked)
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
                globalPreferences.storeSleepingTime(calendar.timeInMillis)
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
        formatter = SimpleDateFormat("h:mm a", Locale(globalPreferences.getLocale()))
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