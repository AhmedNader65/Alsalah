package com.crazyidea.alsalah.ui.azkar.azkar_details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crazyidea.alsalah.databinding.FragmentAzkarDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class AzkarDetailsFragment : Fragment() {

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
        setupObserver()
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
                animator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        Log.i("Animation", "onAnimationStart")
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        Log.i("Animation", "onAnimationEnd")
                        viewModel.getNextAzkar()
                    }
                })
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
        setConstraints()
        setSize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onDestroy() {
        super.onDestroy()
        animator?.addListener(null)
    }


    fun setSize() {
        val display: Display = requireActivity().getWindowManager().getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        Log.e("TAG", "setSize: height" + height + "      width" + width)
        val ratioy = ((height.toDouble() / 2280))
        val ratiox: Double = (width.toDouble() / 1080)
        Log.e("TAG", "setSize: height" + ratioy + "      width" + ratiox)
        binding.bottomTools.bottomToolsCard.scaleX = ratiox.toFloat()//set width scale for ma
        binding.bottomTools.bottomToolsCard.scaleY = ratioy.toFloat()//set height scale for ma
    }

    fun setConstraints() {
        binding.counterImage.post {
            binding.counterImage.width
           val widthh = binding.counterImage.width
            val buttonwidth = (widthh * 0.47)
            val progresswidth = (widthh * 0.7)
            val fontsize = (widthh * 0.08)
            Log.e("TAG", "setConstraints: " + widthh + "      " + buttonwidth + "     " + fontsize)
            binding.materialCard.updateLayoutParams {
                height = buttonwidth.toInt()
                width = buttonwidth.toInt()
            }
            binding.counter.updateLayoutParams {
                height = buttonwidth.toInt()
                width = buttonwidth.toInt()
            }
            binding.counter.textSize=fontsize.toFloat()
            binding.progress.updateLayoutParams {
                height = progresswidth.toInt()
                width = progresswidth.toInt()
            }

        }

    }
}