package `in`.iiitd.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class listOfCities(
    val startCity: String,
    var cities: List<String>,
    var distances: List<Int>,
    val destinationCity: String
)

fun saveRouteToJson(context: Context, route: listOfCities) {
    try {
        // Use start and destination cities to create filename, convert to lowercase for consistency
        val fileName = "${route.startCity}_${route.destinationCity}.json".lowercase()
        val jsonString = Json.encodeToString(route)
        val file = File(context.filesDir, fileName)
        file.writeText(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadRouteFromJson(context: Context, startCity: String, destinationCity: String): listOfCities? {
    try {
        // Create filename using the same logic as in saveRouteToJson, convert to lowercase
        val fileName = "${startCity}_${destinationCity}.json".lowercase()
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            return null
        }

        val jsonString = file.readText()
        return Json.decodeFromString<listOfCities>(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val context = this

        // Simulate saved data - Use the new save function
        saveRouteToJson(context, listOfCities("Delhi", listOf("Delhi", "City 1", "City 2", "Mumbai"), listOf(0, 200, 300, 400), "Mumbai"))
        saveRouteToJson(context, listOfCities("Mumbai", listOf("Mumbai", "City 3", "Pune"), listOf(0, 200, 300), "Pune")) //Example for another route

        val startCityInput = findViewById<TextInputEditText>(R.id.start_city_input)
        val destinationCityInput = findViewById<TextInputEditText>(R.id.destination_city_input)
        val planButton = findViewById<MaterialButton>(R.id.plan_button)

        planButton.setOnClickListener {
            val startCity = startCityInput.text.toString().trim()
            val destinationCity = destinationCityInput.text.toString().trim()

            if (startCity.isNotEmpty() && destinationCity.isNotEmpty()) {
                val my_route = loadRouteFromJson(context, startCity, destinationCity) // Pass start and destination

                if (my_route != null) {
                    val routeJson = Json.encodeToString(my_route)
                    val intent = Intent(this, JourneyActivity::class.java)
                    intent.putExtra("Route", routeJson)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Route not found for $startCity to $destinationCity", Toast.LENGTH_SHORT).show() // More informative message
                }
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}