package ru.burtelov.currencyexchange.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ru.burtelov.currencyexchange.data.model.History
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModel
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModelFactory
import ru.burtelov.currencyexchange.databinding.FragmentDetailBinding
import ru.burtelov.currencyexchange.ui.MainApplication
import kotlinx.coroutines.launch
import ru.burtelov.currencyexchange.ui.DetailInterface
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var mainViewModel: AppViewModel
    private val df = DecimalFormat("#.####")

    override fun onResume() {
        super.onResume()
        binding.defaultCurrencyValue.setText("1")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentDetailBinding.inflate(inflater, container, false)

        val repository = (activity?.application as MainApplication).appRepository

        mainViewModel =
            ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        exchangeButtonClickEvent()

        binding.apply {
            selectedCurrencyName.text = getCurrency()
            selectedCurrencyValue.setText(df.format(getRate()))
            defaultCurrencyValue.addTextChangedListener(textWatcherDefaultCurrency)
            selectedCurrencyValue.addTextChangedListener(textWatcherSelectedCurrency)
        }

        return binding.root
    }

    val textWatcherDefaultCurrency: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.selectedCurrencyValue.removeTextChangedListener(textWatcherSelectedCurrency)
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val resultSelectedCurrency=
                if (s!!.isEmpty()) "0"
                else df.format(s.toString().toDouble() * getRate())

            binding.exchangeButton.isEnabled = s.toString().isNotEmpty()
            binding.selectedCurrencyValue.setText(resultSelectedCurrency)
        }

        override fun afterTextChanged(s: Editable?) {
            binding.selectedCurrencyValue.addTextChangedListener(textWatcherSelectedCurrency)
        }
    }

    val textWatcherSelectedCurrency: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.defaultCurrencyValue.removeTextChangedListener(textWatcherDefaultCurrency)
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val resultDefaultCurrency =
                if (s!!.isEmpty()) "0"
                else df.format((s.toString().toDouble()) / getRate())

            binding.exchangeButton.isEnabled = s.toString().isNotEmpty()
            binding.defaultCurrencyValue.setText(resultDefaultCurrency)
        }

        override fun afterTextChanged(s: Editable?) {
            binding.defaultCurrencyValue.addTextChangedListener(textWatcherDefaultCurrency)
        }
    }

    @SuppressLint("NewApi")
    private fun exchangeButtonClickEvent() {
        binding.exchangeButton.setOnClickListener {
            val item = History(
                0,
                binding.defaultCurrencyName.text.toString(),
                getCurrency(),
                binding.defaultCurrencyValue.text.toString(),
                binding.selectedCurrencyValue.text.toString(),
                DateTimeFormatter
                    .ofPattern("yyyy-MM-dd")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
            )
            lifecycleScope.launch {
                mainViewModel.setHistory(item)
                val detailInterface = requireActivity() as DetailInterface
                detailInterface.closeDetailWindow()
            }
        }
    }

    companion object {
        private lateinit var currentCurrency: String
        private var currentRate: Double = 0.0
        private lateinit var currentIco: String


        fun getCurrency(): String {
            return currentCurrency
        }

        fun setCurrency(name: String) {
            currentCurrency = name
        }

        fun getRate(): Double {
            return currentRate
        }

        fun setRate(rate: Double) {
            currentRate = rate
        }
    }
}