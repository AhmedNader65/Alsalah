package com.crazyidea.alsalah.ui.quran

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentQuranPageBinding
import com.crazyidea.alsalah.ui.quickaction.ActionItem
import com.crazyidea.alsalah.ui.quickaction.QuickAction
import com.crazyidea.alsalah.ui.quickaction.QuickAction.OnActionItemClickListener
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.getJuzName
import com.crazyidea.alsalah.utils.indexesOf
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class QuranPageFragment : Fragment() {


    // action id
    private val ID_ITEM_1 = 1
    private val ID_ITEM_2 = 2
    private val ID_ITEM_3 = 3
    private val ID_ITEM_4 = 4
    private val ID_ITEM_5 = 5

    private val selectIdx = 0

    private val itemArray = intArrayOf(ID_ITEM_1, ID_ITEM_2, ID_ITEM_3, ID_ITEM_4, ID_ITEM_5)
    private lateinit var mQuickAction: QuickAction
    private lateinit var originalText: SpannableStringBuilder
    private var longClicked: Boolean = false
    private var pageNum: Int = 0
    private var _binding: FragmentQuranPageBinding? = null
    private val sharedViewModel by viewModels<SharedQuranViewModel>({ requireActivity() })
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

        initQuickActions()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuran(pageNum)
        ayatTV = binding.ayah
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
                val allah = "ٱللَّهُ "
                val allah2 = "ٱللَّهِ "
                val allah3 = "ٱللَّهَ "
                val allah4 = "ٱللَّه "
                spannable = markAllahWord(spannable, allah)
                spannable = markAllahWord(spannable, allah2)
                spannable = markAllahWord(spannable, allah3)
                spannable = markAllahWord(spannable, allah4)


            }
            print(spannable.toString())
            binding.ayah.setText(spannable, TextView.BufferType.EDITABLE)

            binding.juz.text = it.first().juz.toString().getJuzName(requireContext())
            binding.surah.text = it.first().surah
            binding.pageNumber.text = String.format(
                Locale(globalPreferences.getLocale()),
                "%d", it.first().page
            )
        }
        binding.juz.setOnClickListener {
            sharedViewModel.openDrawer.value = true
        }
        binding.surah.setOnClickListener {
            sharedViewModel.openDrawer.value = true
        }

        binding.ayah.setOnTouchListener { v, motionEvent ->
            when (motionEvent.getAction()) {
                MotionEvent.ACTION_UP -> {
                    if (longClicked) {
                        val mOffset =
                            binding.ayah.getOffsetForPosition(motionEvent.x, motionEvent.y)
                        highlightText(
                            motionEvent.x, motionEvent.y,
                            findWord(
                                binding.ayah.text.toString(),
                                mOffset
                            )
                        )
                        longClicked = false
                    } else {
                        v.performClick()
                    }
                }
            }

            false
        }
        binding.ayah.setOnLongClickListener(OnLongClickListener {
            longClicked = true
            false
        })

    }

    private fun highlightText(x: Float, y: Float, indexClicked: Int) {
        if (!this::originalText.isInitialized)
            originalText = SpannableStringBuilder(binding.ayah.text)
        else
            binding.ayah.text = originalText
        val sb = SpannableStringBuilder(binding.ayah.text)
        var ayat = sb.indexesOf("\u06DD")
        var spaces = sb.indexesOf(" ")
        val start = ayat.findLast {
            indexClicked > it
        }
        val startSpace = spaces.find {
            it > start!!
        }
        val end = ayat.find {
            indexClicked < it
        }
        sb.setSpan(
            BackgroundColorSpan(Color.parseColor("#E4DECF")),
            startSpace!!,
            end!!,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.ayah.text = sb
        val lineOfText = binding.ayah.layout.getLineForOffset(startSpace)
        val yCoord = binding.ayah.layout.getLineTop(lineOfText)
        mQuickAction.show(binding.ayah, 0f, yCoord);
    }


    private fun initQuickActions() {

        mQuickAction = QuickAction(requireContext())
        mQuickAction.setOnActionItemClickListener(mActionClick)

            val copyItem = ActionItem(
                0,
                "Copy",
                resources.getDrawable(R.drawable.ic_baseline_content_copy_24),
                selectIdx
            )
            val bookmarkItem = ActionItem(
                0,
                "Bookmark",
                resources.getDrawable(R.drawable.ic_baseline_bookmark_add_24),
                selectIdx
            )
            val playItem = ActionItem(
                0,
                "Play",
                resources.getDrawable(R.drawable.ic_baseline_play_arrow_24),
                selectIdx
            )
            mQuickAction.addActionItem(bookmarkItem) // add action items into QuickAction.
            mQuickAction.addActionItem(copyItem) // add action items into QuickAction.
            mQuickAction.addActionItem(playItem) // add action items into QuickAction.

    }


    private val mActionClick: OnActionItemClickListener = object : OnActionItemClickListener {
        override fun onItemClick(source: QuickAction, pos: Int, actionId: Int, selectIdx: Int) {
            val msg = ""
            if (actionId in ID_ITEM_1..ID_ITEM_5) {
                Toast.makeText(
                    requireContext(),
                    String.format("ITEM ${source.getActionItem(pos).title}"),
                    Toast.LENGTH_SHORT
                ).show()
            }
            mQuickAction.dismiss()
        }
    }

    // This function is to change allah color to red
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


    // This function is to draw surah name inside an islamic frame
    private fun getDrawable(surah: String): Bitmap {

        val width =
            Resources.getSystem().displayMetrics.widthPixels - resources.getDimensionPixelSize(
                com.intuit.sdp.R.dimen._24sdp
            )
        val height = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._40sdp)

        val bitmap = resources.getDrawable(R.drawable.ic_asset_1).toBitmap(width, height)

        val bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val c = Canvas(bmp)
        val p = Paint()
        p.typeface = ResourcesCompat.getFont(requireContext(), R.font.quran)

        p.textSize = resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp).toFloat()
        p.color = resources.getColor(R.color.black)
        c.drawText(
            surah, (bmp.width - p.measureText(surah)) / 2, (c.height / 2
                    - (p.descent() + p.ascent()) / 2 - 10), p
        )
        return bmp
    }

    private fun findWord(
        str: String,
        offs: Int
    ): Int { // when you touch ' ', this method returns left word.
        var offset = offs
        if (str.length == offset) {
            offset-- // without this code, you will get exception when touching end of the text
        }
        if (str[offset] == ' ') {
            offset--
        }
        var startIndex = offset
        try {
            while (str[startIndex] != ' ' && str[startIndex] != '\n') {
                startIndex--
            }
        } catch (e: StringIndexOutOfBoundsException) {
            startIndex = 0
        }
        return startIndex
    }

    override fun onPause() {
        super.onPause()
        if (this::originalText.isInitialized)
            binding.ayah.text = originalText

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