<!DOCTYPE html>
<html>
<head>
    <title>Assignment 1:FlightTrack App - Project Documentation</title>
</head>
<body>
    <h1>FlightTrack App - Project Documentation</h1>
    
    <h2>Introduction</h2>
    <p>The FlightTrack app is designed to help users track airplane journeys across multiple stops, providing information on distances, visa requirements, and progress tracking. The project includes two implementations:</p>
    <ul>
        <li><strong>Version 1:</strong> XML + Kotlin (Traditional UI)</li>
        <li><strong>Version 2:</strong> Jetpack Compose (Modern UI)</li>
    </ul>
    
    <h2>Features Implemented</h2>
    <h3>Text Display (Traditional and Lazy Column)</h3>
    <p>Both versions display stops using appropriate methods:</p>
    <ul>
        <li><strong>XML Version:</strong> Uses RecyclerView to list stops.</li>
        <li><strong>Jetpack Compose Version:</strong> Uses LazyColumn for efficient rendering.</li>
    </ul>
    
    <h3>Buttons & Unit Conversion</h3>
    <p>The app includes buttons for functionality:</p>
    <ul>
        <li><strong>Toggle Button:</strong> Converts distances between kilometers and miles.</li>
        <li><strong>Next Stop Button:</strong> Moves to the next destination.</li>
        <li><strong>Restart Button:</strong> Resets the journey.</li>
    </ul>
    
    <h3>User Interface & ProgressBar</h3>
    <p>The UI elements ensure a smooth experience:</p>
    <ul>
        <li>Displays the current stop, total distance, and remaining distance.</li>
        <li>ProgressBar dynamically updates based on journey progress.</li>
        <li>Fact box provides interesting information about each stop.</li>
    </ul>
    
    <h2>Implementation Details</h2>
    <h3>Version 1: XML + Kotlin</h3>
    <p>This version follows a traditional UI approach:</p>
    <ul>
        <li>RecyclerView for displaying stops.</li>
        <li>Buttons and text views defined in XML.</li>
        <li>ProgressBar updates based on the current stop.</li>
    </ul>
    
    <h3>Version 2: Jetpack Compose</h3>
    <p>This version is implemented using modern UI techniques:</p>
    <ul>
        <li>LazyColumn for efficient list rendering.</li>
        <li>Composable functions for modular UI components.</li>
        <li>State management using remember and mutableStateOf.</li>
    </ul>
    
    <h2>How to Run the Project</h2>
    <ol>
        <li>Clone the GitHub repository to your local machine.</li>
        <li>Open the project in Android Studio.</li>
        <li>Select the desired version (XML or Jetpack Compose).</li>
        <li>Build and run the app on an emulator or a real device.</li>
    </ol>
    
    <h2>Conclusion</h2>
    <p>This project successfully demonstrates both XML and Jetpack Compose UI implementations, ensuring compatibility and a smooth user experience. It covers key Android development concepts, including state management, UI components, and data handling.</p>
</body>
</html>
