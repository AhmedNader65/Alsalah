package com.crazyidea.alsalah.ui.khatma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.databinding.FragmentAddKhatma1Binding
import com.crazyidea.alsalah.databinding.FragmentAddKhatma2Binding
import com.crazyidea.alsalah.databinding.FragmentAddKhatma3Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddKhatmaFragment3 : Fragment() {

    private var _binding: FragmentAddKhatma3Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKhatma3Binding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.done.setOnClickListener { findNavController().navigate(AddKhatmaFragment3Directions.actionAddKhatmaFragment3ToKhatmaFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}