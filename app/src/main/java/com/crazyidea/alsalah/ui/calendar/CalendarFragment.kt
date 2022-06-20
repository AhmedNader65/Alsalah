package com.crazyidea.alsalah.ui.calendar

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.Status
import com.crazyidea.alsalah.databinding.CalendarDayBinding
import com.crazyidea.alsalah.databinding.CalendarHeaderBinding
import com.crazyidea.alsalah.databinding.FragmentCalendarBinding
import com.crazyidea.alsalah.ui.menu.MenuViewModel
import com.crazyidea.alsalah.utils.daysOfWeekFromLocale
import com.crazyidea.alsalah.utils.setTextColorRes
import com.crazyidea.alsalah.utils.withSimpleAdapter
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.mrerror.calendarview.model.*
import com.mrerror.calendarview.ui.DayBinder
import com.mrerror.calendarview.ui.MonthHeaderFooterBinder
import com.mrerror.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val viewModel by viewModels<CalendarViewModel>()
    private var selectedDate: MyLocaleDate? = null
    private val pastMonths = 10
    private val futureMonths = 10

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuViewModel =
            ViewModelProvider(this).get(MenuViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = daysOfWeekFromLocale()
        setupObservers()
        //HIJRI
        setupCalendar(UmmalquraCalendar(), TYPE.HIJRI)
        binding.group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == R.id.hijri_calendar) {
                setupCalendar(UmmalquraCalendar(), TYPE.HIJRI)
            } else {
                setupCalendar(Calendar.getInstance(), TYPE.GREGORIAN)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            var ar = Locale("ar")
                            val monthText =
                                day.date.yearMonth.getDisplayName(
                                    Calendar.MONTH,
                                    Calendar.LONG,
                                    ar
                                )

                            val yearText = "${day.date.yearMonth.get(Calendar.YEAR)}"
                            val dayText = "${day.date.yearMonth.get(Calendar.DAY_OF_MONTH)}"

                            val text =
                                "<font color=\"#EEB34B\">$dayText</font> <font color=\"#FFFFFF\">$monthText</font> <font color=\"#EEB34B\">$yearText</font>"
                            viewModel.fetchEventsData("${dayText}_$monthText")

                            this@CalendarFragment.binding.eventDate.text =
                                HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@CalendarFragment.binding
                            binding.calendarView.notifyDateChanged(day.date)
                            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
//                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }
        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.dayText
                val layout = container.binding.dayLayout
                textView.text = day.date.dayOfMonth.toString()

                val flightTopView = container.binding.dayEventTop
                val flightBottomView = container.binding.dayEventBottom
                flightTopView.background = null
                flightBottomView.background = null

                if (day.owner == DayOwner.THIS_MONTH) {

                    textView.setTextColorRes(R.color.black)
                    if (DateUtils.isToday(day.date.yearMonth.timeInMillis)) {
                        val monthText =
                            day.date.yearMonth.getDisplayName(
                                Calendar.MONTH,
                                Calendar.LONG,
                                Locale("ar")
                            )
                        val yearText = "${day.date.yearMonth.get(Calendar.YEAR)}"
                        val dayText = "${day.date.yearMonth.get(Calendar.DAY_OF_MONTH)}"
                        viewModel.fetchEventsData("${dayText}_$monthText")
                        val text =
                            "<font color=\"#EEB34B\">$dayText</font> <font color=\"#FFFFFF\">$monthText</font> <font color=\"#EEB34B\">$yearText</font>"
                        this@CalendarFragment.binding.eventDate.text =
                            HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

                        textView.setTextColorRes(R.color.white)

                        textView.setBackgroundResource(R.drawable.today_bg)
                    } else {
                        textView.setBackgroundResource(if (selectedDate == day.date) R.drawable.round_green_background else 0)

                    }
                } else {
                    textView.setTextColor(0xffffff)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout =
                CalendarHeaderBinding.bind(view).legendLayout.root.getChildAt(0) as LinearLayout
        }
        binding.calendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.calendar
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text =
                                daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale("ar"))
                        }
                    month.calendar
                }
            }
        }

        binding.calendarView.monthScrollListener = { month ->
            val ar = Locale("ar")
            val monthText =
                month.calendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    ar
                )

            val yearText = "${month.calendar.get(Calendar.YEAR)}"
            val dayText = "${month.calendar.get(Calendar.DAY_OF_MONTH)}"
            binding.monthText.text = monthText
            binding.yearText.text = yearText
            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.calendarView.notifyDateChanged(it)
//                updateAdapterForDate(null)
            }
        }

        binding.nextMonthImage.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.getNextMonth())
            }
        }

        binding.previousMonthImage.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.getPrevMonth())
            }
        }
    }

    private fun setupObservers() {
        viewModel.eventsData.observe(viewLifecycleOwner) {
            it?.let { data ->
                this@CalendarFragment.binding.eventsText.text =
                    HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY)

            }
        }
    }

    private fun setupCalendar(currentMonth: Calendar, type: TYPE) {

        binding.calendarView.setup(
            pastMonths, futureMonths, daysOfWeekFromLocale().first(),
            type
        )
        binding.calendarView.scrollToMonth(currentMonth)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}