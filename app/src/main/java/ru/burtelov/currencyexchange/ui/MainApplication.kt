package ru.burtelov.currencyexchange.ui

import android.app.Application
import ru.burtelov.currencyexchange.data.dao.AppService
import ru.burtelov.currencyexchange.data.repository.AppRepository
import ru.burtelov.currencyexchange.data.sources.LocalDataSource
import ru.burtelov.currencyexchange.data.sources.RemoteDataSource

class MainApplication : Application() {

    lateinit var appRepository: AppRepository
    /**
     * Точка входа в приложение
     */
    override fun onCreate() {
        super.onCreate()
        initialize()
    }
    /**
     * Инициализируем БД и Retrofit и заполняем репозиторий
     */
    private fun initialize() {
        val quoteService = RemoteDataSource.getInstance().create(AppService::class.java)
        val database = LocalDataSource.getDatabase(applicationContext)
        appRepository = AppRepository(quoteService, database, applicationContext)
    }
}