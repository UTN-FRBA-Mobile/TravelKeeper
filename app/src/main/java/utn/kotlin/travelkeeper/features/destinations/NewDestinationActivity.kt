package utn.kotlin.travelkeeper.features.destinations

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_view_destination.*
import kotlinx.android.synthetic.main.destination_view.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.database.ServiceProvider
import utn.kotlin.travelkeeper.database.ViajesService
import utn.kotlin.travelkeeper.domain.Destination
import utn.kotlin.travelkeeper.domain.Trip
import utn.kotlin.travelkeeper.features.trips.TripTimeLineActivity
import utn.kotlin.travelkeeper.utils.DatePicker
import utn.kotlin.travelkeeper.utils.InternetConnection
import utn.kotlin.travelkeeper.utils.toStringDateOnly
import java.util.*

class NewDestinationActivity : AppCompatActivity() {
    private lateinit var trip: Trip
    private var startDate: Date? = null
    private var endDate: Date? = null
    private lateinit var viajesService : ViajesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_destination)

        viajesService = ServiceProvider.viajesService

        trip = intent.extras["TRIP"] as Trip

        setBackArrow()
        setNewDestinationButton()

        val calendar = Calendar.getInstance()
        from_date_edit.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText, false)
            DatePicker.showDialog(onDateSetListener, calendar, this)
        }

        to_date_edit.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText, true)
            DatePicker.showDialog(onDateSetListener, calendar, this)
        }

    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle(R.string.new_destination_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }


    private fun onDateSetListener(
        calendar: Calendar,
        selectedDate: EditText,
        isEndDate: Boolean
    ): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate.setText(calendar.time.toStringDateOnly())

            if (isEndDate) endDate = calendar.time else startDate = calendar.time
        }
    }

    private fun setNewDestinationButton() {
        done_destination_button_id.setOnClickListener { view ->
            val newDest = Destination(
                    name = destination_edit.text.toString(),
                startDate = startDate!!,
                endDate = endDate!!
                )

                if(InternetConnection.verifyAvailableNetwork(this)) {
                    addDestinationToFirebase(newDest)
                } else {
                    InternetConnection.alertNoInternet(this)
                }
        }
    }

    private fun addDestinationToFirebase(destination: Destination) {
        viajesService.addDestinationToTrip(trip.id!!, destination,
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    Toast.makeText(this@NewDestinationActivity, "Destino agregado", Toast.LENGTH_LONG).show()
                    destination.id = idCreated

                    val intent = Intent(this@NewDestinationActivity, TripTimeLineActivity::class.java)
                    intent.putExtra("EXTRA_NEW_DEST", destination)
                    setResult(Activity.RESULT_OK, intent)

                    finish()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@NewDestinationActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
