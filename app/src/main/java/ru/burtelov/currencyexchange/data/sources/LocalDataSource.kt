package ru.burtelov.currencyexchange.data.sources

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.burtelov.currencyexchange.data.convertor.RatesConverter
import ru.burtelov.currencyexchange.data.dao.AppDAO
import ru.burtelov.currencyexchange.data.model.History
import ru.burtelov.currencyexchange.data.model.Currencies
import ru.burtelov.currencyexchange.data.model.LikedCurrencies

/**
 * Room Database класс
 */
@Database(entities = [Currencies::class, History::class, LikedCurrencies::class], version = 4)
@TypeConverters(RatesConverter::class)
abstract class LocalDataSource : RoomDatabase() {

    abstract fun currencyDAO() : AppDAO

    companion object{
        @Volatile
        private var INSTANCE: LocalDataSource? = null

        fun getDatabase(context: Context): LocalDataSource {
            //context.applicationContext.deleteDatabase("localDB"); // УДАЛЯЕМ ДБ ПОСЛЕ СХЕМЫ
            if (INSTANCE == null) {
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context,
                        LocalDataSource::class.java,
                        "localDB")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}