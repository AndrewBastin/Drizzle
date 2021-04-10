package andrewbastin.drizzle.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    suspend fun getWeatherForLocation(location: String): DailyWeatherData {
        return service.fetchWeatherData(location)
    }
}