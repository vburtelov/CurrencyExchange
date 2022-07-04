package ru.burtelov.currencyexchange.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.burtelov.currencyexchange.data.dao.AppService
import ru.burtelov.currencyexchange.data.model.History
import ru.burtelov.currencyexchange.data.model.Currencies
import ru.burtelov.currencyexchange.data.model.LikedCurrencies
import ru.burtelov.currencyexchange.data.sources.LocalDataSource

/**
 * Репозиторий приложения
 */
class AppRepository
    (
    private val appService: AppService,
    private val localDataSource: LocalDataSource,
    private val applicationContext: Context
) {

    private val currenciesLiveData = MutableLiveData<Currencies>()
    private val historyLiveData = MutableLiveData<List<History>>()
    private val likedCurrenciesData = MutableLiveData<List<LikedCurrencies>>()

    /**
     * Получаем историю из БД и постим в лайв-дату
     */

    val history: LiveData<List<History>>
        get() = historyLiveData

    val currencies: LiveData<Currencies>
        get() = currenciesLiveData

    val likedCurrencies: LiveData<List<LikedCurrencies>>
        get() = likedCurrenciesData


    /**
     * Получаем историю обменов валют из БД и постим в лайв-дату
     */
    suspend fun getHistory() {
        val history = localDataSource.currencyDAO().getHistory()
        historyLiveData.postValue(history)
    }

    /**
     * Сохраняем историю обменов валют в БД и постим в лайв-дату
     */
    suspend fun setHistory(item: History) {
        localDataSource.currencyDAO().insertHistory(item)
        getHistory()
    }

    /**
     * Получаем курсы валют в зависимости от наличия интернета.
     *
     * Если подключение есть - дёргаем за ручку
     *
     * Если подключение отсутствует - получаем данные из БД
     */
    suspend fun getCurrencies() {
        if (isInternetAvailable(applicationContext)) {
            val result = appService.getLatestCurrency()
            localDataSource.currencyDAO().insertCurrencies(result)
            currenciesLiveData.postValue(result)
        } else {
            val currencies = localDataSource.currencyDAO().getCurrencies()
            currenciesLiveData.postValue(currencies)
        }
    }

    suspend fun getLikedCurrencies(){
        if (isInternetAvailable(applicationContext)) {
            val result = appService.getLatestCurrency()
            val _array = runCatching { result }
            var likedCurrenciesList: MutableList<LikedCurrencies> = mutableListOf()
            _array.onSuccess {
                if (localDataSource.currencyDAO().getLikedCurrencies().isEmpty()) {
                    it.rates.forEach {
                        if (it.key != "XDR") {
                            likedCurrenciesList.add(LikedCurrencies(0, it.key, it.value, false))
                        }
                    }
                    localDataSource.currencyDAO().insertLikedCurrencies(likedCurrenciesList)
                }else{
                    it.rates.forEach {
                        if (it.key != "XDR") {
                            localDataSource.currencyDAO().updateLikedCurrenciesPrice(it.value, it.key)
                        }
                    }
                    likedCurrenciesList = localDataSource.currencyDAO().getLikedCurrencies() as MutableList<LikedCurrencies>
                }
            }
            likedCurrenciesData.postValue(likedCurrenciesList)

        } else {
            val currencies = localDataSource.currencyDAO().getLikedCurrencies()
            likedCurrenciesData.postValue(currencies)
        }
    }

    suspend fun likeCurrency(name: String){
        localDataSource.currencyDAO().updateLikedCurrenciesLike(true, name)
    }

    suspend fun dislikeCurrency(name: String){
        localDataSource.currencyDAO().updateLikedCurrenciesLike(false, name)
    }

    /**
     * Функция для проверки подключения к интернету
     */
    companion object {

        fun isInternetAvailable(context: Context): Boolean {
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    ) ?: false
                } else {
                    (@Suppress("DEPRECATION")
                    return this.activeNetworkInfo?.isConnected ?: false)
                }
            }
        }
    }





}