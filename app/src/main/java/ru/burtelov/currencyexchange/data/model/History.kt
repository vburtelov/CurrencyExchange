package ru.burtelov.currencyexchange.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Таблица в room database для хранения истории обменов валют
 */
@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val from_currency_name: String,
    val to_currency_name: String,
    val from_currency_price: String,
    val to_currency_price: String,
    val date: String
)