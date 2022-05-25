package com.crazyidea.alsalah.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.PrimaryColor
import com.crazyidea.alsalah.databinding.FragmentProfileBinding
import com.crazyidea.alsalah.ui.home.HomeViewModel
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel by viewModels<ProfileViewModel>()

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSelectedSecondaryColor(globalPreferences.primaryColor)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            binding.nightModeSwitch.isChecked = true else false
        binding.blueColor.setOnClickListener {
            globalPreferences.storePrimaryColor(PrimaryColor.BLUE)
            setSelectedSecondaryColor(PrimaryColor.BLUE)
            (requireActivity() as MainActivity).restartActivity()
        }
        binding.orangeColor.setOnClickListener {
            globalPreferences.storePrimaryColor(PrimaryColor.ORANGE)
            setSelectedSecondaryColor(PrimaryColor.ORANGE)
            (requireActivity() as MainActivity).restartActivity()
        }
        binding.pinkColor.setOnClickListener {
            globalPreferences.storePrimaryColor(PrimaryColor.PINK)
            setSelectedSecondaryColor(PrimaryColor.PINK)
            (requireActivity() as MainActivity).restartActivity()
        }

        binding.nightModeSwitch.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSelectedSecondaryColor(primaryColor: PrimaryColor) {
        when (primaryColor) {
            PrimaryColor.ORANGE -> {
                binding.selectedBlue.visibility = View.GONE
                binding.selectedPink.visibility = View.GONE
                binding.selectedOrange.visibility = View.VISIBLE
            }
            PrimaryColor.BLUE -> {
                binding.selectedBlue.visibility = View.VISIBLE
                binding.selectedPink.visibility = View.GONE
                binding.selectedOrange.visibility = View.GONE
            }
            PrimaryColor.PINK -> {
                binding.selectedBlue.visibility = View.GONE
                binding.selectedPink.visibility = View.VISIBLE
                binding.selectedOrange.visibility = View.GONE
            }
        }
    }
}