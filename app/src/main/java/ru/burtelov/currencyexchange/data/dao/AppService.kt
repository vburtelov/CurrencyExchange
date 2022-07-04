package ru.burtelov.currencyexchange.data.dao

import ru.burtelov.currencyexchange.data.model.Currencies
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Хард линк до ручки с курсами валют, простите!!111!
 */
interface AppService {
    @Headers("Content-type: application/json", "Connection: close", "Accept-Encoding: identity")
    @GET("./latest?apikey=AYrKB1ePdRygCE9rjpFH3isMVeGkYuNa&format=1")
    suspend fun getLatestCurrency(): Currencies
}