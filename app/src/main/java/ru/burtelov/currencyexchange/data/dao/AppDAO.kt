package ru.burtelov.currencyexchange.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.burtelov.currencyexchange.data.model.History
import ru.burtelov.currencyexchange.data.model.Currencies
import ru.burtelov.currencyexchange.data.model.LikedCurrencies

@Dao
interface AppDAO {

    @Query("DELETE FROM currencies")
    fun nukeTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: Currencies)

    @Query("SELECT * FROM currencies")
    suspend fun getCurrencies() : Currencies

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(historyElement: History)

    @Query("SELECT * FROM history")
    suspend fun getHistory() : List<History>

    @Insert()
    suspend fun insertLikedCurrencies(likedCurrenciesElement: MutableList<LikedCurrencies>)

    @Query("SELECT * FROM likedCurrencies ORDER BY is_liked DESC")
    suspend fun getLikedCurrencies() : List<LikedCurrencies>

    @Query("UPDATE likedCurrencies SET is_liked = :is_liked WHERE name = :name")
    suspend fun updateLikedCurrenciesLike(is_liked: Boolean, name: String)

    @Query("UPDATE likedCurrencies SET price = :price WHERE name = :name")
    suspend fun updateLikedCurrenciesPrice(price: Double, name: String)
}