package ru.burtelov.currencyexchange.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likedCurrencies")
data class LikedCurrencies(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val price: Double,
    val is_liked: Boolean
)