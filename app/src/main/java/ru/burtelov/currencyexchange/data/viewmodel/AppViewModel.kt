package ru.burtelov.currencyexchange.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.burtelov.currencyexchange.data.model.History
import ru.burtelov.currencyexchange.data.model.Currencies
import ru.burtelov.currencyexchange.data.model.LikedCurrencies
import ru.burtelov.currencyexchange.data.repository.AppRepository

/**
 * ViewModel приложения
 */
class AppViewModel(private val repository: AppRepository) : ViewModel() {
    /**
     * Получаем курсы валют из БД или с FIXER IO
     *
     * Возвращаем курсы валют
     */
    suspend fun getCurrencies(): LiveData<Currencies> {
        repository.getCurrencies()
        return repository.currencies
    }
    /**
     * Получаем историю обменов из БД
     *
     * Возвращаем историю обменов
     */
    suspend fun getHistory(): LiveData<List<History>>{
        repository.getHistory()
        return repository.history
    }
    /**
     * Добавляем обменов в историю
     */
    suspend fun setHistory(item: History){
        repository.setHistory(item)
    }

    suspend fun getLikedCurrencies(): LiveData<List<LikedCurrencies>> {
        repository.getLikedCurrencies()
        return repository.likedCurrencies
    }

    suspend fun likeCurrency(name: String){
        repository.likeCurrency(name)
    }

    suspend fun dislikeCurrency(name: String){
        repository.dislikeCurrency(name)
    }

}