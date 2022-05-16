package com.crazyidea.alsalah.ui.menu.whoAreWe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.databinding.FragmentTechnicalSupportBinding
import com.crazyidea.alsalah.databinding.FragmentWhoAreWeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WhoAreWeFragment : Fragment() {

    private var _binding: FragmentWhoAreWeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<WhoAreWeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWhoAreWeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}