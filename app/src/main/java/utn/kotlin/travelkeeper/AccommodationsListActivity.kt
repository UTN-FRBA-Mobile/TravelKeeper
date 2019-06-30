package utn.kotlin.travelkeeper

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_accommodations_list.*
import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.adapters.AccommodationAdapter
import utn.kotlin.travelkeeper.interfaces.AccommodationListInterface
import utn.kotlin.travelkeeper.models.Accommodation

class AccommodationsListActivity : AppCompatActivity(), AccommodationListInterface {
    private lateinit var accommodationService: AccommodationService
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var accommodations: MutableList<Accommodation>
    private lateinit var destinationId: String
    private lateinit var tripId: String
    private lateinit var accommodationSelected: View
    val NEW_ACCOMMODATION_REQUEST = 1
    val EDIT_ACCOMMODATION_INTENT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodations_list)
        destinationId = intent.getStringExtra("DESTINATION_ID")
        tripId = intent.getStringExtra("TRIP_ID")

        setBackArrow()

        accommodationService = ServiceProvider.accommodationService

        accommodationService.getAccomodationFromDestination(
            tripId,
            destinationId,
            object : AccommodationService.GetAccommodationsViajeServiceListener {
                override fun onSuccess(accommodationsSaved: MutableList<Accommodation>) {
                    accommodations = accommodationsSaved
                    loading.visibility = View.GONE

                    viewAdapter = AccommodationAdapter(this@AccommodationsListActivity, accommodations, tripId, destinationId)
                    recyclerView = findViewById<RecyclerView>(R.id.recycler_accommodations).apply {
                        adapter = viewAdapter
                        setHasFixedSize(true)
                    }

                    if (accommodationsSaved == null || accommodations.size < 1) {
                        empty_view.visibility = View.VISIBLE
                        recycler_accommodations.visibility = View.GONE
                        return
                    }

                    recyclerView.visibility = View.VISIBLE
                }

                override fun onError(exception: Exception) {
                    if (accommodations == null || accommodations.size < 1) {
                        empty_view.visibility = View.VISIBLE
                        recycler_accommodations.visibility = View.GONE
                        return
                    }
                    Toast.makeText(this@AccommodationsListActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })

        add_accommodation_fab.setOnClickListener { view ->
            val newAcommodationIntent =
                Intent(this@AccommodationsListActivity, NewAccommodationActivity::class.java)
            newAcommodationIntent.putExtra("TRIP_ID", tripId)
            newAcommodationIntent.putExtra("DESTINATION_ID", destinationId)
            startActivityForResult(newAcommodationIntent, NEW_ACCOMMODATION_REQUEST)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        accommodationSelected = v!!
        menuInflater.inflate(R.menu.options_floating_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.edit_option -> {
                val position = accommodationSelected.tag as Int
                this.showEditAccommodationActivity(position)
                return true
            }
            R.id.delete_option -> {
                val position = accommodationSelected.tag as Int
                this.deleteAccommodation(position)
                return true
            }
            else -> {
                return super.onContextItemSelected(item)
            }
        }
    }

    override fun showEditAccommodationActivity(position: Int) {
        val editDestIntent = Intent(this@AccommodationsListActivity, EditAccommodationActivity::class.java)
        editDestIntent.putExtra("ACCOMMODATION_EDIT", accommodations[position])
        editDestIntent.putExtra("TRIPID_ACCOMMODATION_EDIT", tripId)
        editDestIntent.putExtra("DESTID_ACCOMMODATION_EDIT", destinationId)
        editDestIntent.putExtra("EDIT_ACCOMMODATION_POSITION", position)
        this@AccommodationsListActivity.startActivityForResult(editDestIntent, EDIT_ACCOMMODATION_INTENT)
    }

    private fun deleteAccommodation(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.remove_accommodation)
        builder.setPositiveButton(
            R.string.yes
        ) { dialog, _ ->
            loading.visibility = View.VISIBLE
            accommodationService.deleteAccommodation(
                tripId,
                destinationId,
                accommodations[position].id!!,
                object : AccommodationService.SimpleServiceListener {
                    override fun onSuccess() {
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@AccommodationsListActivity,
                            R.string.accommodation_removed,
                            Toast.LENGTH_SHORT
                        ).show()
                        accommodations.removeAt(position)
                        resetAdapter()
                    }

                    override fun onError(exception: Exception) {
                        loading.visibility = View.GONE
                        Toast.makeText(
                            this@AccommodationsListActivity,
                            R.string.accommodation_not_removed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
        builder.setNegativeButton(
            R.string.no
        ) { dialog, _ -> dialog.dismiss() }

        builder.create().show()
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
                if (data!!.extras != null && data.extras.size() > 0) {
                    val newAccommodation = data!!.extras["EXTRA_NEW_ACCOMMODATION"] as Accommodation
                    if (newAccommodation != null) {
                        accommodations.add(newAccommodation)
                        accommodations.sortBy { d1 -> d1.startDate }
                        resetAdapter()
                    }
                }
            }
        } else if (requestCode == EDIT_ACCOMMODATION_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.extras != null && data.extras.size() > 0) {
                    val editAccommodation = data!!.extras["ACCOMMODATION_EDIT"] as Accommodation
                    val position = data!!.extras["ACCOMMODATION_EDIT_DEST_POSITION"] as Int
                    if (editAccommodation != null) {
                        accommodations[position] = editAccommodation
                        accommodations.sortBy { d1 -> d1.startDate }
                        resetAdapter()
                    }
                }
            }
        }
    }

    private fun resetAdapter() {
        var preAdapter = recyclerView?.adapter
        recyclerView.adapter = null
        recyclerView.adapter = preAdapter

        if (accommodations == null || accommodations.size < 1) {
            empty_view.visibility = View.VISIBLE
            recycler_accommodations.visibility = View.GONE
        } else {
            empty_view.visibility = View.GONE
            recycler_accommodations.visibility = View.VISIBLE
        }
    }
}