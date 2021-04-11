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
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {

    val isLoadingWeatherData = MutableLiveData(false)
    val weatherData = MutableLiveData<DailyWeatherData?>(null)

    fun loadWeatherData(location: String) {
        this.isLoadingWeatherData.value = true

        CoroutineScope(Dispatchers.Main).launch {
            weatherData.value = OpenWeatherMapRetriever().getWeatherForLocation(location)
            isLoadingWeatherData.value = false
        }
    }

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: MainViewModel by viewModels()

        setContent {
            MainScreen(model)
        }

        model.loadWeatherData("Thunder Bay")
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel) {

    val isLoading: Boolean by mainViewModel.isLoadingWeatherData.observeAsState(true)
    val weatherData: DailyWeatherData? by mainViewModel.weatherData.observeAsState()
    DrizzleTheme {
        Surface(color = MaterialTheme.colors.background) {
            if (isLoading) {
                Text("Loading")
            } else weatherData?.let {
                DailyWeatherContent(it)
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

        Text(data.main.temp.kelvinToCelsius().toInt().toString())
        Text(data.weather[0].description)
        Text(data.main.feels_like.kelvinToCelsius().toInt().toString())
        Text(data.main.temp_max.kelvinToCelsius().toInt().toString())
        Text(data.main.temp_min.kelvinToCelsius().toInt().toString())
        Text(data.wind.speed.toString())
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
