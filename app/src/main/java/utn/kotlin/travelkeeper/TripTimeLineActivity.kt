package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_trip_time_line.*
import utn.kotlin.travelkeeper.DBServices.FlightService
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.adapters.TripTimeLineAdapter
import utn.kotlin.travelkeeper.interfaces.TripTimeLineInterface
import utn.kotlin.travelkeeper.models.*

class TripTimeLineActivity : AppCompatActivity(), TripTimeLineInterface {
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private lateinit var tripElements: MutableList<TripElement>
    private lateinit var trip: Trip
    private lateinit var destinationSelected: View
    val NEW_DESTINATION_REQUEST = 1
    val EDIT_DESTINATION_REQUEST = 2
    //    val EDIT_DESTINATION_INTENT = 2
    val NEW_FLIGHT_REQUEST = 3
    val EDIT_FLIGHT_REQUEST = 4

    private lateinit var viajesService: ViajesService
    private lateinit var flightService: FlightService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_time_line)
        setSupportActionBar(trip_timeline_toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        viajesService = ServiceProvider.viajesService
        flightService = ServiceProvider.flightService

        trip = intent.extras["TRIP"] as Trip

        title = trip.title

        tripElements = mutableListOf()

        viajesService.getDestinationsFromTrip(
            trip.id!!,
            object : ViajesService.GetDestinationsListener {
                override fun onSuccess(destinations: MutableList<Destination>) {
                    tripElements.addAll(destinations)

                    flightService.getAllFromTrip(trip.id!!, object : FlightService.GetFlightsListener {
                        override fun onSuccess(flights: MutableList<Flight>) {
                            tripElements.addAll(flights)
                            sortElementsByStartDate()

                            loading.visibility = View.GONE

                            if (tripElements.size == 0) {
                                no_destinations.visibility = View.VISIBLE
                            }

                            configureRecyclerView()
                        }

                        override fun onError(exception: Exception) {
                            onFirebaseError(exception)
                        }

                    })
                }

                override fun onError(exception: Exception) {
                    onFirebaseError(exception)
                }
            })

        add_destination_fab.setOnClickListener {
            floating_actions_menu.collapse()
            val newDestinationIntent = Intent(this@TripTimeLineActivity, NewDestinationActivity::class.java)
            newDestinationIntent.putExtra("TRIP", trip)
            startActivityForResult(newDestinationIntent, NEW_DESTINATION_REQUEST)
        }

        add_flight_fab.setOnClickListener {
            floating_actions_menu.collapse()
            val newDestinationIntent = Intent(this@TripTimeLineActivity, NewFlightActivity::class.java)
            newDestinationIntent.putExtra("TRIP", trip)
            startActivityForResult(newDestinationIntent, NEW_FLIGHT_REQUEST)
        }

    }

    private fun onFirebaseError(exception: Exception) {
        loading.visibility = View.GONE
        no_destinations.visibility = View.VISIBLE
        Toast.makeText(this@TripTimeLineActivity, exception.message, Toast.LENGTH_LONG).show()
    }

    private fun configureRecyclerView() {
        viewManager = LinearLayoutManager(this@TripTimeLineActivity)
        viewAdapter = TripTimeLineAdapter(tripElements, trip, this)

        recyclerView = trip_timeline_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
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
                val destinationOrFlight = tripElements[position]
                when (destinationOrFlight.getType()) {
                    TripElementType.DESTINATION -> showEditDestinationActivity(
                        destinationOrFlight as Destination,
                        position
                    )
                    TripElementType.FLIGHT -> showEditFlightActivity(destinationOrFlight as Flight, position)
                }
                return true
            }
            R.id.delete_option -> {
                val position = destinationSelected.tag as Int
                val destinationOrFlight = tripElements[position]
                when (destinationOrFlight.getType()) {
                    TripElementType.DESTINATION -> showAlertForDeleteDestination(
                        destinationOrFlight as Destination,
                        position
                    )
                    TripElementType.FLIGHT -> deleteFlight(destinationOrFlight as Flight, position)
                }
                return true
            }
            else -> {
                return super.onContextItemSelected(item)
            }
        }
    }

    private fun deleteFlight(flight: Flight, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.remove_flight)
        builder.setPositiveButton(R.string.yes) { _, _ -> deleteFlightFromFirebase(flight, position) }
        builder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun deleteFlightFromFirebase(flight: Flight, position: Int) {
        flightService.delete(
            trip.id!!,
            flight.id!!,
            object : FlightService.EditOrDeleteFlightListener {
                override fun onSuccess() {
                    Toast.makeText(this@TripTimeLineActivity, R.string.flight_removed, Toast.LENGTH_SHORT)
                        .show()
                    tripElements.removeAt(position)
                    resetAdapter()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@TripTimeLineActivity, R.string.flight_not_removed, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    override fun showEditFlightActivity(flight: Flight, position: Int) {
        val editDestIntent = Intent(this, EditFlightActivity::class.java)
        editDestIntent.putExtra("FLIGHT_EDIT", flight)
        editDestIntent.putExtra("TRIP_DEST_EDIT", trip)
        editDestIntent.putExtra("EDIT_FLIGHT_POSITION", position)
        startActivityForResult(editDestIntent, EDIT_FLIGHT_REQUEST)
    }

    override fun showEditDestinationActivity(destination: Destination, position: Int) {
        val editDestIntent = Intent(this, EditDestinationActivity::class.java)
        editDestIntent.putExtra("DEST_EDIT", destination)
        editDestIntent.putExtra("TRIP_DEST_EDIT", trip)
        editDestIntent.putExtra("EDIT_DEST_POSITION", position)
        startActivityForResult(editDestIntent, EDIT_DESTINATION_REQUEST)
    }

    private fun showAlertForDeleteDestination(destination: Destination, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.remove_destination)
        builder.setPositiveButton(R.string.yes) { _, _ -> deleteDestinationFromFirebase(destination, position) }
        builder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun deleteDestinationFromFirebase(destination: Destination, position: Int) {
        viajesService.deleteDestinationInTrip(
            trip.id!!,
            destination.id!!,
            object : UsuariosService.SimpleServiceListener {
                override fun onSuccess() {
                    Toast.makeText(this@TripTimeLineActivity, R.string.destination_removed, Toast.LENGTH_SHORT)
                        .show()
                    tripElements.removeAt(position)
                    resetAdapter()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@TripTimeLineActivity, R.string.destination_not_removed, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to

        when (requestCode) {
            NEW_DESTINATION_REQUEST -> addDestination(resultCode, data)
            EDIT_DESTINATION_REQUEST -> editDestination(resultCode, data)
            NEW_FLIGHT_REQUEST -> addFlight(resultCode, data)
            EDIT_FLIGHT_REQUEST -> editFlight(resultCode, data)
        }
    }

    private fun editFlight(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.extras != null && data!!.extras.size() > 0) {
                val editedFlight = data!!.extras["FLIGHT_EDIT"] as Flight
                val position = data!!.extras["EDIT_FLIGHT_POSITION"] as Int
                no_destinations.visibility = View.GONE
                tripElements[position] = editedFlight
                sortElementsByStartDate()
                resetAdapter()
            }
        }
    }

    private fun addFlight(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.extras != null && data!!.extras.size() > 0) {
                val newFlight = data!!.extras["EXTRA_NEW_FLIGHT"] as Flight
                no_destinations.visibility = View.GONE
                tripElements.add(newFlight)
                sortElementsByStartDate()
                resetAdapter()
            }
        }
    }

    private fun editDestination(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.extras != null && data!!.extras.size() > 0) {
                val editDest = data!!.extras["DEST_EDIT"] as Destination
                val position = data!!.extras["EDIT_DEST_POSITION"] as Int
                no_destinations.visibility = View.GONE
                tripElements[position] = editDest
                sortElementsByStartDate()
                resetAdapter()
            }
        }
    }

    private fun addDestination(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.extras != null && data!!.extras.size() > 0) {
                val newDestination = data!!.extras["EXTRA_NEW_DEST"] as Destination
                no_destinations.visibility = View.GONE
                tripElements.add(newDestination)
                sortElementsByStartDate()
                resetAdapter()
            }
        }
    }

    private fun sortElementsByStartDate() {
        tripElements.sortBy { d1 -> d1.getBeginDate() }
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
