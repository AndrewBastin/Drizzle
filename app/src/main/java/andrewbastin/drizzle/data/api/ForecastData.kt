package andrewbastin.drizzle.data.api

data class DayForecastData(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Float,
    val sea_level: Float,
    val grnd_level: Float,
    val humidity: Float,
    val temp_kf: Float
)

data class ForecastWeatherInfo(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class ForecastClouds(
    val all: Int
)

data class ForecastWind(
    val speed: Float,
    val deg: Float,
    val gust: Float
)

data class ForecastSys(
    val pod: String
)

data class ForecastSnow(
    val `3h`: Float
)

data class ForecastRain(
    val `3h`: Float
)

data class ForecastList(
    val dt: Long,
    val main: DayForecastData,
    val weather: List<ForecastWeatherInfo>,
    val clouds: ForecastClouds,
    val wind: ForecastWind,
    val visibility: Float,
    val pop: Float  ,
    val sys: ForecastSys,
    val dt_txt: String,
    val snow: ForecastSnow,
    val rain: ForecastRain
)

data class ForecastCoord(
    val lat: Float,
    val lon: Float
)

data class ForecastCity(
    val id: Int,
    val name: String,
    val coord: ForecastCoord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class ForecastData(
    val cod: String,
    val message: Float,
    val cnt: Int,
    val list: List<ForecastList>,
    val city: ForecastCity
)

