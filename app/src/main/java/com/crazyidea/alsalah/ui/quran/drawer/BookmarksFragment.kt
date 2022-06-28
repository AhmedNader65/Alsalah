package com.crazyidea.alsalah.ui.quran.drawer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.*
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.databinding.FragmentBookmarkBinding
import com.crazyidea.alsalah.databinding.FragmentFehresBinding
import com.crazyidea.alsalah.databinding.FragmentJuzBinding
import com.crazyidea.alsalah.ui.quran.SharedQuranViewModel
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.withoutDiacritics
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private lateinit var allData: MutableList<DataItem>
    private lateinit var adapter: BookmarksAdapter
    private var lastChecked: Surah? = null
    private var _binding: FragmentBookmarkBinding? = null
    private val viewModel by viewModels<SharedQuranViewModel>({ requireActivity() })

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = BookmarksAdapter(
            BookmarkClickListener {
                viewModel.setCurrentPage(it.id.toInt())
                viewModel.openDrawer.value = false
            }
        )
        binding.bookmarks.adapter = adapter

        setupObservers()
    }


    private fun setupObservers() {
        viewModel.bookmarks.observe(viewLifecycleOwner) { listOfSurahs ->
            val list = mutableListOf<BookmarkItem>()
            listOfSurahs.forEach {
                it.ayat?.let {
                    list.add(BookmarkItem.AyaItem(it))
                } ?: run {
                    list.add(
                        BookmarkItem.PageItem(
                            it.bookmark.page,
                            getString(R.string.page) + " " + it.bookmark.page
                        )
                    )
                }
            }
            adapter.submitList(list)
            binding.bookmarks.scrollToPosition(viewModel.sidePage.value?.minus(1) ?: 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}