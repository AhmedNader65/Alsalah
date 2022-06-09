package com.crazyidea.alsalah.ui.blogDetail

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.adapter.RepliesAdapter
import com.crazyidea.alsalah.databinding.FragmentBlogDetailBinding
import com.crazyidea.alsalah.utils.ImageGetter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BlogDetailFragment : Fragment() {

    private var _binding: FragmentBlogDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<BlogDetailViewModel>()
    private val args by navArgs<BlogDetailFragmentArgs>()
    var bool: Boolean = false
    lateinit var repliesAdapter: RepliesAdapter

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
        if (args.type == 1)
            viewModel.getComments(args.article.id)
        else
            viewModel.getFwaedComments(args.article.id)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
        bool = args.article.liked
        setupComments()

        // Creating object of ImageGetter class you just created
        val imageGetter = ImageGetter(requireActivity().resources, binding.blogDesc)

        // Using Html framework to parse html
        val styledText = HtmlCompat.fromHtml(
            args.article.text,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            imageGetter, null
        )

        // to enable image/link clicking
        binding.blogDesc.movementMethod = LinkMovementMethod.getInstance()

        // setting the text after formatting html and downloading and setting images
        binding.blogDesc.text = styledText
        binding.storeImgContainer.setOnClickListener {
            if (args.type == 1)
                viewModel.postArticleComment(args.article.id, binding.myText.text.toString())
            else
                viewModel.postFwaedComment(args.article.id, binding.myText.text.toString())

        }
        checkImageStatus(bool)

        binding.likesImg.setOnClickListener {
            if (args.type == 1)
                viewModel.postArticleLike(args.article.id)
            else
                viewModel.postFwaedLike(args.article.id)

        }


    }

    private fun setupComments() {
        viewModel.commentData.observe(viewLifecycleOwner) {
            repliesAdapter = RepliesAdapter(it)
            binding.repliesList.adapter = repliesAdapter
        }
        viewModel.comments.observe(viewLifecycleOwner) {
            binding.commentsCount.text = args.article.comments.plus(1).toString()
            repliesAdapter.addComment(it)
            binding.myText.setText("")
        }
        viewModel.likedComment.observe(viewLifecycleOwner) {
            if (!bool){
                args.article.likes = args.article.likes.plus(1)
                binding.likesCount.text = args.article.likes.toString()
            }else{
                args.article.likes = args.article.likes.minus(1)
                binding.likesCount.text = args.article.likes.toString()
            }
            bool = !bool
            checkImageStatus(bool)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun checkImageStatus(liked: Boolean) {
        if (liked)
            binding.likesImg.setImageDrawable(requireContext().getDrawable(R.drawable.ic_baseline_favorite_24))
        else
            binding.likesImg.setImageDrawable(requireContext().getDrawable(R.drawable.ic_baseline_favorite_border_24))

    }


}