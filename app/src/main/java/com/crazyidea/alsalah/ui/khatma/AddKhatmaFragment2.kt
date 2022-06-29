package com.crazyidea.alsalah.ui.khatma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.ExpectedTime
import com.crazyidea.alsalah.databinding.FragmentAddKhatma2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddKhatmaFragment2 : Fragment() {

    private var _binding: FragmentAddKhatma2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKhatma2Binding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.next.setOnClickListener {
            viewModel.saveKhatma()
            findNavController().navigate(AddKhatmaFragment2Directions.actionAddKhatmaFragment2ToAddKhatmaFragment3())
        }
        binding.expectedTimeRadio.setOnClickListener { setChecked(ExpectedTime.TIME) }
        binding.expectedTimeCard.setOnClickListener { setChecked(ExpectedTime.TIME) }
        binding.expectedWerdRadio.setOnClickListener { setChecked(ExpectedTime.WERD) }
        binding.expectedWerdCard.setOnClickListener { setChecked(ExpectedTime.WERD) }
        setParts()
        binding.expectedTimeCard.performClick()
    }

    private fun setChecked(expectedTime: ExpectedTime) {
        if (expectedTime == ExpectedTime.TIME) {
            binding.expectedTimeRadio.isChecked = true
            binding.expectedWerdRadio.isChecked = false
            binding.expectedTimeText.text = resources.getString(R.string.requiredTime)
            binding.daysHezb.text = resources.getString(R.string.days)
            viewModel.days.postValue(30)
            viewModel.result.postValue(20)
            viewModel.type.postValue(0)

        } else if (expectedTime == ExpectedTime.WERD) {
            binding.expectedWerdRadio.isChecked = true
            binding.expectedTimeRadio.isChecked = false
            binding.expectedTimeText.text = resources.getString(R.string.requiredWerd)
            binding.daysHezb.text = resources.getString(R.string.page)
            viewModel.days.postValue(20)
            viewModel.result.postValue(30)
            viewModel.type.postValue(1)

        }
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
                viewModel.khatma.value?.start = i.plus(1)
                viewModel.khatma.value = viewModel.khatma.value
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}