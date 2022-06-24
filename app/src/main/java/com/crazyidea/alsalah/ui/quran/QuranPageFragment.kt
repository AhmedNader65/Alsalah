package com.crazyidea.alsalah.ui.quran

import android.graphics.*
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentQuranPageBinding
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.getJuzName
import com.crazyidea.alsalah.utils.indexesOf
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
//        val drawables: Drawable = resources.getDrawable(R.drawable.surah_img)

//        surahTV = binding.topLevelSurahName
        viewModel.pageContent.observe(viewLifecycleOwner) {
            var spannable = SpannableStringBuilder("")
            var first = true
            it.forEach {
                var newText = it.text
                if (it.number == 1) {
                    val drawables = getDrawable(it.surah)
                    if (first) {
                        first = false
                    } else
                        spannable.append("\n")
                    val start: Int = spannable.length

                    val newStr =
                        "f"
                    spannable.append(newStr)
                    spannable.setSpan(
                        ImageSpan(requireContext(), drawables),
                        start,
                        spannable.length,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    spannable.append("\n")

                    newText =
                        it.text.replace("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "\uFDFD\n")
                    newText = newText.replace("بِّسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيم", "\uFDFD\n")
                } else {
                    first = false
                }
                spannable.append(newText)
                spannable.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    spannable.length - newText.length, spannable.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                val numberAya = " \u06DD${
                    String.format(
                        Locale(globalPreferences.getLocale()),
                        "%d", it.number
                    )
                } "
                spannable.append(
                    numberAya
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#58452f")),
                    spannable.length - numberAya.length, spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                val allah = "ٱللَّهُ"
                val allah2 = "ٱللَّهِ"
                val allah3 = "ٱللَّهَ"
                val allah4 = "ٱللَّه"
                spannable = markAllahWord(spannable,allah)
                spannable = markAllahWord(spannable,allah2)
                spannable = markAllahWord(spannable,allah3)
                spannable = markAllahWord(spannable,allah4)


            }
            print(spannable.toString())
            binding.ayah.setText(spannable, TextView.BufferType.EDITABLE)

            binding.juz.text = it.first().juz.toString().getJuzName(requireContext())
            binding.surah.text = it.first().surah
        }
//        viewModel.pageContent.observe(viewLifecycleOwner) {
//
//            val spannable = SpannableStringBuilder("")
//            spannable.setSpan(
//                ForegroundColorSpan(Color.BLACK),
//                0, 0,
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
//            )
//
//            var lastIndex = 0
//            var surah = if (it.first().number == 1) 1 else 2
//            var surahName = ""
//            var firstAyatLength = 0
//            var secondAyatLength = 0
//            var thirdAyatLength = 0
//            it.forEach {
//                if (it.number == 1) {
//                    when (surah) {
//                        1 -> {
//                            surahName = it.surah
//                            surahTV = binding.topLevelSurahName
//                            ayatTV = binding.ayah
//                            binding.topLevelSurah.visibility = VISIBLE
//                            binding.topLevelSurahName.visibility = VISIBLE
//                        }
//                        2 -> {
//                            setAyatText(spannable, surahName)
//                            firstAyatLength = spannable.length
//                            spannable.clear()
//
//                            lastIndex = 0
//                            surahName = it.surah
//                            surahTV = binding.secondLevelSurahName
//                            ayatTV = binding.ayah2
//                            binding.secondLevelSurah.visibility = VISIBLE
//                            binding.secondLevelSurahName.visibility = VISIBLE
//                            binding.ayah2.visibility = VISIBLE
//                        }
//                        3 -> {
//                            setAyatText(spannable, surahName)
//                            secondAyatLength = spannable.length
//                            spannable.clear()
//                            lastIndex = 0
//                            surahName = it.surah
//                            surahTV = binding.thirdLevelSurahName
//                            ayatTV = binding.ayah3
//                            binding.thirdLevelSurah.visibility = VISIBLE
//                            binding.thirdLevelSurahName.visibility = VISIBLE
//                            binding.ayah3.visibility = VISIBLE
//                        }
//                    }
//                    surah++
//                }
//                println(it.text)
//                var newText = it.text.replace("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "\uFDFD\n")
//                newText = newText.replace("بِّسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيم", "\uFDFD\n")
//                spannable.insert(lastIndex, newText)
//                lastIndex += newText.length
//
//                spannable.insert(
//                    lastIndex,
//                    " \u06DD${
//                        String.format(
//                            Locale(globalPreferences.getLocale()),
//                            "%d", it.number
//                        )
//                    } "
//                )
//                spannable.setSpan(
//                    ForegroundColorSpan(Color.parseColor("#58452f")),
//                    lastIndex, (lastIndex + it.number.toString().length + 3),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
////                spannable.setSpan(
////                    RelativeSizeSpan(.7f),
////                    lastIndex + 1, (lastIndex + it.number.toString().length + 2),
////                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
////                )
//                lastIndex += it.number.toString().length + 3
//            }
//
//            if (surah == 4) {
//                thirdAyatLength = spannable.length
//            } else if (surah == 3) {
//                secondAyatLength = spannable.length
//            } else if (surah == 2) {
//                firstAyatLength = spannable.length
//            } else {
//                firstAyatLength = spannable.length
//            }
//            setAyatText(spannable, surahName)
//            binding.juz.text = it.first().juz.toString().getJuzName(requireContext())
//            binding.surah.text = it.first().surah
//            setupWeights(firstAyatLength, secondAyatLength, thirdAyatLength)
//        }
    }

    private fun markAllahWord(
        spannable: SpannableStringBuilder,
        allah: String
    ): SpannableStringBuilder {

        val indexes = spannable.indexesOf(allah)
        indexes.forEach {
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#ff0000")),
                it, it + allah.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }

    private fun getDrawable(surah: String): Bitmap {
        var bitmap = BitmapFactory.decodeResource(
            resources, R.drawable.surah_img
        )
        val width = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._160sdp)
        val height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._40sdp)
        bitmap =
            resize(
                bitmap,
                width,
                height
            )
        val bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val c = Canvas(bmp)
        val text = surah
        val p = Paint()
        p.typeface = ResourcesCompat.getFont(requireContext(), R.font.quran);
        p.textSize = resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp).toFloat()
        p.color = resources.getColor(R.color.black)
        c.drawText(
            text, (bmp.width - p.measureText(text)) / 2, (c.getHeight() / 2
                    - (p.descent() + p.ascent()) / 2 - 10), p
        )
        return bmp
    }

    private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var image = image
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            image
        } else {
            image
        }
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