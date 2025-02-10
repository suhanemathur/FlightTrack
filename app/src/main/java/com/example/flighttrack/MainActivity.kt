package com.example.flighttrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Calendar
import com.example.flighttrack.ui.FlightTrackTheme // Import the theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlightTrackTheme { // Use the imported theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    FlightTrackApp()
                }
            }
        }
    }
}

@Composable
fun FlightTrackApp() {
    var useMiles by remember { mutableStateOf(false) }
    var currentStopIndex by remember { mutableIntStateOf(0) }
    val stops = Utils.loadStopsFromResource(LocalContext.current, R.raw.stops)
    val totalDistance = stops.sumOf { it.distanceKm }
    val coveredDistance = stops.take(currentStopIndex + 1).sumOf { it.distanceKm } // Include current stop
    val remainingDistance = maxOf(0, totalDistance - coveredDistance) // Ensure it doesn't go negative

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome Message
        Text(
            text = getWelcomeMessage(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Weather Information
        WeatherInfo(stops[currentStopIndex].name)

        // Starting Point
        Text(
            text = "Starting Point: ${stops[0].name}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Progress Bar
        LinearProgressIndicator(
            progress = { coveredDistance.toFloat() / totalDistance.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Progress Text
        Text(
            text = "Covered: ${if (useMiles) Utils.kmToMiles(coveredDistance) else coveredDistance} ${if (useMiles) "miles" else "km"} | " +
                    "Left: ${if (useMiles) Utils.kmToMiles(remainingDistance) else remainingDistance} ${if (useMiles) "miles" else "km"}",
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Stops List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(stops) { stop ->
                StopItem(
                    stop = stop,
                    useMiles = useMiles,
                    isCurrentStop = stops.indexOf(stop) == currentStopIndex,
                    onStopClick = { newIndex -> currentStopIndex = newIndex },
                    stops = stops
                )
            }
        }

        // Fact Box
        Text(
            text = getFactText(stops[currentStopIndex].name),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.Yellow)
                .padding(8.dp)
        )

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { useMiles = !useMiles }) {
                Text("Toggle Distance")
            }

            Button(
                onClick = {
                    if (currentStopIndex < stops.size - 1) {
                        currentStopIndex++
                    }
                },
                enabled = currentStopIndex < stops.size - 1
            ) {
                Text(if (currentStopIndex == stops.size - 1) "Journey Completed" else "Next Stop")
            }
        }

        // Restart Button
        Button(
            onClick = { currentStopIndex = 0 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Journey")
        }
    }
}

@Composable
fun StopItem(
    stop: Stop,
    useMiles: Boolean,
    isCurrentStop: Boolean,
    onStopClick: (Int) -> Unit,
    stops: List<Stop>
) {
    val distance = if (useMiles) Utils.kmToMiles(stop.distanceKm) else stop.distanceKm
    val unit = if (useMiles) "miles" else "km"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onStopClick(stops.indexOf(stop)) }
    ) {
        Text(
            text = if (isCurrentStop) "▶ ${stop.name}" else stop.name,
            fontSize = 18.sp,
            fontWeight = if (isCurrentStop) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrentStop) Color.Blue else Color.Black
        )
        Text(text = "Distance: $distance $unit", fontSize = 16.sp)
        Text(text = "Visa: ${stop.visaRequirement}", fontSize = 14.sp, color = Color.Red)
    }
}

@Composable
fun WeatherInfo(location: String) {
    var weatherText by remember { mutableStateOf("Fetching weather...") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(location) {
        coroutineScope.launch {
            weatherText = fetchWeather(location)
        }
    }

    Text(
        text = weatherText,
        fontSize = 16.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

private fun getWelcomeMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning! Ready for an adventure?"
        in 12..17 -> "Good Afternoon! Keep flying high!"
        else -> "Good Evening! Safe travels!"
    }
}

private fun getFactText(location: String): String {
    val (fact, recommendations) = placeFacts[location] ?: Pair("No info available", emptyList())
    val recommendedPlaces = if (recommendations.isNotEmpty())
        "Places to Visit: " + recommendations.joinToString(", ")
    else "No recommendations available"

    return "Fact: $fact\n$recommendedPlaces"
}

private val placeFacts = mapOf(
    "New York" to Pair(
        "New York has the largest subway system in the world!",
        listOf("Times Square", "Central Park")
    ),
    "London" to Pair(
        "London's Underground is the oldest metro system in the world.",
        listOf("Big Ben", "Tower of London")
    ),
    "Dubai" to Pair(
        "Dubai is home to the world's tallest building, the Burj Khalifa.",
        listOf("Burj Khalifa", "Palm Jumeirah")
    ),
    "Mumbai" to Pair(
        "Mumbai is India's financial capital and home to Bollywood.",
        listOf("Gateway of India", "Marine Drive")
    ),
    "Sydney" to Pair(
        "Sydney has the world's largest natural harbor.",
        listOf("Sydney Opera House", "Bondi Beach")
    )
)

private suspend fun fetchWeather(location: String): String {
    val apiKey = "64a152de095d26616fa6126b62cd0e5a"
    val formattedLocation = location.trim()
    val url = "https://api.openweathermap.org/data/2.5/weather?q=$formattedLocation&appid=$apiKey&units=metric"

    return try {
        val result = withContext(Dispatchers.IO) { URL(url).readText() }
        val jsonObj = JSONObject(result)

        if (jsonObj.has("cod") && jsonObj.getInt("cod") != 200) {
            val errorMessage = jsonObj.optString("message", "Unknown error")
            "Weather unavailable: $errorMessage"
        } else {
            val temp = jsonObj.getJSONObject("main").getDouble("temp")
            val description = jsonObj.getJSONArray("weather").getJSONObject(0).getString("description")
            "Weather at $formattedLocation: $temp°C, $description"
        }
    } catch (e: Exception) {
        "Weather unavailable"
    }
}