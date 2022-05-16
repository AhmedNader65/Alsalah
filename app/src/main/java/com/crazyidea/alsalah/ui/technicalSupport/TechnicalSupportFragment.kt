package com.crazyidea.alsalah.ui.technicalSupport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.databinding.FragmentTechnicalSupportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TechnicalSupportFragment : Fragment() {

    private var _binding: FragmentTechnicalSupportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<TechnicalSupportViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTechnicalSupportBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}