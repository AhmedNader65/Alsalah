package com.crazyidea.alsalah.ui.azkar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.databinding.FragmentAzkarBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AzkarFragment : Fragment() {

    private var _binding: FragmentAzkarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<AzkarViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAzkarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.morningAzkarButton.setOnClickListener {
            findNavController().navigate(
                AzkarFragmentDirections.actionNavigationAzkarToNavigationAzkarDetails(
                    "أذكار الصباح"
                )
            )
        }
        binding.eveningAzkarButton.setOnClickListener {
            findNavController().navigate(
                AzkarFragmentDirections.actionNavigationAzkarToNavigationAzkarDetails(
                    "أذكار المساء"
                )
            )
        }
        binding.sleepingAzkarButton.setOnClickListener {
            findNavController().navigate(
                AzkarFragmentDirections.actionNavigationAzkarToNavigationAzkarDetails(
                    "أذكار النوم"
                )
            )
        }
        binding.afterPrayerAzkarButton.setOnClickListener {
            findNavController().navigate(
                AzkarFragmentDirections.actionNavigationAzkarToNavigationAzkarDetails(
                    "أذكار بعد السلام من الصلاة المفروضة"
                )
            )
        }
        binding.sebhaButton.setOnClickListener {
            findNavController().navigate(AzkarFragmentDirections.actionNavigationAzkarToSebhaFragment())
        }
        binding.moreAzkarButton.setOnClickListener {
            findNavController().navigate(
                AzkarFragmentDirections.actionNavigationAzkarToNavigationAzkarDetails(
                    "اخرى"
                )
            )
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTotalProgress()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}