package com.crazyidea.alsalah.ui.poleCalculateWay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.PoleCalculation
import com.crazyidea.alsalah.databinding.FragmentChoosePoleCalculateWayBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoleCalculateWayFragment : Fragment() {

    private var _binding: FragmentChoosePoleCalculateWayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<PoleCalcculateWayViewModel>()
    lateinit var globalPreferences: GlobalPreferences

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
        globalPreferences = GlobalPreferences(requireContext())
        PoleCalculation.values().find { it.name == globalPreferences.pole }
            ?.let { checkMazhab(it) }
        binding.nothingCon.setOnClickListener { checkMazhab(PoleCalculation.NOTHING) }
        binding.angelarCon.setOnClickListener { checkMazhab(PoleCalculation.ANGLE) }
        binding.midnightCon.setOnClickListener { checkMazhab(PoleCalculation.MIDNIGHT) }
        binding.oneOfSevenCon.setOnClickListener { checkMazhab(PoleCalculation.ONEONSEVEN) }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }


    fun checkMazhab(poleCalculation: PoleCalculation) {
        clearPoles()
        when (poleCalculation) {
            PoleCalculation.ANGLE -> setColor(binding.angelarImg, binding.angelarSelected)
            PoleCalculation.ONEONSEVEN -> setColor(
                binding.oneOfSevenImg,
                binding.oneOfSevenSelected
            )
            PoleCalculation.NOTHING -> setColor(binding.nothingImg, binding.nothingImgSelected)
            PoleCalculation.MIDNIGHT -> setColor(binding.midnightImg, binding.midnightImgSelected)
        }
        globalPreferences.savePole(poleCalculation)

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