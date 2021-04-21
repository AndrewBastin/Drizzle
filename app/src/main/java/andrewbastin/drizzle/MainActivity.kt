package andrewbastin.drizzle

import andrewbastin.drizzle.data.api.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.tooling.preview.Preview
import andrewbastin.drizzle.ui.theme.DrizzleTheme
import andrewbastin.drizzle.utils.epochToDateString
import andrewbastin.drizzle.utils.kelvinToCelsius
import andrewbastin.drizzle.utils.luminosity
import android.app.Activity
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Water
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.LocalSystemUiController
import com.google.accompanist.systemuicontroller.rememberAndroidSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {

    val isLoadingWeatherData = MutableLiveData(false)
    val weatherData = MutableLiveData<List<CompleteWeatherData>>()

    fun loadWeatherData(locations: List<String>) {
        this.isLoadingWeatherData.value = true

        CoroutineScope(Dispatchers.Main).launch {
            weatherData.value = locations.map {
                OpenWeatherMapRetriever().getWeatherForLocation(it)
            }

            isLoadingWeatherData.value = false
        }
    }

}

@ExperimentalPagerApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: MainViewModel by viewModels()

        setContent {
            val controller = rememberAndroidSystemUiController()

            CompositionLocalProvider(LocalSystemUiController provides controller) {
                MainScreen(model)
            }
        }

        model.loadWeatherData(listOf("Thunder Bay", "London", "Kochi"))
    }
}

@ExperimentalPagerApi
@Composable
fun MainScreen(mainViewModel: MainViewModel) {

    val isLoading: Boolean by mainViewModel.isLoadingWeatherData.observeAsState(true)
    val weatherData: List<CompleteWeatherData>? by mainViewModel.weatherData.observeAsState()

    var bgColor by remember { mutableStateOf(Color.Black) }
    val textColor = remember(bgColor) {
        if (bgColor.luminosity > 0.5) Color.Black else Color.White
    }

    val systemUiController = LocalSystemUiController.current

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = bgColor,
            darkIcons = bgColor.luminosity > 0.5
        )
    }

    DrizzleTheme {
        Surface(
            color = Color.Red
        ) {
            if (isLoading) {
                Text("Loading")
            } else weatherData?.let {
                bgColor = it[0].current.weather[0].iconColor

                val pagerState = rememberPagerState(
                    pageCount = it.size,
                    initialPage = 0
                )
                Surface(
                    color = Color.Red
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.background(Color.Red)
                    ) { page ->
                        Log.d("Offset", "%.2f".format(currentPageOffset))
                        bgColor = if (currentPage < it.size - 1)
                            lerp(
                                it[currentPage].current.weather[0].iconColor,
                                it[currentPage + 1].current.weather[0].iconColor,
                                currentPageOffset
                            )
                        else
                            it[currentPage].current.weather[0].iconColor

                        Surface(
                            modifier = Modifier.fillMaxHeight(),
                            color = bgColor,
                            contentColor = textColor
                        ) {
                            DailyWeatherContent(it[page].current, it[page].forecast)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyWeatherContent(
    data: DailyWeatherData,
    forecastData: ForecastData
) {
    Column(
        Modifier.padding(30.dp)
    ) {
        DailyWeatherHeader(data)
        DailyWeatherCurrentStats(data)
        DailyWeatherDayStats(data, forecastData)
    }
}

@Preview(showBackground = true)
@Composable
fun DailyWeatherHeader(
    @PreviewParameter(FakeWeatherDataProvider::class) data: DailyWeatherData
) {
    Column(
        Modifier
            .padding(
                bottom = 100.dp
            )
    ) {
        Text(
            text = data.name.toUpperCase(Locale.ROOT),

            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )
        Text(
            text = "updated on ${data.dt.epochToDateString("h:m a")}",

            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(0.4F)
        )
        Divider(
            modifier = Modifier
                .padding(top = 15.dp)
                .alpha(0.7F),
            color = LocalContentColor.current, thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyWeatherCurrentStats(
    @PreviewParameter(FakeWeatherDataProvider::class) data: DailyWeatherData
) {
    Column(
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontSize = 100.sp, fontWeight = FontWeight.Bold))
                append(data.main.temp.kelvinToCelsius().toInt().toString())

                pop()

                pushStyle(SpanStyle(fontSize = 20.sp, baselineShift = BaselineShift(4.5f)))
                append("°C")

                pop()
            }
        )
        Text(
            text = data.weather[0].description.capitalize(Locale.ROOT),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )

        Text(
            text = "people say it feels colder",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.
                    alpha(0.4F)
        )

        Divider(
            modifier = Modifier
                .padding(top = 150.dp)
                .alpha(0.7F),
            color = LocalContentColor.current,
            thickness = 1.dp
        )
    }
}

@Composable
fun DailyWeatherIconText(
    icon: ImageVector,
    iconText: String,
    text: String
) {
    Row {
        Icon(
            icon,
            iconText,
            modifier = Modifier
                .padding(end = 10.dp)
                .size(30.dp)
        )

        Text(
            text,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun DailyWeatherDayStats(
    data: DailyWeatherData,
    forecastData: ForecastData
) {
    Column {
        Row(
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                DailyWeatherIconText(
                    Icons.Filled.ArrowUpward,
                    "Maximum",
                    "${forecastData.list[0].main.temp_max.kelvinToCelsius().toInt()}°C"
                )
            }

            Row(
                modifier = Modifier.weight(1f),
                Arrangement.End
            ) {
                DailyWeatherIconText(
                    Icons.Filled.ArrowUpward,
                    "Minimum",
                    "${forecastData.list[0].main.temp_min.kelvinToCelsius().toInt()}°C"
                )
            }
        }

        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                DailyWeatherIconText(
                    Icons.Filled.Air,
                    "Wind",
                    "${data.wind.speed.toInt()}km/h"
                )
            }

            Row(
                modifier = Modifier.weight(1f),
                Arrangement.End
            ) {
                DailyWeatherIconText(
                    Icons.Filled.Water,
                    "Humidity",
                    "${data.main.humidity.toInt()}%"
                )
            }
        }
    }
}

class FakeForecastDataProvider: PreviewParameterProvider<ForecastData> {

    override val values: Sequence<ForecastData>
        get() = sequenceOf(
            ForecastData(
                cod = "200",
                message = 0F,
                cnt = 40,
                list = listOf(
                    ForecastList(
                        dt = 1619049600,
                        main = DayForecastData(
                            temp = 277.1F,
                            feels_like = 274.87F,
                            temp_min = 275.16F,
                            temp_max = 277.1F,
                            pressure = 1016F,
                            sea_level = 1016F,
                            grnd_level = 990F,
                            humidity = 41F,
                            temp_kf = 1.94F
                        ),
                        weather = listOf(
                            ForecastWeatherInfo(
                                id = 803,
                                main = "Clouds",
                                description = "broken clouds",
                                icon = "04d"
                            )
                        ),
                        clouds = ForecastClouds(
                            all = 75
                        ),
                        wind = ForecastWind(
                            speed = 2.43F,
                            deg = 201F,
                            gust = 3.58F
                        ),
                        visibility = 10000F,
                        pop = 0F,
                        sys = ForecastSys(
                            pod = "d"
                        ),
                        dt_txt = "2021-04-22 00:00:00",
                        snow = null,
                        rain = null
                    )
                ),
                city = ForecastCity(
                    id = 6166142,
                    name = "Thunder Bay",
                    coord = ForecastCoord(
                        lat = 48.4001F,
                        lon = -89.3168F
                    ),
                    country = "CA",
                    population = 99334,
                    timezone = -14400,
                    sunrise = 1619002497,
                    sunset = 1619052988
                )
            )
        )

}

class FakeWeatherDataProvider: PreviewParameterProvider<DailyWeatherData> {
    override val values: Sequence<DailyWeatherData>
        get() = sequenceOf(
            DailyWeatherData(
            Coord(
                -89.3168F,
                48.4001F
            ),
            listOf(
                Weather(
                    "804",
                    "Clouds",
                    "overcast clouds",
                    "04n"
                )
            ),
            "stations",
            Main(
                279.71F,
                277.47F,
                279.15F,
                280.15F,
                1003F,
                93F
            ),
            10000,
            Wind(
                3.09F,
                110F
            ),
            Clouds(
                90F
            ),
            1618020583,
            Sys(
                1,
                906,
                "CA",
                1617967090,
                1618015134
            ),
            -14400,
            6166142,
            "Thunder Bay",
            200
        )
        )
}
