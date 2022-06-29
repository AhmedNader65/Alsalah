package com.crazyidea.alsalah.ui.khatma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.databinding.FragmentAddKhatma1Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddKhatmaFragment1 : Fragment() {

    private var name: String? = null
    private var type: String? = null
    private var _binding: FragmentAddKhatma1Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKhatma1Binding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.model = viewModel
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setKhatmaTypes()
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        binding.next.setOnClickListener {
            var error = false
            this.name = binding.nameIT.editText?.text.toString()
            if (type == null) {
                error = true
                binding.typeIT.error = getString(R.string.choose_khatma_type)
            }
            if (name.isNullOrEmpty()) {
                error = true
                binding.nameIT.error = getString(R.string.type_khatma_name)
            }
            if (!error)
                findNavController().navigate(AddKhatmaFragment1Directions.actionAddKhatmaFragment1ToAddKhatmaFragment2())
        }
    }


    private fun setKhatmaTypes() {
        val stringList = listOf(
            getString(R.string.review), getString(R.string.memorise), getString(
                R.string.thinking
            ), getString(R.string.reading)
        )

        val fieldsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_in_array,
            stringList
        )
        binding.typesAutoComplete.setAdapter<ArrayAdapter<String>>(fieldsAdapter)
        binding.typesAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView: AdapterView<*>?, view1: View, i: Int, l: Long ->
                this.type = when (i) {
                    0 -> "review"
                    1 -> "memorise"
                    2 -> "think"
                    else -> "read"
                }
                this.type?.let {
                    viewModel.khatma.value?.type = it
                    viewModel.khatma.value = viewModel.khatma.value
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}