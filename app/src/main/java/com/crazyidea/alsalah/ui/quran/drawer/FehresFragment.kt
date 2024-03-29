package com.crazyidea.alsalah.ui.quran.drawer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.crazyidea.alsalah.MainActivity
import com.crazyidea.alsalah.adapter.DataItem
import com.crazyidea.alsalah.adapter.SurahAdapter
import com.crazyidea.alsalah.adapter.SurahClickListener
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.databinding.FragmentFehresBinding
import com.crazyidea.alsalah.ui.quran.SharedQuranViewModel
import com.crazyidea.alsalah.utils.withoutDiacritics
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FehresFragment : Fragment() {

    private lateinit var allData: MutableList<DataItem>
    private lateinit var adapter: SurahAdapter
    private var lastChecked: Surah? = null
    private var _binding: FragmentFehresBinding? = null
    private val viewModel by viewModels<SharedQuranViewModel>({ requireActivity() })


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFehresBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = SurahAdapter(SurahClickListener({ surah ->
            lastChecked?.let {
                it.checked = false
            }
            lastChecked = surah
            surah.checked = true
            adapter.notifyDataSetChanged()
            viewModel.setCurrentPage(surah.page)
            viewModel.openDrawer.value = false
        }, {
            viewModel.getJuzPage(it.toInt())
            viewModel.openDrawer.value = false
        }))
        viewModel.getAllSurah()
        binding.juz.adapter = adapter
        binding.searchSurah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s != null && s.isNotEmpty()) {
                    adapter.submitList(allData.filter {
                        (it is DataItem.surahItem && getWithoutDiac(it.surah.name)
                            .contains(s.toString()))
                    })
                } else {
                    if (this@FehresFragment::allData.isInitialized)
                        adapter.addHeaderAndSubmitList(allData)
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        setupObservers()
    }

    fun getWithoutDiac(st: String): String {

        val surWithDiac = st.withoutDiacritics()
        return surWithDiac
    }

    private fun setupObservers() {

        viewModel.sidePage.observe(viewLifecycleOwner) { page ->
            lastChecked?.let {
                it.checked = false
            }
            val dataItem = adapter.getData().findLast {
                (it is DataItem.surahItem && it.surah.page <= page && it.surah.last_page >= page)
            }
            dataItem?.let {
                lastChecked = (it as DataItem.surahItem).surah
                lastChecked!!.checked = true
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.allSurahs.observe(viewLifecycleOwner) { listOfSurahs ->
            val list = mutableListOf<DataItem>()
            for (juz in 1..30) {
                list.add(DataItem.Header(juz.toString()))
                list += listOfSurahs.filter { it.juz == juz }.map {
                    DataItem.surahItem(it.apply {
                        if (it.page == viewModel.sidePage.value)
                            checked = true
                    })
                }
            }
            allData = list
            adapter.addHeaderAndSubmitList(allData)
            binding.juz.scrollToPosition(viewModel.sidePage.value?.minus(1) ?: 0)
        }

        binding.juz.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                (requireActivity() as MainActivity).hideKeyboard()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}