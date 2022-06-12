package com.crazyidea.alsalah.ui.azkar.azkar_details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentAzkarDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt

@AndroidEntryPoint
class AzkarDetailsFragment : Fragment(), Animator.AnimatorListener {

    private var animator: ObjectAnimator? = null
    private var _binding: FragmentAzkarDetailsBinding? = null
    private val args by navArgs<AzkarDetailsFragmentArgs>()

    private val viewModel by viewModels<AzkarDetailsViewModel>()
    val fonts =
        intArrayOf(
            com.intuit.ssp.R.dimen._13ssp,
            com.intuit.ssp.R.dimen._15ssp,
            com.intuit.ssp.R.dimen._17ssp,
            com.intuit.ssp.R.dimen._19ssp,
            com.intuit.ssp.R.dimen._21ssp,
            com.intuit.ssp.R.dimen._23ssp,
        )

    var currentFontIndex = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAzkarDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.bottomTools.settings.setOnClickListener {
            findNavController().navigate(
                AzkarDetailsFragmentDirections.actionNavigationAzkarDetailsToAzkarMenuFragment(
                    args.category
                )
            )
        }

        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return root
    }

    private fun setupObserver() {
        viewModel.azkarCounter.observe(viewLifecycleOwner) {
            viewModel.azkar.value?.let { azkar ->
                val progress = ((it.toDouble() / azkar.count) * 100.toDouble()).roundToInt()
                animator = ObjectAnimator.ofInt(
                    binding.progress,
                    "progress",
                    progress
                )
                animator!!.duration = 300
                animator!!.addListener(this@AzkarDetailsFragment)
                animator!!.start()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = args.category
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getAzkar(args.category)
        }
        binding.bottomTools.fontSize.setOnClickListener {
            currentFontIndex++
            binding.azkarTv.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(fonts[currentFontIndex % fonts.size])
            )
        }
        binding.bottomTools.nightMode.setOnClickListener {
            val nightModeEnabled = AppCompatDelegate.getDefaultNightMode()
            if (nightModeEnabled == AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAnimationStart(animation: Animator?) {

        Log.i("Animation", "onAnimationStart")
    }

    override fun onAnimationEnd(animation: Animator?) {
        Log.i("Animation", "onAnimationEnd")
        viewModel.getNextAzkar()
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        animator?.addListener(null)
    }
}