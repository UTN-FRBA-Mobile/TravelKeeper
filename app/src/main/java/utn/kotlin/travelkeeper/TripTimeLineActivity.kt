package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_trip_time_line.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.adapters.TripTimeLineAdapter
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.time.Instant
import java.util.*

class TripTimeLineActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var destinations: MutableList<TripTimeLineInfo>
    private lateinit var trip: Trip
    val NEW_DESTINATION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_time_line)
        setSupportActionBar(trip_timeline_toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        trip = intent.extras["TRIP"] as Trip

        ViajesService().getDestinationsFromTrip(trip.id!!,
            object : ViajesService.GetDestinationsViajeServiceListener {
            override fun onSuccess(dests: MutableList<TripTimeLineInfo>) {
                destinations = dests

                if(destinations != null && destinations.size > 0) {
                    no_destinations.visibility = View.GONE
                }

                viewManager = LinearLayoutManager(this@TripTimeLineActivity)
                viewAdapter = TripTimeLineAdapter(destinations, trip)

                recyclerView = findViewById<RecyclerView>(R.id.trip_timeline_recycler_view).apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter

                    fab.setOnClickListener { view ->
                        val newDestinationIntent = Intent(context, NewDestinationActivity::class.java)
                        newDestinationIntent.putExtra("TRIP", trip)
                        startActivityForResult(newDestinationIntent, NEW_DESTINATION_REQUEST)
                    }
                }
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@TripTimeLineActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == NEW_DESTINATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.extras != null && data!!.extras.size() > 0) {
                    val newDest = data!!.extras["EXTRA_NEW_DEST"] as TripTimeLineInfo
                    if (newDest != null) {
                        no_destinations.visibility = View.GONE
                        destinations.add(newDest)
                        destinations.sortBy { d1 -> d1.start_date }
                        resetAdapter()
                    }
                }
            }
        }
    }

    private fun resetAdapter() {
        var preAdapter = recyclerView.adapter
        recyclerView.adapter = null
        recyclerView.adapter = preAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
