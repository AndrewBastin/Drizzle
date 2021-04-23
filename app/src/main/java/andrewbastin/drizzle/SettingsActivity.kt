package andrewbastin.drizzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import andrewbastin.drizzle.ui.theme.DrizzleTheme
import android.content.Context
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    val locationA = MutableLiveData("Thunder Bay")
    val locationB = MutableLiveData("Kochi")
    val locationC = MutableLiveData("London")

    fun loadSettingsValues(context: Context) {
        val sharedPrefs = context.getSharedPreferences("drizzle", Context.MODE_PRIVATE)

        // Default Values
        locationA.value = sharedPrefs.getString("locA", "Thunder Bay")
        locationB.value = sharedPrefs.getString("locB", "Kochi")
        locationC.value = sharedPrefs.getString("locC", "London")
    }

    fun updateLocationA(newLocation: String, context: Context) {
        val sharedPrefs = context.getSharedPreferences("drizzle", Context.MODE_PRIVATE)
        locationA.value = newLocation
        sharedPrefs.edit {
            putString("locA", newLocation)
        }
    }

    fun updateLocationB(newLocation: String, context: Context) {
        val sharedPrefs = context.getSharedPreferences("drizzle", Context.MODE_PRIVATE)
        locationB.value = newLocation
        sharedPrefs.edit {
            putString("locB", newLocation)
        }
    }

    fun updateLocationC(newLocation: String, context: Context) {
        val sharedPrefs = context.getSharedPreferences("drizzle", Context.MODE_PRIVATE)
        locationC.value = newLocation
        sharedPrefs.edit {
            putString("locC", newLocation)
        }
    }
}

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SettingsViewModel by viewModels()
        viewModel.loadSettingsValues(this)

        setContent {
            DrizzleTheme {
                SettingsPageContent(
                    settingsViewModel = viewModel,
                    onBackTap = {
                        this.finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsPageContent(
    settingsViewModel: SettingsViewModel,
    onBackTap: () -> Unit
) {
    val context = LocalContext.current

    val locationA = settingsViewModel.locationA.observeAsState()
    val locationB = settingsViewModel.locationB.observeAsState()
    val locationC = settingsViewModel.locationC.observeAsState()

    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxHeight()
    ) {
        Column {
            TopAppBar(
                backgroundColor = Color.Black,
                contentColor = Color.White,
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackTap) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = locationA.value!!,
                    onValueChange = {
                                    settingsViewModel.updateLocationA(it, context)
                    },
                    label = { Text("Location A") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = Color.White,
                        textColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 40.dp
                        )
                )
                OutlinedTextField(
                    value = locationB.value!!,
                    onValueChange = {
                                    settingsViewModel.updateLocationB(it, context)
                    },
                    label = { Text("Location B") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = Color.White,
                        textColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 40.dp
                        )
                )
                OutlinedTextField(
                    value = locationC.value!!,
                    onValueChange = {
                                    settingsViewModel.updateLocationC(it, context)
                    },
                    label = { Text("Location A") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = Color.White,
                        textColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 40.dp
                        )
                )
            }
        }
    }
}