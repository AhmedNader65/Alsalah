package com.crazyidea.alsalah.ui.mazhab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentChooseMazhabBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MazhabFragment : Fragment() {

    private var _binding: FragmentChooseMazhabBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<MazhabViewModel>()
    lateinit var globalPreferences: GlobalPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChooseMazhabBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        globalPreferences = GlobalPreferences(requireContext())
        checkMazhab(globalPreferences.getMazhab())
        binding.hanafiCon.setOnClickListener { checkMazhab("hanafi") }
        binding.hanbaliCon.setOnClickListener { checkMazhab("others") }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }


    fun checkMazhab(mazhab: String) {
        binding.hanafiImg.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.hanbaliImg.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.hanbaliImgChecked.visibility = View.GONE
        binding.hanafiImgSelect.visibility = View.GONE
        globalPreferences.saveMazhab(mazhab)
        if (mazhab == "others") {
            binding.hanbaliImg.setColorFilter(requireContext().themeColor(android.R.attr.colorPrimary))
            binding.hanbaliImgChecked.visibility = View.VISIBLE

        } else if (mazhab == "hanafi") {
            binding.hanafiImg.setColorFilter(requireContext().themeColor(android.R.attr.colorPrimary))
            binding.hanafiImgSelect.visibility = View.VISIBLE
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}