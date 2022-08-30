package com.crazyidea.alsalah.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.getSpans
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.crazyidea.alsalah.App
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.databinding.FragmentQuranPageBinding
import com.crazyidea.alsalah.ui.quickaction.ActionItem
import com.crazyidea.alsalah.ui.quickaction.QuickAction
import com.crazyidea.alsalah.ui.quickaction.QuickAction.OnActionItemClickListener

import com.crazyidea.alsalah.utils.getJuzName
import com.crazyidea.alsalah.utils.indexesOf
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"

// action id
private const val ID_ITEM_COPY = 1
private const val ID_ITEM_BOOKMARK = 2
private const val ID_ITEM_PLAY = 3

@AndroidEntryPoint
class QuranPageFragment : Fragment() {


    private var bookmarked: Boolean = false
    private lateinit var highlightedText: String
    private var ayahId: Int = 0
    private var isPLAYING: Boolean = false
    private lateinit var mp: MediaPlayer
    private var startSpace: Int = 0
    private var end: Int = 0
    private val selectIdx = 0

    private lateinit var mQuickAction: QuickAction
    private lateinit var originalText: SpannableStringBuilder
    private var longClicked: Boolean = false
    private var pageNum: Int = 0
    private var _binding: FragmentQuranPageBinding? = null
    private val sharedViewModel by viewModels<SharedQuranViewModel>({ requireActivity() })
    private val viewModel by viewModels<QuranViewModel>()

    lateinit var ayatTV: TextView


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
        setupObservers()
        binding.juz.setOnClickListener {
            sharedViewModel.openDrawer.value = true
        }
        binding.surah.setOnClickListener {
            sharedViewModel.openDrawer.value = true
        }

        binding.bookmarkPage.setOnClickListener {
            if (!bookmarked) {
                binding.bookmarkPage.setImageResource(R.drawable.ic_bookmarked)
            } else {
                binding.bookmarkPage.setImageResource(R.drawable.ic_border_bookmark)
            }
            viewModel.bookmarkPage(pageNum.toLong())

        }
        binding.ayah.setOnTouchListener { v, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    if (longClicked) {
                        val mOffset =
                            binding.ayah.getOffsetForPosition(motionEvent.x, motionEvent.y)
                        highlightText(
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
        binding.ayah.setOnLongClickListener {
            longClicked = true
            false
        }

    }

    private fun setupObservers() {
        if (bookmarked) {
            binding.bookmarkPage.setImageResource(R.drawable.ic_bookmarked)
        } else {
            binding.bookmarkPage.setImageResource(R.drawable.ic_border_bookmark)
        }

        sharedViewModel.khatma.observe(viewLifecycleOwner) {
            if (it.read.plus(1) == pageNum) {
                binding.khatmaMark.visibility = VISIBLE
                binding.endKhatmaMark.visibility = GONE
            } else {
                if (it.pages_num == 0) {

                    val todayCalendar = Calendar.getInstance()

                    val differenceMillis = todayCalendar.timeInMillis - it.time!!
                    val daysDifference = (differenceMillis / (1000 * 60 * 60 * 24)).toInt()
                    val pages = (604 - it.read) / (it.days - daysDifference + 1)
                    if (it.read.plus(pages) == pageNum) {
                        binding.endKhatmaMark.visibility = VISIBLE
                        binding.khatmaMark.visibility = GONE
                    } else {
                        binding.khatmaMark.visibility = GONE
                        binding.endKhatmaMark.visibility = GONE
                    }
                } else {
                    if (it.read.plus(it.pages_num) == pageNum) {
                        binding.endKhatmaMark.visibility = VISIBLE
                        binding.khatmaMark.visibility = GONE
                    } else {
                        binding.khatmaMark.visibility = GONE
                        binding.endKhatmaMark.visibility = GONE
                    }
                }

            }

        }
        sharedViewModel.bookmarks.observe(viewLifecycleOwner) {
            if (it != null)
                if (it.any { it.bookmark.page == pageNum.toLong() }) {
                    bookmarked = true
                    binding.bookmarkPage.setImageResource(R.drawable.ic_bookmarked)
                }
        }
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
                    ForegroundColorSpan(
                        resources.getColor(R.color.bw, requireActivity().theme)
                    ),
                    spannable.length - newText.length, spannable.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                val numberAya = " \u06DD${
                    String.format(
                        App.instance.getAppLocale(),
                        "%d", it.number
                    )
                } "
                spannable.append(
                    numberAya
                )
                if (it.bookmarked) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.RED),
                        spannable.length - numberAya.length, spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#58452f")),
                        spannable.length - numberAya.length, spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
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
                App.instance.getAppLocale(),
                "%d", it.first().page
            )
        }


        viewModel.ayahContent.observe(viewLifecycleOwner) {
            if (isPLAYING) {
                val tracks = mQuickAction.mRootView.findViewById<ViewGroup>(R.id.tracks)
                val playImg = (tracks
                    .getChildAt(4) as ImageView)
                playImg.setImageResource(R.drawable.ic_baseline_stop_24)
                mp = MediaPlayer()
                try {
                    mp.setDataSource(it)
                    mp.prepare()
                    mp.start()
                    mp.setOnCompletionListener {
                        isPLAYING = false
                        playImg.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                } catch (e: IOException) {
                    Timber.e("prepare() failed")
                }
            }
        }
    }

    private fun highlightText(indexClicked: Int) {
        if (!this::originalText.isInitialized)
            originalText = SpannableStringBuilder(binding.ayah.text)
        else
            binding.ayah.text = originalText
        val sb = SpannableStringBuilder(binding.ayah.text)
        val ayat = sb.indexesOf("\u06DD")
        val spaces = sb.indexesOf(" ")
        val start: Int = ayat.findLast {
            indexClicked > it
        } ?: 0
        startSpace = if (start == 0)
            0
        else
            spaces.find {
                it > start
            } ?: 0

        end = ayat.find {
            indexClicked < it
        } ?: 0

        val x = spaces.find {
            it > end
        } ?: 0
        val ayahIdSt = sb.substring(end, x)
        ayahId = ayahIdSt.replace("\u06DD", "").toInt()
        highlightedText = sb.substring(
            startSpace,
            end
        ).trim()
        if (highlightedText.contains("﷽\n"))
            highlightedText = highlightedText.substring("﷽\n".length + 1).trim()
        sb.setSpan(
            BackgroundColorSpan(Color.parseColor("#E4DECF")),
            startSpace,
            end,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.ayah.text = sb
        val lineOfText = binding.ayah.layout.getLineForOffset(startSpace)
        val yCoord = binding.ayah.layout.getLineTop(lineOfText)
        mQuickAction.show(binding.ayah, 0f, yCoord)
    }


    private fun initQuickActions() {

        mQuickAction = QuickAction(requireContext())
        mQuickAction.setOnActionItemClickListener(mActionClick)

        val copyItem = ActionItem(
            ID_ITEM_COPY,
            "Copy",
            resources.getDrawable(R.drawable.ic_baseline_content_copy_24),
            selectIdx
        )
        val bookmarkItem = ActionItem(
            ID_ITEM_BOOKMARK,
            "Bookmark",
            resources.getDrawable(R.drawable.ic_baseline_bookmark_add_24),
            selectIdx
        )
        val playItem = ActionItem(
            ID_ITEM_PLAY,
            "Play",
            resources.getDrawable(R.drawable.ic_baseline_play_arrow_24),
            selectIdx
        )
        playItem.isSticky = true
        mQuickAction.addActionItem(bookmarkItem) // add action items into QuickAction.
        mQuickAction.addActionItem(copyItem) // add action items into QuickAction.
        mQuickAction.addActionItem(playItem) // add action items into QuickAction.

    }


    private val mActionClick: OnActionItemClickListener = object : OnActionItemClickListener {
        override fun onItemClick(source: QuickAction, pos: Int, actionId: Int, selectIdx: Int) {
            when (actionId) {
                ID_ITEM_COPY -> {
                    // Copy highlighted text to clipboard
                    val clipboard: ClipboardManager =
                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip =
                        ClipData.newPlainText("label", originalText.substring(startSpace, end))
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(requireContext(), getString(R.string.copied), Toast.LENGTH_SHORT)
                        .show()
                    mQuickAction.dismiss()
                }
                ID_ITEM_PLAY -> {

                    if (!isPLAYING) {
                        isPLAYING = true
                        viewModel.getAudio(highlightedText, ayahId, pageNum)
                    } else {
                        isPLAYING = false
                        val tracks = mQuickAction.mRootView.findViewById<ViewGroup>(R.id.tracks)
                        val playImg = (tracks
                            .getChildAt(4) as ImageView)
                        playImg.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        stopPlaying()
                    }
                }
                ID_ITEM_BOOKMARK -> {
                    viewModel.bookmarkAya(highlightedText, ayahId, pageNum)
                    binding.ayah.text = originalText
                    val spannable = SpannableStringBuilder(binding.ayah.text)
                    val textAfterEnd = spannable.substring(end)
                    val nextSpace = textAfterEnd.indexOfFirst {
                        it == ' '
                    }
                    val oldSpan = spannable.getSpans<ForegroundColorSpan>(end, end + nextSpace)
                    if (oldSpan.any { it.foregroundColor == Color.RED })
                        spannable.setSpan(
                            ForegroundColorSpan(Color.parseColor("#58452f")),
                            end, end + nextSpace,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    else
                        spannable.setSpan(
                            ForegroundColorSpan(Color.RED),
                            end, end + nextSpace,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    originalText = spannable
                    binding.ayah.text = spannable
                }
            }
        }
    }

    private fun stopPlaying() {
        if (this::mp.isInitialized)
            mp.release()
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
        p.color = resources.getColor(R.color.bw)
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
        if (isPLAYING) {
            stopPlaying()
        }
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