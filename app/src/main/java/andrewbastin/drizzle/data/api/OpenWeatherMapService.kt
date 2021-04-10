package andrewbastin.drizzle.data.api

import andrewbastin.drizzle.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {

    @GET("/data/2.5/weather?appid=${BuildConfig.OWM_API_KEY}")
    suspend fun fetchWeatherData(@Query("q") location: String): DailyWeatherData

}