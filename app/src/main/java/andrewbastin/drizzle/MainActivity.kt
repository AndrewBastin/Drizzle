package andrewbastin.drizzle

import andrewbastin.drizzle.data.api.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import andrewbastin.drizzle.ui.theme.DrizzleTheme
import andrewbastin.drizzle.utils.epochToDateString
import andrewbastin.drizzle.utils.kelvinToCelsius
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.animation.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Water
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
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
            MainScreen(model)
        }

        model.loadWeatherData(listOf("Thunder Bay", "Winnipeg", "Toronto"))
    }
}

@ExperimentalPagerApi
@Composable
fun MainScreen(mainViewModel: MainViewModel) {

    val isLoading: Boolean by mainViewModel.isLoadingWeatherData.observeAsState(true)
    val weatherData: List<CompleteWeatherData>? by mainViewModel.weatherData.observeAsState()
    val colors = listOf(Color.Red, Color.Green, Color.Blue)

    DrizzleTheme {
        Surface(color = MaterialTheme.colors.background) {
            if (isLoading) {
                Text("Loading")
            } else weatherData?.let {

                val pagerState = PagerState(
                    pageCount = it.size,
                    currentPage = 0
                )

                HorizontalPager(
                    state = pagerState
                ) { page ->
                    Surface(
                        color = if (currentPage < it.size - 1)
                                    lerp(
                                        colors[currentPage],
                                        colors[currentPage + 1],
                                        currentPageOffset
                                    )
                                else
                                    colors[it.size - 1],
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        DailyWeatherContent(it[page].current)
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DailyWeatherContent(
    @PreviewParameter(FakeWeatherDataProvider::class) data: DailyWeatherData
) {
    Column(
        Modifier.padding(30.dp)
    ) {
        DailyWeatherHeader(data)
        DailyWeatherCurrentStats(data)
        DailyWeatherDayStats(data)
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
                bottom = 20.dp
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
            color = MaterialTheme.colors.onSurface, thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyWeatherCurrentStats(
    @PreviewParameter(FakeWeatherDataProvider::class) data: DailyWeatherData
) {
    Column(
        modifier = Modifier.padding(bottom = 20.dp)
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
                .padding(top = 50.dp)
                .alpha(0.7F),
            color = MaterialTheme.colors.onSurface,
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

@Preview(showBackground = true)
@Composable
fun DailyWeatherDayStats(
    @PreviewParameter(FakeWeatherDataProvider::class) data: DailyWeatherData
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
                    "${data.main.temp_max.kelvinToCelsius().toInt()}°C"
                )
            }

            Row(
                modifier = Modifier.weight(1f),
                Arrangement.End
            ) {
                DailyWeatherIconText(
                    Icons.Filled.ArrowUpward,
                    "Minimum",
                    "${data.main.temp_min.kelvinToCelsius().toInt()}°C"
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
