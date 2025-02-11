# Mobile computing Assignment 1: FlightTrack App

## Overview
FlightTrack is an Android application that will help users track their airplane journey across multiple stops. The app provides details such as distance traveled, remaining distance, visa requirements, current weather conditions and additional facts about each stop. It also includes a unit conversion feature (km ↔ miles) and allows users to mark progress as they reach each stop.

This project has been implemented in two versions:
1.Version 1 - Developed using XML + Kotlin 
2.Version 2 - Developed using Jetpack Compose

## Features
### Common Features in Both Versions
- Displays a list of stops (City name, distance, and visa requirement).
- Unit conversion between km and miles.
- Progress tracking with a ProgressBar.
- "Next Stop" button to update the journey progress.
- A text box for additional travel facts and places recommended.
- Current Weather conditions of the location you are in using OpenWeatherMap API
- Data is loaded from a resource file (`stops.txt`).
- A `Restart Journey` button to reset progress.
- Greeting message based on the time of day.


### Version 1: XML + Kotlin
- Uses RecyclerView to display the list of stops.
- Implements UI using XML layouts.
- Uses `ProgressBar` for progress visualization.
- Standard Android Views (`TextView`, `Button`, `LinearLayout`).

### Version 2: Jetpack Compose
- Uses LazyColumn for dynamic lists.
- Implements UI using Composables (`Text`, `Column`, `Button`, `ProgressBar`).
- Uses State Management (`remember`) to update UI dynamically.
- Implements a fully declarative UI with Jetpack Compose.

---

## Implementation Details

### Data Handling
- Stops data (`stops.txt`) is read using a helper utility (`Utils.loadStopsFromResource()`).
- The data is parsed and stored in a List of Stop objects.
- Each stop contains:
  - `name`: City name
  - `distanceKm`: Distance from the previous stop
  - `visaRequired`: Boolean value indicating visa necessity

### Text Display
- XML Version: Uses `TextView` inside a `RecyclerView` item.
- Compose Version: Uses `Text()` inside `LazyColumn`.

### Distance Conversion
- A toggle button allows switching between km and miles.
- Uses a helper function:  
  ```kotlin
  fun kmToMiles(km: Int): Int {
      return (km * 0.621371).toInt()
  }

### Weather Updates
- The app fetches real-time weather data for the current location using the API pf OpenWeatherMap.
- The weather details (temperature & conditions) are displayed in the greeting box.
- Implemented using:
  ```kotlin
  fun fetchWeather(city: String) {
      // API Call to fetch weather for 'city'
  }

### Fun Facts Display
- A small box below the stops shows:
- A fun fact about the city.
- 2 recommended places to visit.
Facts are stored in a Map inside MainActivity

### Greeting
- The app greets the user with a "Good Morning", "Good Afternoon", or "Good Evening" message based on the current system time.


