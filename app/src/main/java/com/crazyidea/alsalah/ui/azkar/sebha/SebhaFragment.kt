package com.crazyidea.alsalah.ui.azkar.sebha

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazyidea.alsalah.DataStoreCollector
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentSebhaBinding
import com.crazyidea.alsalah.ui.setting.AzkarSettings
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


const val MODE = "SEBHA_MODE"

@AndroidEntryPoint
class SebhaFragment : Fragment(), Animator.AnimatorListener {

    private var muted: Boolean = !DataStoreCollector.AzkarPrefs.azkarBeep
    private var vibrate: Boolean = DataStoreCollector.AzkarPrefs.azkarVibrate
    private var animator: ObjectAnimator? = null
    private var _binding: FragmentSebhaBinding? = null
    private val viewModel by viewModels<SebhaViewModel>()
    lateinit var mp: MediaPlayer
    lateinit var vp: Vibrator

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val fonts =
        intArrayOf(
            com.intuit.ssp.R.dimen._13ssp,
            com.intuit.ssp.R.dimen._15ssp,
            com.intuit.ssp.R.dimen._17ssp,
            com.intuit.ssp.R.dimen._19ssp,
            com.intuit.ssp.R.dimen._21ssp,
            com.intuit.ssp.R.dimen._23ssp,
        )

    private var currentFontIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mp = MediaPlayer.create(requireContext(), R.raw.click)
        vp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireActivity().getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        _binding = FragmentSebhaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        observerLiveData()
        return root
    }

    private fun observerLiveData() {
        viewModel.playSound.observe(viewLifecycleOwner) {
            if (it) {
                if (!muted) {
                    mp.seekTo(0)
                    mp.start()
                }
                if (!vibrate) {
                    vp.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                }
            }
        }
        viewModel.azkarCounter.observe(viewLifecycleOwner) {
            viewModel.azkar.value?.let { azkar ->
                val progress = ((it.toDouble() / azkar.count) * 100.toDouble()).roundToInt()
                animator = ObjectAnimator.ofInt(
                    binding.progress,
                    "progress",
                    progress
                )
                animator!!.duration = 300
                animator!!.addListener(this@SebhaFragment)
                animator!!.start()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getSebha()

        }
        binding.mute.setOnClickListener {
            muted = !muted
            viewModel.muted.value = muted
            viewModel.update(AzkarSettings.BEEP, !muted)
        }
        binding.vibration.setOnClickListener {
            vibrate = !vibrate
            viewModel.vibrate.value = vibrate
            viewModel.update(AzkarSettings.VIBRATE,vibrate)
        }
        binding.count.setOnClickListener {
            viewModel.azkarCounter.value = 0
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
        binding.bottomTools.settings.setOnClickListener {
            findNavController().navigate(
                SebhaFragmentDirections.actionSebhaFragmentToAzkarMenuFragment(
                    "تسابيح"
                )
            )
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.sebhaImg.isVisible = tab.position == 0
                binding.counterContainer.isVisible = tab.position == 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })

        if (savedInstanceState != null) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(savedInstanceState.getInt(MODE)))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(MODE, binding.tabLayout.selectedTabPosition)
        super.onSaveInstanceState(outState)

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