package utn.kotlin.travelkeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.adapters.AccommodationAdapter
import utn.kotlin.travelkeeper.models.Accommodation

class AccomodationsListActivity : AppCompatActivity() {
    private lateinit var accommodationService: AccommodationService
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var accommodations: MutableList<Accommodation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodations_list)
        val destinationId = intent.getStringExtra("DESTINATION_ID")
        val tripId = intent.getStringExtra("TRIP_ID")

        accommodationService = ServiceProvider.accommodationService

        accommodationService.getAccomodationFromDestination(tripId, destinationId, object : AccommodationService.GetAccommodationsViajeServiceListener {
            override fun onSuccess(accommodations: MutableList<Accommodation>) {
                viewAdapter = AccommodationAdapter(accommodations, tripId, destinationId)
                recyclerView = findViewById<RecyclerView>(R.id.recycler_accommodations).apply {
                    adapter = viewAdapter
                    setHasFixedSize(true)
                }


            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@AccomodationsListActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })




    }
}