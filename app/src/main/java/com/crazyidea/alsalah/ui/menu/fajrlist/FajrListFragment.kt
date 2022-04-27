package com.crazyidea.alsalah.ui.menu.fajrlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.crazyidea.alsalah.databinding.FragmentFajrListBinding
import com.crazyidea.alsalah.utils.ContactData
import com.crazyidea.alsalah.utils.retrieveAllContacts


class FajrListFragment : Fragment() {

    private var _binding: FragmentFajrListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fajrListViewModel =
            ViewModelProvider(this).get(FajrListViewModel::class.java)

        _binding = FragmentFajrListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addContacts.setOnClickListener {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}