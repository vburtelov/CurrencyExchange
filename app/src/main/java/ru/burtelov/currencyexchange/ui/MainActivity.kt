package ru.burtelov.currencyexchange.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.burtelov.currencyexchange.R
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModel
import ru.burtelov.currencyexchange.data.viewmodel.AppViewModelFactory
import ru.burtelov.currencyexchange.databinding.ActivityMainBinding
import ru.burtelov.currencyexchange.ui.detail.DetailFragment
import ru.burtelov.currencyexchange.ui.graph.GraphFragment
import ru.burtelov.currencyexchange.ui.history.HistoryFragment
import ru.burtelov.currencyexchange.ui.currency.CurrencyFragment
import ru.burtelov.currencyexchange.ui.filter.FilterFragment

interface HistoryInterface {
    fun openCurrencyWindow()
    fun openFilterWindow()
    fun closeFilterWindow()
}

interface CurrencyInterface {
    fun openDetailWindow()
}

interface DetailInterface {
    fun closeDetailWindow()
}

class MainActivity : AppCompatActivity(), HistoryInterface, CurrencyInterface, DetailInterface {

    private lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: AppViewModel

    private val detail = DetailFragment()
    private val currency = CurrencyFragment()
    private val history = HistoryFragment()
    private val graphic = GraphFragment()
    private val filter = FilterFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startDate = findViewById<EditText>(R.id.start_date)
        val finishDate = findViewById<EditText>(R.id.finish_date)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.navBar.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.currency -> replaceFragment(currency)
                R.id.history -> replaceFragment(history)
                R.id.graphic -> replaceFragment(graphic)
            }
            true
        }
        loadScreen()

        val repository = (application as MainApplication).appRepository

        mainViewModel =
            ViewModelProvider(this, AppViewModelFactory(repository))[AppViewModel::class.java]

        setContentView(binding.root)
    }

    private fun loadScreen() {
        replaceFragment(currency)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        when (fragment) {
            detail -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            history, filter -> supportActionBar?.title = "" +
                    "Фильтр: ${
                        when (FilterFragment.filterTitle) {
                            1 -> "Всё"
                            2 -> "Неделя"
                            3 -> "Месяц"
                            else -> "Выбранный период"
                        }
                    }"
            else -> {
                supportActionBar?.title = getString(R.string.app_name)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }

        transaction.replace(binding.fragmentContainer.id, fragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu_filter, menu)
        return false

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> openCurrencyWindow()
            R.id.filter -> openFilterWindow()
        }
        return true
    }

    override fun openCurrencyWindow() {
        replaceFragment(currency)
    }

    override fun openDetailWindow() {
        replaceFragment(detail)
    }

    override fun closeDetailWindow() {
        replaceFragment(currency)
    }

    override fun closeFilterWindow() {
        replaceFragment(history)
    }

    override fun openFilterWindow() {
        replaceFragment(filter)
    }
}