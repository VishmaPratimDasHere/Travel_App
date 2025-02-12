package `in`.iiitd.travel_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iiitd.travel_app.ui.theme.Travel_AppTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class CityDistPair(
    val city: String,
    val distance: Int,
    val isSelected: Boolean = false
)

class JourneyDeetsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routeJson = intent.getStringExtra("Route")
        val my_route = routeJson?.let { Json.decodeFromString<Route>(it) }

        enableEdgeToEdge()
        setContent {
            Travel_AppTheme {
                Journey(my_route)
            }
        }
    }
}

@Composable
fun Journey(my_route: Route?) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(0f) }
    var selectedIndex by remember { mutableStateOf(0) }
    var remainingDistance by remember { mutableStateOf(my_route?.totalDistance ?: 0) }
    var useMiles by remember { mutableStateOf(false) }

    val listOfCities = remember { mutableStateListOf<CityDistPair>() }

    LaunchedEffect(my_route) {
        listOfCities.clear()
        if (my_route != null) {
            listOfCities.add(CityDistPair(my_route.startCity ?: "", 0))
            for (i in 0 until my_route.route.size) {
                listOfCities.add(CityDistPair(my_route.route[i], my_route.distances[i]))
            }
            listOfCities.add(CityDistPair(my_route.destinationCity ?: "", my_route.distances.lastOrNull() ?: 0))
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        SuggestionChip(onClick = {}, label = { Text("Tap the below button to switch cities.") })

        Button(
            onClick = {
                if (selectedIndex < listOfCities.size - 1) {
                    selectedIndex++

                    progress = if (listOfCities[selectedIndex].city == my_route?.destinationCity) {
                        100.0f
                    } else {
                        (listOfCities[selectedIndex].distance.toFloat() / my_route!!.totalDistance) * 100
                    }

                    remainingDistance = if (listOfCities[selectedIndex].city == my_route?.destinationCity) {
                        0 // Set remainingDistance to 0 at the destination
                    } else {
                        my_route.totalDistance - listOfCities[selectedIndex].distance
                    }

                } else {
                    Toast.makeText(context, "You've reached the end!", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = my_route != null
        ) {
            Text(text = "I'm at the next stop!")
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.height(450.dp)) {
            itemsIndexed(listOfCities) { index, cityDistPair ->
                CityCard(
                    cityDistPair = cityDistPair,
                    isStart = index == 0 || index == listOfCities.size - 1,
                    isSelected = index == selectedIndex,
                    useMiles = useMiles
                )
            }
        }

        if (my_route != null) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(progress = { progress / 100f }, color = Color.Green, trackColor = Color.Black)
                        Text("${String.format("%.2f", progress)}% covered", fontSize = 10.sp)

                        val distanceText = if (useMiles) {
                            String.format("%.2f", remainingDistance * 0.621371) + " miles"
                        } else {
                            "$remainingDistance km"
                        }
                        Text("Distance left: $distanceText", fontSize = 16.sp, modifier = Modifier.padding(8.dp))

                        Button(onClick = { useMiles = !useMiles }) {
                            Text(if (useMiles) "Change to km" else "Change to miles")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun CityCard(cityDistPair: CityDistPair, isStart: Boolean, isSelected: Boolean, mod: Modifier = Modifier.fillMaxWidth().padding(8.dp).background(if (isSelected) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surface), useMiles: Boolean) {
    Card(modifier = mod) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(cityDistPair.city, fontSize = 16.sp)
                Text(if (isStart) "Journey ${if (cityDistPair.distance == 0) "starts" else "ends"} here!" else "On the way", fontSize = 10.sp)
            }
            if (!isStart) {
                val distanceText = if (useMiles) {
                    String.format("%.2f", cityDistPair.distance * 0.621371) + " miles"
                } else {
                    "${cityDistPair.distance} km"
                }
                Text(distanceText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JourneyPreview() {
    Travel_AppTheme {
        Journey(
            Route(
                "Delhi",
                listOf("Agra", "Gwalior", "Mumbai"),
                listOf(200, 320, 1500),
                "Mumbai",
                1600
            )
        )
    }
}