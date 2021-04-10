package andrewbastin.drizzle.data.api

data class Coord(
    val lon: Float,
    val lat: Float
)

data class Weather(
    val id: String,
    val main: String,
    val description: String,
    val icon: String
)

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