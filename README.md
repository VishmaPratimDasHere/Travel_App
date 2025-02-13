# Journey Planner App

This Android application helps users plan their journeys between cities, displaying a list of cities along the route, the remaining distance, and a progress bar.  It also allows users to toggle between displaying distances in kilometers and miles.

## Features

*   Displays a list of cities in the planned route.
*   Shows the remaining distance of the journey.
*   Provides a visual progress bar indicating the journey's completion.
*   Allows users to switch between displaying distances in kilometers and miles.
*   Highlights the current city in the route.

## Technologies Used

*   Kotlin
*   Android SDK
*   RecyclerView
*   Material Design Components
*   Kotlin Serialization (`kotlinx.serialization`)

## Setup

1.  Clone the repository: `git clone https://github.com/your-username/journey-planner-app.git` (Replace with your actual repository URL)
2.  Open the project in Android Studio.
3.  Ensure you have the necessary Android SDK and build tools installed.
4.  Build and run the app on an Android emulator or physical device.

## How to Use

1.  The app starts by displaying the route information passed to it (e.g., via an Intent). The first city in the route is highlighted.
2.  The "I'm at the next stop!" button advances the user to the next city in the route, updating the displayed distance, progress bar, and highlighting.
3.  The "Show distance in miles/km" button toggles the distance units between kilometers and miles.

## How We Built This

This app was developed using Kotlin and the Android SDK.  The user interface was built using layouts defined in XML, incorporating Material Design components for a modern look and feel. A `RecyclerView` was used to efficiently display the list of cities, and a custom adapter was implemented to handle the data binding and item highlighting.

Key aspects of the development process:

*   **Data Serialization:**  Kotlin Serialization (`kotlinx.serialization`) was used to convert the route data (a `listOfCities` object) into a JSON string for easy storage and transfer between activities.
*   **Dynamic Highlighting:** The highlighting of the current city in the `RecyclerView` was achieved by maintaining a `selectedPosition` in the adapter and using `notifyItemChanged()` to refresh the highlighted and previously highlighted items. This ensures smooth and efficient updates.
*   **Distance Calculation and Display:** The remaining distance is calculated based on the current city and the total distance, and displayed using `TextViews`. The conversion between kilometers and miles is handled by a simple function.
*   **Progress Bar:** A `ContentLoadingProgressBar` provides a visual representation of the journey's progress, calculated as a percentage of the total distance covered.

## Project Structure

JourneyPlannerApp/<br>
├── app/<br>
│   ├── src/<br>
│   │   └── main/<br>
│   │       ├── java/<br>
│   │       │   └── in/<br>
│   │       │       └── iiitd/<br>
│   │       │           └── myapplication/<br>
│   │       │               ├── JourneyActivity.kt      # Main activity<br>
│   │       │               ├── HomeActivity.kt          # Activity for inputting cities (if applicable)<br>
│   │       │               ├── listOfCities.kt        # Data class for route information<br>
│   │       │               └── ...                     # Other Kotlin files<br>
│   │       ├── res/<br>
│   │       │   ├── layout/<br>
│   │       │   │   ├── activity_journey.xml        # Layout for JourneyActivity<br>
│   │       │   │   └── list_item_city.xml           # Layout for RecyclerView items<br>
│   │       │   ├── values/<br>
│   │       │   │   ├── colors.xml                 # Color definitions<br>
│   │       │   │   └── strings.xml                # String resources<br>
│   │       │   └── ...                             # Other resource files<br>
│   │       └── AndroidManifest.xml<br>
│   └── build.gradle<br>
├── build.gradle<br>
├── settings.gradle<br>
└── ...<br>


## Contributing

Contributions are welcome!  Please submit a pull request with your changes.

## License

[Choose a license, e.g., MIT License]
