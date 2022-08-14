package com.crazyidea.alsalah.ui.poleCalculateWay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.PoleCalculation
import com.crazyidea.alsalah.databinding.FragmentChoosePoleCalculateWayBinding
import com.crazyidea.alsalah.ui.setting.SalahSettings
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PoleCalculateWayFragment : Fragment() {

    private var _binding: FragmentChoosePoleCalculateWayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<PoleCalculateWayViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChoosePoleCalculateWayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchIntegerData(SalahSettings.POLE_CALCULATION).collect { pole ->
                    PoleCalculation.values().find {
                        it.name == when (pole) {
                            3 -> PoleCalculation.ANGLE.toString()
                            2 -> PoleCalculation.ONEONSEVEN.toString()
                            1 -> PoleCalculation.MIDNIGHT.toString()
                            else -> PoleCalculation.NOTHING.toString()
                        }
                    }
                        ?.let { checkMazhab(it) }
                }
            }
        }

        binding.nothingCon.setOnClickListener { checkMazhab(PoleCalculation.NOTHING) }
        binding.angelarCon.setOnClickListener { checkMazhab(PoleCalculation.ANGLE) }
        binding.midnightCon.setOnClickListener { checkMazhab(PoleCalculation.MIDNIGHT) }
        binding.oneOfSevenCon.setOnClickListener { checkMazhab(PoleCalculation.ONEONSEVEN) }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }


    fun checkMazhab(poleCalculation: PoleCalculation) {
        clearPoles()
        var selectedPole = 3
        when (poleCalculation) {
            PoleCalculation.ANGLE -> {
                selectedPole = 3
                setColor(binding.angelarImg, binding.angelarSelected)
            }
            PoleCalculation.ONEONSEVEN -> {
                selectedPole = 2
                setColor(
                    binding.oneOfSevenImg,
                    binding.oneOfSevenSelected
                )
            }
            PoleCalculation.NOTHING -> setColor(binding.nothingImg, binding.nothingImgSelected)
            PoleCalculation.MIDNIGHT -> {
                setColor(binding.midnightImg, binding.midnightImgSelected)

                selectedPole = 1
            }
        }
        viewModel.update(SalahSettings.POLE_CALCULATION, selectedPole)

    }

    fun setColor(imageView: ImageView, selectedIMG: ImageView) {
        imageView.setColorFilter(requireContext().themeColor(android.R.attr.colorPrimary))
        selectedIMG.visibility = View.VISIBLE
    }

    private fun clearPoles() {
        binding.angelarImg.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.midnightImg.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.nothingImg.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.oneOfSevenImg.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )

        binding.angelarSelected.visibility = View.GONE
        binding.midnightImgSelected.visibility = View.GONE
        binding.nothingImgSelected.visibility = View.GONE
        binding.oneOfSevenSelected.visibility = View.GONE


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}