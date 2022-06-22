package com.crazyidea.alsalah.ui.quran

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.databinding.FragmentQuranPageBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.getJuzName
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class QuranPageFragment : Fragment() {

    private var pageNum: Int = 0
    private var _binding: FragmentQuranPageBinding? = null
    private val viewModel by viewModels<QuranViewModel>()

    lateinit var surahTV: TextView
    lateinit var ayatTV: TextView

    @Inject
    lateinit var globalPreferences: GlobalPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pageNum = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuranPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuran(pageNum)
        ayatTV = binding.ayah
        surahTV = binding.topLevelSurahName
        viewModel.pageContent.observe(viewLifecycleOwner) {

            val spannable = SpannableStringBuilder("")
            spannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0, 0,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )

            var lastIndex = 0
            var surah = if (it.first().number == 1) 1 else 2
            var surahName = ""
            var firstAyatLength = 0
            var secondAyatLength = 0
            var thirdAyatLength = 0
            it.forEach {
                if (it.number == 1) {
                    when (surah) {
                        1 -> {
                            surahName = it.surah
                            surahTV = binding.topLevelSurahName
                            ayatTV = binding.ayah
                            binding.topLevelSurah.visibility = VISIBLE
                            binding.topLevelSurahName.visibility = VISIBLE
                        }
                        2 -> {
                            setAyatText(spannable, surahName)
                            firstAyatLength = spannable.length
                            spannable.clear()

                            lastIndex = 0
                            surahName = it.surah
                            surahTV = binding.secondLevelSurahName
                            ayatTV = binding.ayah2
                            binding.secondLevelSurah.visibility = VISIBLE
                            binding.secondLevelSurahName.visibility = VISIBLE
                            binding.ayah2.visibility = VISIBLE
                        }
                        3 -> {
                            setAyatText(spannable, surahName)
                            secondAyatLength = spannable.length
                            spannable.clear()
                            lastIndex = 0
                            surahName = it.surah
                            surahTV = binding.thirdLevelSurahName
                            ayatTV = binding.ayah3
                            binding.thirdLevelSurah.visibility = VISIBLE
                            binding.thirdLevelSurahName.visibility = VISIBLE
                            binding.ayah3.visibility = VISIBLE
                        }
                    }
                    surah++
                }
                val newText = it.text.replace("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "\uFDFD\n")
                spannable.insert(lastIndex, newText)
                lastIndex += newText.length

                spannable.insert(
                    lastIndex,
                    " \u06DD${
                        String.format(
                            Locale(globalPreferences.getLocale()),
                            "%d", it.number
                        )
                    } "
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#58452f")),
                    lastIndex, (lastIndex + it.number.toString().length + 3),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
//                spannable.setSpan(
//                    RelativeSizeSpan(.7f),
//                    lastIndex + 1, (lastIndex + it.number.toString().length + 2),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                lastIndex += it.number.toString().length + 3
            }

            if (surah == 4) {
                thirdAyatLength = spannable.length
            } else if (surah == 3) {
                secondAyatLength = spannable.length
            } else if (surah == 2) {
                firstAyatLength = spannable.length
            } else {
                firstAyatLength = spannable.length
            }
            setAyatText(spannable, surahName)
            binding.juz.text = it.first().juz.toString().getJuzName(requireContext())
            binding.surah.text = it.first().surah
            setupWeights(firstAyatLength, secondAyatLength, thirdAyatLength)
        }
    }

    private fun setupWeights(firstAyatLength: Int, secondAyatLength: Int, thirdAyatLength: Int) {
        val sum = firstAyatLength + secondAyatLength + thirdAyatLength
        val set = ConstraintSet()
        set.clone(binding.container)
        set.setVerticalWeight(binding.ayah.id, firstAyatLength / sum.toFloat())
        set.setVerticalWeight(binding.ayah2.id, secondAyatLength / sum.toFloat())
        set.setVerticalWeight(binding.ayah3.id, thirdAyatLength / sum.toFloat())
        set.applyTo(binding.container)
    }

    private fun setAyatText(spannable: SpannableStringBuilder, surah: String) {
        ayatTV.setText(spannable, TextView.BufferType.SPANNABLE)
        surahTV.text = surah
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            QuranPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}