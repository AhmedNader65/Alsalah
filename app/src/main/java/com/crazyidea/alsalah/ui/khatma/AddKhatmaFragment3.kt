package com.crazyidea.alsalah.ui.khatma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentAddKhatma3Binding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.setAlarm
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddKhatmaFragment3 : Fragment() {

    private var hour: Int = 12
    private var minute: Int = 10
    private var _binding: FragmentAddKhatma3Binding? = null

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKhatma3Binding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notificationTime.setOnClickListener({ openTimer() })
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.done.setOnClickListener {
            val time = Time(hour, minute, 0)
            viewModel.khatma.value?.time = time
            viewModel.khatma.value?.notify = binding.notificationSwitch.isChecked
            viewModel.khatma.value = viewModel.khatma.value
            viewModel.saveKhatma()
            viewModel.khatma.value?.name?.let { it1 ->
                setAlarm(
                    requireContext(),
                    "khatma",
                    it1,
                    time.time
                )
            }
            findNavController().navigate(AddKhatmaFragment3Directions.actionAddKhatmaFragment3ToKhatmaFragment())
            Toast.makeText(requireContext(), getString(R.string.saved_khatma), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openTimer() {
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

    private fun getTime(hr: Int, min: Int): String? {
        val tme = Time(hr, min, 0) //seconds by default set to zero
        val formatter: Format
        formatter = SimpleDateFormat("h:mm a", Locale(globalPreferences.getLocale()))
        return formatter.format(tme)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}