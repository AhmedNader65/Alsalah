package com.crazyidea.alsalah.ui.khatma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentAddKhatma2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddKhatmaFragment2 : Fragment() {

    private var _binding: FragmentAddKhatma2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKhatma2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.next.setOnClickListener { findNavController().navigate(AddKhatmaFragment2Directions.actionAddKhatmaFragment2ToAddKhatmaFragment3()) }
        setParts()
    }


    private fun setParts() {
        val stringList = (1..30).toList().map { it.toString() }

        val fieldsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            stringList
        )
        binding.partsAutoComplete.setAdapter<ArrayAdapter<String>>(fieldsAdapter)
        binding.partsAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView: AdapterView<*>?, view1: View, i: Int, l: Long ->

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}