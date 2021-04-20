package andrewbastin.drizzle.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class CompleteWeatherData(
    val current: DailyWeatherData,
    val forecast: ForecastData
)

class OpenWeatherMapRetriever {
    private val service: OpenWeatherMapService

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(OpenWeatherMapService::class.java)
    }

    suspend fun getWeatherForLocation(location: String): CompleteWeatherData {
        return CompleteWeatherData(
            service.fetchWeatherData(location),
            service.fetchForecastData(location)
        )
    }
}