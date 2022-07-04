package ru.burtelov.currencyexchange.ui.filter

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.burtelov.currencyexchange.databinding.FragmentFilterBinding
import ru.burtelov.currencyexchange.ui.HistoryInterface
import ru.burtelov.currencyexchange.ui.history.HistoryFragment
import java.time.LocalDate
import java.util.*


class FilterFragment : Fragment() {
    private lateinit var binding: FragmentFilterBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        val historyInterface = requireActivity() as HistoryInterface

        val calendar = Calendar.getInstance(Locale.ITALY)
        val yearToday = calendar.get(Calendar.YEAR)
        val monthToday = calendar.get(Calendar.MONTH)
        val dayToday = calendar.get(Calendar.DAY_OF_MONTH)
        val today = "$yearToday-${zeroAdd(monthToday + 1)}-${zeroAdd(dayToday)}"

        binding.apply {

            allTime.setOnClickListener {
                filterTitle = 1
                if (onFilter.isChecked) disablePeriod()
                historyInterface.openFilterWindow()
            }
            week.setOnClickListener {
                filterTitle = 2
                if (onFilter.isChecked) disablePeriod()
                historyInterface.openFilterWindow()
            }
            month.setOnClickListener {
                filterTitle = 3
                if (onFilter.isChecked) disablePeriod()
                historyInterface.openFilterWindow()
            }

            onFilter.setOnCheckedChangeListener { _, _ ->
                if (onFilter.isChecked) {
                    startDate.setText(today)
                    finishDate.setText(today)

                    filterTitle = 4
                    startDate.addTextChangedListener(dateValidator)
                    finishDate.addTextChangedListener(dateValidator)
                    startDate.isEnabled = true
                    finishDate.isEnabled = true
                } else {
                    if (filterTitle == 4) filterTitle = 1
                    startDate.removeTextChangedListener(dateValidator)
                    finishDate.removeTextChangedListener(dateValidator)
                    startDate.isEnabled = false
                    finishDate.isEnabled = false
                    startDate.setText("")
                    finishDate.setText("")
                }
                historyInterface.openFilterWindow()
            }

            calendarStart.isVisible = false
            calendarStart.firstDayOfWeek = 2
            calendarFinish.isVisible = false
            calendarFinish.firstDayOfWeek = 2


            startDateLayout.setStartIconOnClickListener {
                calendarStart.isVisible = !calendarStart.isVisible && startDate.isFocused
                calendarFinish.isVisible = false

                if (startDate.text?.length == 10) {
                    val dateStartInputParsed = LocalDate.parse(startDate.text)
                    val dateStartInput = calendar.clone() as Calendar

                    dateStartInput.set(dateStartInputParsed.year,
                        dateStartInputParsed.monthValue - 1,
                        dateStartInputParsed.dayOfMonth)
                    calendarStart.date = dateStartInput.timeInMillis
                } else calendarStart.date = calendar.timeInMillis
            }

            finishDateLayout.setStartIconOnClickListener {
                calendarStart.isVisible = false
                calendarFinish.isVisible = !calendarFinish.isVisible && finishDate.isFocused

                if (finishDate.text?.length == 10) {
                    val dateFinishInputParsed = LocalDate.parse(finishDate.text)
                    val dateFinishInput = calendar.clone() as Calendar

                    dateFinishInput.set(dateFinishInputParsed.year,
                        dateFinishInputParsed.monthValue - 1,
                        dateFinishInputParsed.dayOfMonth)
                    calendarFinish.date = dateFinishInput.timeInMillis
                } else calendarFinish.date = calendar.timeInMillis
            }

            calendarStart.setOnDateChangeListener { _, year: Int, month: Int, day: Int ->
                val date = "$year-${zeroAdd(month + 1)}-${zeroAdd(day)}"

                startDate.setText(date)
                calendarStart.isVisible = !calendarStart.isVisible

            }
            calendarFinish.setOnDateChangeListener { _, year: Int, month: Int, day: Int ->
                val date = "$year-${zeroAdd(month + 1)}-${zeroAdd(day)}"

                finishDate.setText(date)
                calendarFinish.isVisible = !calendarFinish.isVisible
            }

            applyButton.setOnClickListener {
                if (onFilter.isChecked &&
                    startDate.text?.length == 10 &&
                    finishDate.text?.length == 10
                ) {

                    val testStartDate = LocalDate.parse(startDate.text)
                    val testFinishDate = LocalDate.parse(finishDate.text)

                    if (testStartDate <= testFinishDate) {
                        HistoryFragment.setStartDate(startDate.text.toString())
                        HistoryFragment.setFinishDate(finishDate.text.toString())
                        historyInterface.closeFilterWindow()

                    } else {
                        startDateLayout.error = "Некорректный период"
                        finishDateLayout.error = "Некорректный период"
                    }

                } else if (!onFilter.isChecked &&
                    filterTitle == 1 ||
                    filterTitle == 2 ||
                    filterTitle == 3
                )
                    historyInterface.closeFilterWindow()
                else {
                    startDateLayout.error = "Неверная длина даты"
                    finishDateLayout.error = "Неверная длина даты"
                }
            }
        }

        return binding.root
    }

    private fun zeroAdd(v: Int): String {
        return if (v < 10) "0$v"
        else "$v"
    }

    private val dateValidator: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.apply {
                startDateLayout.error = null
                finishDateLayout.error = null
                calendarStart.isVisible = false
                calendarFinish.isVisible = false

                applyButton.isEnabled =
                    startDate.text!!.isNotEmpty() && finishDate.text!!.isNotEmpty()
            }

        }

    }

    private fun disablePeriod() {
        binding.apply {
            startDate.removeTextChangedListener(dateValidator)
            finishDate.removeTextChangedListener(dateValidator)
            onFilter.isChecked = false
            startDate.isEnabled = false
            finishDate.isEnabled = false
            applyButton.isEnabled = true
            startDateLayout.error = null
            finishDateLayout.error = null
            startDate.setText("")
            finishDate.setText("")
        }
    }

    companion object {
        var filterTitle = 1
    }
}