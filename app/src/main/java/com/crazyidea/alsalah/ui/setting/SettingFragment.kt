package com.crazyidea.alsalah.ui.setting

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<SettingViewModel>()
    lateinit var batteryOptIntent: Intent
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

        binding.back.setOnClickListener { requireActivity().onBackPressed() }

        binding.backgroundPermission.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showRequestBatteryOptDialog()
                batteryOptIntent = Intent()
                val packageName = requireActivity().packageName

                batteryOptIntent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                batteryOptIntent.data = Uri.parse("package:$packageName")
                val pm =
                    requireActivity().getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    showRequestBatteryOptDialog()
                }
            }
        }
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

    private fun showRequestBatteryOptDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setView(
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_alert, null)
        )
            // if the dialog is cancelable
            .setCancelable(true)

        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
        val settingsBtn = alert.findViewById<Button>(R.id.action_btn)
        val closeBtn = alert.findViewById<ImageView>(R.id.close)
        closeBtn.setOnClickListener {
            alert.cancel()
        }
        settingsBtn.setOnClickListener {
            startActivity(batteryOptIntent)
            alert.cancel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}