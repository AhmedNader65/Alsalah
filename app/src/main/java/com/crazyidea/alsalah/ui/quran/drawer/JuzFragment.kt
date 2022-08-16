package com.crazyidea.alsalah.ui.quran.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.adapter.DataItem
import com.crazyidea.alsalah.adapter.SurahAdapter
import com.crazyidea.alsalah.adapter.SurahClickListener
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.databinding.FragmentJuzBinding
import com.crazyidea.alsalah.ui.quran.SharedQuranViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class JuzFragment : Fragment() {

    private lateinit var allData: MutableList<DataItem>
    private lateinit var adapter: SurahAdapter
    private var lastChecked: Surah? = null
    private var _binding: FragmentJuzBinding? = null
    private val viewModel by viewModels<SharedQuranViewModel>({ requireActivity() })


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentJuzBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = SurahAdapter(SurahClickListener({},{
            viewModel.getJuzPage(it.toInt())
            viewModel.openDrawer.value = false
        }))
        viewModel.getAllSurah()
        binding.juz.adapter = adapter

        setupObservers()
    }


    private fun setupObservers() {


        viewModel.allSurahs.observe(viewLifecycleOwner) { listOfSurahs ->
            val list = mutableListOf<DataItem>()
            for (juz in 1..30) {
                list.add(DataItem.Header(juz.toString()))
            }
            allData = list
            adapter.addHeaderAndSubmitList(allData)
            binding.juz.scrollToPosition(viewModel.sidePage.value?.minus(1) ?: 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}