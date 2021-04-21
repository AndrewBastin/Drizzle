package andrewbastin.drizzle.data.api

import androidx.compose.ui.graphics.Color

data class Coord(
    val lon: Float,
    val lat: Float
)

data class Weather(
    val id: String,
    val main: String,
    val description: String,
    val icon: String
) {

    val iconColor: Color
        get() = when (icon) {
            "01d", "02d", "03d" -> Color(135, 206, 235)
            "01n", "02n", "03n" -> Color(25, 25, 112)
            "04d"               -> Color(208, 226, 237)
            "04n"               -> Color(46, 26, 79)
            "09d", "10d"        -> Color(3, 74, 236)
            "09n", "10n"        -> Color(1, 16, 150)
            "11d"               -> Color(119, 33, 111)
            "11n"               -> Color(44, 0, 30)
            "13d"               -> Color(245, 245, 245)
            "13n"               -> Color(40, 40, 40)
            "50d"               -> Color(233, 242, 228)
            "50n"               -> Color(50, 50, 50)
            else -> Color.Black
        }
}

data class Main(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Float,
    val humidity: Float
)

data class Wind(
    var speed: Float,
    var deg: Float
)

data class Clouds(
    val all: Float
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class DailyWeatherData(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)