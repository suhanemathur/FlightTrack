
package com.example.flighttrack

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var stopAdapter: StopAdapter
    private var stops: List<Stop> = listOf()
    private var useMiles: Boolean = false
    private var currentStopIndex: Int = 0

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        val distanceToggle = findViewById<Button>(R.id.btnToggleDistance)
        val nextStopButton = findViewById<Button>(R.id.btnNextStop)
        val restartButton = findViewById<Button>(R.id.btnRestart)
        val progressBar = findViewById<ProgressBar>(R.id.progress)
        val progressText = findViewById<TextView>(R.id.tvProgress)
        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val weatherText = findViewById<TextView>(R.id.tvWeather)
        val startingPointText = findViewById<TextView>(R.id.tvStartingPoint)
        val factText = findViewById<TextView>(R.id.tvFact)
        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)

        // Load stops from resources
        stops = Utils.loadStopsFromResource(this, R.raw.stops)

        if (stops.isNotEmpty()) {
            startingPointText.text = "Starting Point: ${stops[0].name}"
            updateFact(factText, stops[currentStopIndex].name)
        }

        stopAdapter = StopAdapter(stops, useMiles, currentStopIndex)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = stopAdapter

        // Set dynamic welcome message
        setWelcomeMessage(welcomeText)

        // Fetch initial weather for the first stop
        if (stops.isNotEmpty()) {
            fetchWeather(weatherText, stops[currentStopIndex].name)
        } else {
            weatherText.text = "No stops available"
        }

        // Handle distance unit toggle
        distanceToggle.setOnClickListener {
            useMiles = !useMiles
            stopAdapter.updateUnit(useMiles)
            updateProgress(progressText, progressBar)
        }

        // Handle next stop button
        nextStopButton.setOnClickListener {
            if (currentStopIndex < stops.size - 1) {
                currentStopIndex++
                stopAdapter.updateCurrentStop(currentStopIndex)
                updateProgress(progressText, progressBar)
                fetchWeather(weatherText, stops[currentStopIndex].name)
                updateFact(factText, stops[currentStopIndex].name)
            }

            // If last stop reached, disable button
            if (currentStopIndex == stops.size - 1) {
                nextStopButton.isEnabled = false
                nextStopButton.text = "Journey Completed"
            }
        }

        // Handle restart button
        restartButton.setOnClickListener {
            currentStopIndex = 0
            stopAdapter.updateCurrentStop(currentStopIndex)
            nextStopButton.isEnabled = true
            nextStopButton.text = "Next Stop"
            updateProgress(progressText, progressBar)
            fetchWeather(weatherText, stops[currentStopIndex].name)
            updateFact(factText, stops[currentStopIndex].name)
        }

        // Initialize progress tracking
        updateProgress(progressText, progressBar)

        // Ensure buttons are centered below destinations
        buttonContainer.bringToFront()
    }

    private fun updateProgress(progressText: TextView, progressBar: ProgressBar) {
        val totalDistance = stops.sumOf { it.distanceKm }
        var coveredDistance = stops.take(currentStopIndex).sumOf { it.distanceKm }

        // If the journey has ended, set coveredDistance to totalDistance
        if (currentStopIndex >= stops.size - 1) {
            coveredDistance = totalDistance
        }

        val remainingDistance = totalDistance - coveredDistance

        val coveredDisplay = if (useMiles) Utils.kmToMiles(coveredDistance) else coveredDistance
        val remainingDisplay = if (useMiles) Utils.kmToMiles(remainingDistance) else remainingDistance
        val unit = if (useMiles) "miles" else "km"

        progressText.text = "Covered: $coveredDisplay $unit | Left: $remainingDisplay $unit"

        // Ensure progress bar reaches 100% when journey ends
        updateProgressBar(progressBar, coveredDistance, totalDistance)
    }

    private fun updateProgressBar(progressBar: ProgressBar, coveredDistance: Int, totalDistance: Int) {
        val progressPercentage = if (totalDistance == 0) 0 else (coveredDistance * 100 / totalDistance)

        ObjectAnimator.ofInt(progressBar, "progress", progressPercentage).apply {
            duration = 500
            start()
        }
    }

    private fun setWelcomeMessage(welcomeText: TextView) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val message = when (hour) {
            in 0..11 -> "Good Morning! Ready for an adventure?"
            in 12..17 -> "Good Afternoon! Keep flying high!"
            else -> "Good Evening! Safe travels!"
        }
        welcomeText.text = message
    }

    private fun updateFact(factText: TextView, location: String) {
        val (fact, recommendations) = placeFacts[location] ?: Pair("No info available", emptyList())
        val recommendedPlaces = if (recommendations.isNotEmpty())
            "Places to Visit: " + recommendations.joinToString(", ")
        else "No recommendations available"

        factText.text = "Fact: $fact\n$recommendedPlaces"
    }

    private fun fetchWeather(weatherText: TextView, location: String) {
        val apiKey = "64a152de095d26616fa6126b62cd0e5a"

        // Ensure proper city format
        val formattedLocation = location.trim()
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$formattedLocation&appid=$apiKey&units=metric"

        Log.d("WeatherAPI", "Fetching weather for: $formattedLocation")

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { URL(url).readText() }
                val jsonObj = JSONObject(result)

                if (jsonObj.has("cod") && jsonObj.getInt("cod") != 200) {
                    val errorMessage = jsonObj.optString("message", "Unknown error")
                    withContext(Dispatchers.Main) {
                        weatherText.text = "Weather unavailable: $errorMessage"
                    }
                    return@launch
                }

                val temp = jsonObj.getJSONObject("main").getDouble("temp")
                val description = jsonObj.getJSONArray("weather").getJSONObject(0).getString("description")

                withContext(Dispatchers.Main) {
                    weatherText.text = "Weather at $formattedLocation: $temp°C, $description"
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    weatherText.text = "Weather unavailable"
                }
            }
        }
    }
}
