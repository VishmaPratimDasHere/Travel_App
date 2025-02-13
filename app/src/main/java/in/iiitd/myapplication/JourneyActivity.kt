package `in`.iiitd.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.serialization.json.Json

class JourneyActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var distanceTextView: TextView
    private lateinit var progressBar: androidx.core.widget.ContentLoadingProgressBar
    private lateinit var milesButton: MaterialButton
    private var useMiles = false
    private var currentCityIndex = 0
    private lateinit var my_route: listOfCities
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journey)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.my_recycler_view)
        distanceTextView = findViewById(R.id.Distance)
        progressBar = findViewById(R.id.progress_bar)
        milesButton = findViewById(R.id.miles_button)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val routeJson = intent.getStringExtra("Route")
        my_route = routeJson?.let { Json.decodeFromString<listOfCities>(it) } ?: return

        adapter = CityAdapter(my_route.cities, my_route.distances, useMiles)
        recyclerView.adapter = adapter

        adapter.selectedPosition = 0
        adapter.notifyItemChanged(0)

        updateDistance()
        updateProgress()

        val nextStopButton = findViewById<MaterialButton>(R.id.next_stop_button)

        nextStopButton.setOnClickListener {
            if (currentCityIndex < my_route.cities.size - 1) {
                val previousSelectedPosition = adapter.selectedPosition
                adapter.selectedPosition++
                currentCityIndex++
                updateDistance()
                updateProgress()
                adapter.notifyItemChanged(previousSelectedPosition)
                adapter.notifyItemChanged(adapter.selectedPosition)
                recyclerView.scrollToPosition(currentCityIndex)
            } else {
                Toast.makeText(this, "You've reached the end!", Toast.LENGTH_SHORT).show()
            }
        }

        milesButton.setOnClickListener {
            useMiles = !useMiles
            updateDistance()
            adapter.setUseMiles(useMiles)
            adapter.notifyDataSetChanged()

            milesButton.text = if (useMiles) {
                "Show distance in km"
            } else {
                "Show distance in miles"
            }
        }
    }

    private fun updateDistance() {
        val remainingDistance = my_route.distances[my_route.distances.lastIndex] - my_route.distances[currentCityIndex]
        val distanceText = if (useMiles) {
            String.format("%.2f", remainingDistance * 0.621371) + " miles"
        } else {
            "$remainingDistance km"
        }
        distanceTextView.text = distanceText
    }

    private fun updateProgress() {
        val totalDistanceCovered = my_route.distances[currentCityIndex]
        val progressPercentage = (totalDistanceCovered.toFloat() / my_route.distances[my_route.distances.lastIndex]) * 100
        progressBar.progress = progressPercentage.toInt()
    }

    inner class CityAdapter(
        private val cities: List<String>,
        private val distances: List<Int>,
        private var useMiles: Boolean
    ) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

        var selectedPosition = -1

        fun setUseMiles(newUseMiles: Boolean) {
            useMiles = newUseMiles
        }

        inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(android.R.id.text1)

            init {
                itemView.setOnClickListener {
                    val adapterPosition = adapterPosition
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val previousSelectedPosition = selectedPosition
                        selectedPosition = adapterPosition
                        notifyItemChanged(previousSelectedPosition)
                        notifyItemChanged(selectedPosition)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_city, parent, false)
            return CityViewHolder(view)
        }

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            val city = cities[position]
            val distance = distances.getOrNull(position) ?: 0
            val distanceText = if (useMiles) {
                String.format("%.2f", distance * 0.621371) + " miles"
            } else {
                "$distance km"
            }

            holder.textView.text = "$city ($distanceText)"

            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_item_background_color))
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        override fun getItemCount(): Int {
            return cities.size
        }
    }
}