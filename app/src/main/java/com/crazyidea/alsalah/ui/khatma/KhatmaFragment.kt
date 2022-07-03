package com.crazyidea.alsalah.ui.khatma

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.KhatmaClickListener
import com.crazyidea.alsalah.adapter.KhatmasAdapter
import com.crazyidea.alsalah.adapter.QuranPageAdapter
import com.crazyidea.alsalah.databinding.FragmentKhatmaBinding
import com.crazyidea.alsalah.ui.quran.QuranViewModel
import com.crazyidea.alsalah.ui.quran.SharedQuranViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KhatmaFragment : Fragment() {

    private lateinit var alert: AlertDialog
    private var _binding: FragmentKhatmaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<KhatmaViewModel>()
    private val quranViewModel by viewModels<SharedQuranViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentKhatmaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quranViewModel.downloadQuran()
        showDialog()
        binding.addKhatma.setOnClickListener {
            findNavController().navigate(KhatmaFragmentDirections.actionKhatmaFragmentToAddKhatmaFragment1())
        }
        viewModel.khatmas.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.emptyLayout.visibility = VISIBLE
                binding.khatmaList.visibility = GONE
            } else {
                binding.emptyLayout.visibility = GONE
                binding.khatmaList.visibility = VISIBLE
                val adapter = KhatmasAdapter(
                    KhatmaClickListener {
                        findNavController().navigate(
                            KhatmaFragmentDirections.actionKhatmaFragmentToQuranFragment(
                                it,"khatma"
                            )
                        )
                        Toast.makeText(requireContext(), it.name, Toast.LENGTH_SHORT).show()
                    }
                )
                binding.khatmaList.adapter = adapter
                adapter.submitList(it)
            }
        }
        binding.back.setOnClickListener { requireActivity().onBackPressed() }

        quranViewModel.downloaded.observe(viewLifecycleOwner) {
            if (it == true) {
                alert.dismiss()
            }
        }
    }

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setView(layoutInflater.inflate(R.layout.dialog_downloading, null))
            // if the dialog is cancelable
            .setCancelable(false)
        // positive button text and action

        // create dialog box
        alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}