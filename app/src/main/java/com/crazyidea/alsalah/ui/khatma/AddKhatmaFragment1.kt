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
import com.crazyidea.alsalah.databinding.FragmentAddKhatma1Binding
import com.crazyidea.alsalah.databinding.FragmentAddKhatma2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddKhatmaFragment1 : Fragment() {

    private var _binding: FragmentAddKhatma1Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKhatma1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setKhatmaTypes()
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.next.setOnClickListener { findNavController().navigate(AddKhatmaFragment1Directions.actionAddKhatmaFragment1ToAddKhatmaFragment2()) }
    }


    private fun setKhatmaTypes() {
        val stringList = listOf<String>("مراجعة", "حفظ", "تدبر", "قراءة")

        val fieldsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_in_array,
            stringList
        )
        binding.typesAutoComplete.setAdapter<ArrayAdapter<String>>(fieldsAdapter)
        binding.typesAutoComplete.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView: AdapterView<*>?, view1: View, i: Int, l: Long ->

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}