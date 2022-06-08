package com.crazyidea.alsalah.ui.blogDetail

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.crazyidea.alsalah.databinding.FragmentBlogDetailBinding
import com.crazyidea.alsalah.ui.azkar.azkar_details.AzkarDetailsFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlogDetailFragment : Fragment() {

    private var _binding: FragmentBlogDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<BlogDetailViewModel>()
    private val args by navArgs<BlogDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBlogDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.article.postValue(args.article)
        binding.model = viewModel
        binding.lifecycleOwner = this
        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }

        binding.blogDesc.text=
            (Html.fromHtml("<br><p>${args.article.text}</p>", Html.FROM_HTML_MODE_COMPACT))

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}