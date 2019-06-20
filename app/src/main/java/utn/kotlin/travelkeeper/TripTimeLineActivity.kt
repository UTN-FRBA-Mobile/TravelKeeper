package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_trip_time_line.*
import kotlinx.android.synthetic.main.view_trip_time_line.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.adapters.TripTimeLineAdapter
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.time.Instant
import java.util.*
import android.widget.AdapterView.AdapterContextMenuInfo
import kotlinx.android.synthetic.main.content_trip_time_line.*


class TripTimeLineActivity : AppCompatActivity() {
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private lateinit var destinations: MutableList<TripTimeLineInfo>
    private lateinit var trip: Trip
    private lateinit var destinationSelected: View
    val NEW_DESTINATION_REQUEST = 1
    val EDIT_DESTINATION_REQUEST = 2
    val EDIT_DESTINATION_INTENT = 2

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

                viewManager = androidx.recyclerview.widget.LinearLayoutManager(this@TripTimeLineActivity)
                viewAdapter = TripTimeLineAdapter(destinations, trip)

                recyclerView = trip_timeline_recycler_view.apply {
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

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        destinationSelected = v!!
        menuInflater.inflate(R.menu.options_floating_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.edit_option -> {
                val position = destinationSelected.tag as Int
                this.showEditDestinationActivity(position)
                return true
            }
            R.id.delete_option -> {
                Toast.makeText(this, "WASTED", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                return super.onContextItemSelected(item)
            }
        }
    }

    private fun showEditDestinationActivity(position: Int) {
        val editDestIntent = Intent(this@TripTimeLineActivity, EditDestinationActivity::class.java)
        editDestIntent.putExtra("DEST_EDIT", destinations[position])
        editDestIntent.putExtra("TRIP_DEST_EDIT", trip)
        editDestIntent.putExtra("EDIT_DEST_POSITION", position)
        this@TripTimeLineActivity.startActivityForResult(editDestIntent, EDIT_DESTINATION_INTENT)
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
        else if (requestCode == EDIT_DESTINATION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.extras != null && data!!.extras.size() > 0) {
                    val editDest = data!!.extras["DEST_EDIT"] as TripTimeLineInfo
                    val position = data!!.extras["EDIT_DEST_POSITION"] as Int
                    if (editDest != null) {
                        no_destinations.visibility = View.GONE
                        destinations[position] = editDest
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
