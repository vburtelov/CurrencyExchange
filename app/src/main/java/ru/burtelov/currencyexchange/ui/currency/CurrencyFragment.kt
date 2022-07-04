package ru.burtelov.currencyexchange.ui.currency

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.burtelov.currencyexchange.data.model.Currency
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModel
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModelFactory
import ru.burtelov.currencyexchange.databinding.FragmentCurrencyBinding
import ru.burtelov.currencyexchange.ui.MainApplication
import ru.burtelov.currencyexchange.ui.CurrencyInterface
import ru.burtelov.currencyexchange.ui.currency.adapter.CurrencyAdapter
import kotlinx.coroutines.launch
import ru.burtelov.currencyexchange.data.model.LikedCurrencies
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

interface CurrencyAdapterInterface {
    fun openDetailWindow()
    fun likeCurrency(name: String)
    fun dislikeCurrency(name: String)
}

class CurrencyFragment : Fragment() {

    private lateinit var binding: FragmentCurrencyBinding
    private lateinit var adapter: CurrencyAdapter
    private lateinit var mainViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCurrencyBinding.inflate(inflater, container, false)

        val repository = (activity?.application as MainApplication).appRepository

        mainViewModel =
            ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        val currencyInterface = requireActivity() as CurrencyInterface

        adapter = CurrencyAdapter(object : CurrencyAdapterInterface {
            override fun openDetailWindow() {
                currencyInterface.openDetailWindow()
            }

            override fun likeCurrency(name: String) {
                lifecycleScope.launch {
                    mainViewModel.likeCurrency(name)
                    loadCurrencies()
                }
            }

            override fun dislikeCurrency(name: String) {
                lifecycleScope.launch {
                    mainViewModel.dislikeCurrency(name)
                    loadCurrencies()
                }
            }

        })

        loadCurrencies()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.apply {
            currencyRecycler.layoutManager = layoutManager
            currencyRecycler.adapter = adapter
        }

        return binding.root

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun loadCurrencies() {
        lifecycleScope.launch {
            mainViewModel.getLikedCurrencies().observe(viewLifecycleOwner) {
                adapter.currencyList = it as MutableList<LikedCurrencies>
                adapter.notifyDataSetChanged()
            }
        }
    }
}