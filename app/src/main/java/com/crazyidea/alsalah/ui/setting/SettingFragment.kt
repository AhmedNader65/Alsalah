package com.crazyidea.alsalah.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<SettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languageCon.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLanguageFragment())
        }
        binding.calculateWay.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToPrayerTimingFragment())
        }
        binding.almazhab.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToMazhabFragment())
        }
        binding.editPrayerTime.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToRefactorPrayerTimingFragment())
        }
        binding.poleCalculateWay.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToPoleCalculateWayFragment())
        }
        binding.defaultSettingPrayer.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToResetFragment())
        }
        binding.getLocaAutomatically.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToLocationFragment())
        }
        binding.azkar.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToAzkarSettingFragment())
        }
        binding.fwaed.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToFawaedFragment())
        }
        binding.azanSoundAndImage.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToAzanSettingFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}