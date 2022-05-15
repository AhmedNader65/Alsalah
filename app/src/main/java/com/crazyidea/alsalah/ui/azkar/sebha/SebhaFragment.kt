package com.crazyidea.alsalah.ui.azkar.sebha

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentAzkarDetailsBinding
import com.crazyidea.alsalah.databinding.FragmentSebhaBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SebhaFragment : Fragment() {

    private var _binding: FragmentSebhaBinding? = null
    private val viewModel by viewModels<SebhaViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val fonts =
        intArrayOf(
            com.intuit.ssp.R.dimen._13ssp,
            com.intuit.ssp.R.dimen._15ssp,
            com.intuit.ssp.R.dimen._17ssp,
            com.intuit.ssp.R.dimen._19ssp,
            com.intuit.ssp.R.dimen._21ssp,
            com.intuit.ssp.R.dimen._23ssp,
        )

    var currentFontIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSebhaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getSebha()

        }
        binding.bottomTools.fontSize.setOnClickListener {
            currentFontIndex++
            binding.azkarTv.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(fonts[currentFontIndex % fonts.size])
            )
        }
        binding.bottomTools.nightMode.setOnClickListener {
            val nightModeEnabled = AppCompatDelegate.getDefaultNightMode()
            if (nightModeEnabled == AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.sebhaImg.isVisible = tab.position == 0
                binding.counterContainer.isVisible = tab.position == 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}