package com.crazyidea.alsalah.ui.azkar_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.databinding.FragmentAzkarBinding
import com.crazyidea.alsalah.databinding.FragmentAzkarDetailsBinding

class AzkarDetailsFragment : Fragment() {

    private var _binding: FragmentAzkarDetailsBinding? = null

    private val viewModel by viewModels<AzkarDetailsViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzkarDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.bottomTools.settings.setOnClickListener {
            findNavController().navigate(AzkarDetailsFragmentDirections.actionNavigationAzkarDetailsToAzkarSettingsFragment())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}