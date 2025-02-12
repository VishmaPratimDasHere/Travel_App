@file:Suppress("RemoveExplicitTypeArguments")

package `in`.iiitd.travel_app


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iiitd.travel_app.ui.theme.Travel_AppTheme
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Route(
    val startCity: String? = null,
    val route: List<String> = emptyList(),
    val distances: List<Int> = emptyList(),
    val destinationCity: String? = null,
    val totalDistance: Int = 0 // Add totalDistance to Route data class
)

fun saveRouteToJson(context: Context, fileName: String, route: Route) {
    try {
        val jsonString = Json.encodeToString(route) // Serialize to JSON string
        val file = File(context.filesDir, fileName)
        file.writeText(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadRouteFromJson(context: Context, fileName: String): Route? {
    try {
        val file = File(context.filesDir, fileName)
        val jsonString = file.readText()
        return Json.decodeFromString<Route>(jsonString) // Deserialize from JSON
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Travel_AppTheme {
                Homepage()
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(){
    // Simulate some data is present on some database. We run a function to add hardcoded data.
    saveRouteToJson(LocalContext.current,"delhi_mumbai.json",Route("Delhi", listOf("Jaipur", "Ajmer", "Udaipur", "Ahmedabad", "Vadodara", "Surat","Nashik"), listOf(200,320,520,710,1160,1310,1510), "Mumbai",1600))
    val context= LocalContext.current
    var showDialog by remember { mutableStateOf<Boolean>(false) }
    var startCity by remember { mutableStateOf<String>("") }
    var destinationCity by remember { mutableStateOf<String>("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(64.dp))
        Card (
            modifier = Modifier.padding(all = 16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row (
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Welcome message icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text="Plan your travel!")
                }
                TextField(
                    value = startCity,
                    onValueChange = {
                        startCity = it
                    },
                    placeholder = { Text("Start City") },
                    label = {Text("Start")}
                )
                TextField(
                    value = destinationCity,
                    onValueChange = {
                        destinationCity=it
                    },
                    placeholder = { Text("Destination City") },
                    label = { Text("Destination") }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if(startCity!="" && destinationCity!=""){
                            showDialog=true
                        } else {
                            Toast.makeText(context,"Please fill all fields.",Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Plan my journey!")
                }

                if(showDialog){
                    BasicAlertDialog(
                        onDismissRequest = {
                            showDialog=false
                        }
                    ) {
                        Card (
                            border = BorderStroke(width = 1.dp, color = Color.Gray),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    "Is this correct?",
                                    style = TextStyle(fontSize = 24.sp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "It appears you want to travel to and from the following cities.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(startCity, destinationCity).forEach { x ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.background(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(16.dp)
                                                ).size(24.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            ) // Leading icon
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(x)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        colors = ButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            contentColor = MaterialTheme.colorScheme.primary,
                                            disabledContentColor = MaterialTheme.colorScheme.primary,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                        ),
                                        onClick = {
                                            showDialog=false
                                        }
                                    ) {
                                        Text("Wrong!")
                                    }
                                    Button(
                                        colors = ButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            contentColor = MaterialTheme.colorScheme.primary,
                                            disabledContentColor = MaterialTheme.colorScheme.primary,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                        ),
                                        onClick = {
                                            // Get data from database
                                            val my_route=loadRouteFromJson(context,startCity.lowercase()+"_"+destinationCity.lowercase()+".json")
                                            // TODO: Launch next activity and pass data using intent
                                            if (my_route!=null) {
                                                val intent = Intent(
                                                    context,
                                                    JourneyDeetsActivity::class.java
                                                )
                                                intent.putExtra(
                                                    "Route",
                                                    Json.encodeToString(my_route)
                                                )
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(context,"Some error in getting data",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Text("Yes, that's correct!")
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    Travel_AppTheme {
        Homepage()
    }
}