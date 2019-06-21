package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_accommodations_list.*
import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.adapters.AccommodationAdapter
import utn.kotlin.travelkeeper.models.Accommodation

class AccommodationsListActivity : AppCompatActivity() {
    private lateinit var accommodationService: AccommodationService
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var accommodations: MutableList<Accommodation>
    private lateinit var destinationId: String
    private lateinit var tripId: String
    val NEW_ACCOMMODATION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodations_list)
        destinationId = intent.getStringExtra("DESTINATION_ID")
        tripId = intent.getStringExtra("TRIP_ID")

        setBackArrow()

        accommodationService = ServiceProvider.accommodationService

        accommodationService.getAccomodationFromDestination(tripId, destinationId, object : AccommodationService.GetAccommodationsViajeServiceListener {
            override fun onSuccess(accommodationsSaved: MutableList<Accommodation>) {
                accommodations = accommodationsSaved
                viewAdapter = AccommodationAdapter(accommodations, tripId, destinationId)
                recyclerView = findViewById<RecyclerView>(R.id.recycler_accommodations).apply {
                    adapter = viewAdapter
                    setHasFixedSize(true)
                }

                add_accommodation_fab.setOnClickListener { view ->
                    val newAcommodationIntent = Intent(this@AccommodationsListActivity, NewAccommodationActivity::class.java)
                    newAcommodationIntent.putExtra("TRIP_ID", tripId)
                    newAcommodationIntent.putExtra("DESTINATION_ID", destinationId)
                    startActivityForResult(newAcommodationIntent, NEW_ACCOMMODATION_REQUEST)
                }

            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@AccommodationsListActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle(R.string.accommodations_list)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == NEW_ACCOMMODATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.extras != null && data!!.extras.size() > 0) {
                    val newAccommodation = data!!.extras["EXTRA_NEW_ACCOMMODATION"] as Accommodation
                    if (newAccommodation != null) {
//                        no_destinations.visibility = View.GONE
                        accommodations.add(newAccommodation)
                        accommodations.sortBy { d1 -> d1.startDate }
                        resetAdapter()
                    }
                }
            }
        }
//        else if (requestCode == EDIT_DESTINATION_REQUEST) {
//            if (resultCode == Activity.RESULT_OK) {
//                if(data!!.extras != null && data!!.extras.size() > 0) {
//                    val editDest = data!!.extras["DEST_EDIT"] as TripTimeLineInfo
//                    val position = data!!.extras["EDIT_DEST_POSITION"] as Int
//                    if (editDest != null) {
//                        no_destinations.visibility = View.GONE
//                        destinations[position] = editDest
//                        destinations.sortBy { d1 -> d1.start_date }
//                        resetAdapter()
//                    }
//                }
//            }
//        }
    }

    private fun resetAdapter() {
        var preAdapter = recyclerView.adapter
        recyclerView.adapter = null
        recyclerView.adapter = preAdapter
    }


}