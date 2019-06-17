package utn.kotlin.travelkeeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import utn.kotlin.travelkeeper.DBServices.ViajesService

class TripDashboardActivity : AppCompatActivity(), EditTripNameDialog.EditTitleDialogListener {

    private lateinit var viajesService: ViajesService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_dashboard)

        viajesService = ServiceProvider.viajesService

        val destinationId = intent.getStringExtra("DESTINATION_ID")
        viajesService.getAccomodationFromDestination(destinationId)


        val cityTitle = findViewById<TextView>(R.id.city_title)
        cityTitle.text = (intent.getStringExtra("cityName"))
        val editButton = findViewById<Button>(R.id.edit_name)
        editButton.setOnClickListener {
            val dialog = EditTripNameDialog()
            dialog.show(supportFragmentManager, "DialogTitle")
        }
    }

    override fun onDialogPositiveClick(title: String) {
        findViewById<TextView>(R.id.city_title).text = title

    }

    override fun onDialogNegativeClick(dialog: androidx.fragment.app.DialogFragment) {
        // User touched the dialog's negative button
    }

}