package ru.burtelov.currencyexchange.ui.history

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModel
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModelFactory
import ru.burtelov.currencyexchange.databinding.FragmentHistoryBinding
import ru.burtelov.currencyexchange.ui.MainApplication
import ru.burtelov.currencyexchange.ui.history.adapter.HistoryAdapter
import kotlinx.coroutines.launch
import ru.burtelov.currencyexchange.ui.filter.FilterFragment
import java.time.LocalDate
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private lateinit var mainViewModel: AppViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        setHasOptionsMenu(true)

        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val repository = (activity?.application as MainApplication).appRepository

        mainViewModel =
            ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        adapter = HistoryAdapter()

        loadHistory()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.historyRecycler.layoutManager = layoutManager
        binding.historyRecycler.adapter = adapter

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun loadHistory() {
        val calendar = Calendar.getInstance(Locale.ITALY)

        val month = calendar.get(Calendar.MONTH)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        lifecycleScope.launch {
            mainViewModel.getHistory().observe(viewLifecycleOwner) { l ->

                when (FilterFragment.filterTitle) {
                    1 -> {
                        adapter.historyList = l.sortedByDescending { it.date }
//                        println("Всё ${FilterFragment.filterTitle}")
                    }
                    2 -> {
                        adapter.historyList =
                            l.filter { comparisonOfWeeks(calendar, week, year, it.date) }
                                .sortedByDescending { it.date }
//                        println("Недели ${FilterFragment.filterTitle}")
                    }
                    3 -> {
                        adapter.historyList =
                            l.filter { comparisonOfMonths(calendar, month, year, it.date) }
                                .sortedByDescending { it.date }
//                        println("Месяц ${FilterFragment.filterTitle}")
                    }
                    else -> {
                        adapter.historyList = l.filter { comparisonOfPeriod(calendar, it.date) }
                            .sortedByDescending { it.date }
//                        println("Период ${FilterFragment.filterTitle}")
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comparisonOfWeeks(
        calendar: Calendar,
        week: Int,
        year: Int,
        date: String,
    ): Boolean {
        val parsedDate = LocalDate.parse(date)

        calendar.set(parsedDate.year, parsedDate.monthValue - 1, parsedDate.dayOfMonth)

        return calendar.get(Calendar.WEEK_OF_YEAR) == week &&
                calendar.get(Calendar.YEAR) == year
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comparisonOfMonths(
        calendar: Calendar,
        month: Int,
        year: Int,
        date: String,
    ): Boolean {
        val parsedDate = LocalDate.parse(date)
        calendar.set(parsedDate.year, parsedDate.monthValue - 1, parsedDate.dayOfMonth)

        return calendar.get(Calendar.MONTH) == month &&
                calendar.get(Calendar.YEAR) == year
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun comparisonOfPeriod(calendar: Calendar, date: String): Boolean {
        val startDateParsed = LocalDate.parse(getStartDate())
        val finishDateParsed = LocalDate.parse(getFinishDate())
        val comparisonDateParsed = LocalDate.parse(date)


        val startDate = calendar.clone() as Calendar
        startDate.set(startDateParsed.year,
            startDateParsed.monthValue - 1,
            startDateParsed.dayOfMonth)

        val comparisonDate = calendar.clone() as Calendar
        comparisonDate.set(comparisonDateParsed.year,
            comparisonDateParsed.monthValue - 1,
            comparisonDateParsed.dayOfMonth)

        val finishDate = calendar.clone() as Calendar
        finishDate.set(finishDateParsed.year,
            finishDateParsed.monthValue - 1,
            finishDateParsed.dayOfMonth)

        return comparisonDate in startDate..finishDate
    }

    companion object {
        private var startDate = ""
        private var finishDate = ""

        fun setStartDate(date: String) {
            startDate = date
        }

        fun setFinishDate(date: String) {
            finishDate = date
        }

        private fun getStartDate(): String {
            return startDate
        }

        private fun getFinishDate(): String {
            return finishDate
        }
    }
}