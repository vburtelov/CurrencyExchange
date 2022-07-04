package ru.burtelov.currencyexchange.ui.graph

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ru.burtelov.currencyexchange.R
import ru.burtelov.currencyexchange.databinding.FragmentGraphBinding
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModelFactory
import ru.burtelov.currencyexchange.ui.MainApplication
import ru.burtelov.currencyexchange.data.model.Currency
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class GraphFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var mainViewModel: AppViewModel
    private lateinit var binding: FragmentGraphBinding

    private var graphList: MutableList<Currency> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGraphBinding.inflate(inflater, container, false)

        val repository = (activity?.application as MainApplication).appRepository

        mainViewModel =
            ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        barChart = binding.barChart

        buildGraph()

        return binding.root

    }

    private fun initBarChart() {

        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        val yAxis: YAxis = barChart.axisLeft
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        barChart.axisRight.isEnabled = false
        barChart.setDrawValueAboveBar(false)
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setExtraOffsets(15f, 0f, 15f, 10f)
        barChart.animateY(1000)
        barChart.setVisibleXRange(10f, 10f)
        barChart.setTouchEnabled(false)

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 20f
        yAxis.textSize = 20f
        xAxis.textColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val labels = mutableListOf("")
        for (i in graphList.indices) {
            labels.add(graphList[i].name)
        }

        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f

    }

    private fun buildGraph() {
        lifecycleScope.launch {
            val currencyList: MutableList<Currency> = mutableListOf()
            lifecycleScope.launch {
                mainViewModel.getCurrencies().observe(viewLifecycleOwner, Observer { it ->
                    if (currencyList.isNotEmpty()) return@Observer
                    it.rates.forEach {
                        if (it.key == "RUB" ||
                            it.key == "USD" ||
                            it.key == "UAH" ||
                            it.key == "JEP" ||
                            it.key == "BYN" ||
                            it.key == "GBP"
                        ) {
                            currencyList.add(Currency(it.key, it.value))
                        }
                    }
                    graphList = currencyList
                    barChart.notifyDataSetChanged()
                    barChart.invalidate()

                    initBarChart()

                    val entries = ArrayList<BarEntry>()

                    for (i in graphList.indices) {
                        entries.add(BarEntry((i + 1).toFloat(), graphList[i].currency.toFloat()))
                    }

                    val barDataSet = BarDataSet(entries, "")
                    barDataSet.setColors(
                        Color.rgb(87, 86, 83),
                        Color.rgb(68, 68, 68),
                        Color.rgb(135, 134, 131),
                        Color.rgb(190, 190, 190),
                        Color.rgb(68, 68, 68),
                        Color.rgb(87, 86, 83),
                    )
                    barDataSet.valueTextSize = 18f
                    barDataSet.valueTextColor = Color.WHITE
                    val data = BarData(barDataSet)
                    barChart.data = data
                    barChart.invalidate()
                })
            }
        }
    }
}