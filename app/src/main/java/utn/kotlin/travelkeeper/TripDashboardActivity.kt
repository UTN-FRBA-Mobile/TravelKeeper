package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.models.Accommodation

class TripDashboardActivity : AppCompatActivity(), EditTripNameDialog.EditTitleDialogListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_dashboard)
        val destinationId = intent.getStringExtra("DESTINATION_ID")
        val tripId = intent.getStringExtra("TRIP_ID")

        val cityTitle = findViewById<TextView>(R.id.city_title)
        cityTitle.text = (intent.getStringExtra("cityName"))
        val editButton = findViewById<FloatingActionButton>(R.id.edit_name)
        editButton.setOnClickListener {
            val dialog = EditTripNameDialog()
            dialog.show(supportFragmentManager, "DialogTitle")
        }

        val accommodationButton = findViewById<Button>(R.id.accommodation_button)
        accommodationButton.setOnClickListener{
            val intentAccommodation = Intent(this,AccomodationsListActivity::class.java )
            intentAccommodation.putExtra("DESTINATION_ID", destinationId)
            intentAccommodation.putExtra("TRIP_ID", tripId)
            startActivity(intentAccommodation)
        }
    }

    override fun onDialogPositiveClick(title: String) {
        findViewById<TextView>(R.id.city_title).text = title

    }

    override fun onDialogNegativeClick(dialog: androidx.fragment.app.DialogFragment) {
        // User touched the dialog's negative button
    }

}