package com.crazyidea.alsalah.ui.fawaed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.ArticlesAdapter
import com.crazyidea.alsalah.adapter.LanguagesAdapter
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.SupportedLanguage
import com.crazyidea.alsalah.databinding.FragmentFawaedBinding
import com.crazyidea.alsalah.databinding.FragmentFawaedBolgsBinding
import com.crazyidea.alsalah.ui.home.HomeFragmentDirections
import com.crazyidea.alsalah.utils.withSimpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FawaedFragment : Fragment(), ArticlesAdapter.ArticleListner {

    private var _binding: FragmentFawaedBolgsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<FawaedSettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFawaedBolgsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        setupFwaed()
    }

    private fun setupFwaed() {
        viewModel.fawaedData.observe(viewLifecycleOwner) {
            binding.blogsRV.adapter = ArticlesAdapter(it, this)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onArticlePicked(article: Articles) {
        findNavController().navigate(
            FawaedFragmentDirections.actionFawaedFragment2ToBlogDetailFragment(
                article,2
            )
        )
    }

    override fun onLikedClicked(article: Articles) {

    }


}