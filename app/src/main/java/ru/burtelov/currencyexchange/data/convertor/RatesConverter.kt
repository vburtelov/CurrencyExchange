package ru.burtelov.currencyexchange.data.convertor

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RatesConverter {
    /**
     * Конвертируем курсы валют из мапы в JSON
     */
    @TypeConverter
    fun fromRate(rates: Map<String, Double>): String {
        return Gson().toJson(rates)
    }

    /**
     * Конвертируем курсы валют из JSON в мапу
     */
    @TypeConverter
    fun toRate(rates: String): Map<String, Double> {
        val listType = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(rates, listType)
    }
}