package ru.burtelov.currencyexchange.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Таблица в room database с курсами валют
 */
@Entity(tableName = "currencies")
data class Currencies(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val date: String,
    val timestamp: Int,
    val base: String,
    val rates: Map<String, Double>
)
