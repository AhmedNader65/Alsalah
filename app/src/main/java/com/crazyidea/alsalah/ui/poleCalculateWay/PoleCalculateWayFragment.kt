package com.crazyidea.alsalah.ui.poleCalculateWay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.PoleCalculation
import com.crazyidea.alsalah.databinding.FragmentChoosePoleCalculateWayBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
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
        binding.angelarImg.setImageDrawable(resources.getDrawable(R.drawable.ic_lang_unchecked))
        binding.midnightImg.setImageDrawable(resources.getDrawable(R.drawable.ic_lang_unchecked))
        binding.nothingImg.setImageDrawable(resources.getDrawable(R.drawable.ic_lang_unchecked))
        binding.oneOfSevenImg.setImageDrawable(resources.getDrawable(R.drawable.ic_lang_unchecked))
        when (poleCalculation) {
            PoleCalculation.ANGLE -> binding.angelarImg.setImageDrawable(resources.getDrawable(R.drawable.ic_checked_lang))
            PoleCalculation.ONEONSEVEN -> binding.oneOfSevenImg.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_checked_lang
                )
            )
            PoleCalculation.NOTHING -> binding.nothingImg.setImageDrawable(resources.getDrawable(R.drawable.ic_checked_lang))
            PoleCalculation.MIDNIGHT -> binding.midnightImg.setImageDrawable(resources.getDrawable(R.drawable.ic_checked_lang))
        }
        globalPreferences.savePole(poleCalculation)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}