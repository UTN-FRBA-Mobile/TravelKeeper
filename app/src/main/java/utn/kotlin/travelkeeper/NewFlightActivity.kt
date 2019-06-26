package utn.kotlin.travelkeeper

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_flight.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import utn.kotlin.travelkeeper.utils.DatePicker
import utn.kotlin.travelkeeper.utils.dateToString
import java.util.*

class NewFlightActivity : AppCompatActivity() {
    private var startDate: Date? = null
    private lateinit var trip: Trip
    private lateinit var viajesService: ViajesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_flight)

        trip = intent.extras["TRIP"] as Trip
        viajesService = ServiceProvider.viajesService


        setDoneButton()

        val calendar = Calendar.getInstance() //todo: ver el tema de Locale-Instance al pedir la instancia
        flight_takeoff_date.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText)
            DatePicker.showDialog(onDateSetListener, calendar, this)
        }


    }

    private fun onDateSetListener(
        calendar: Calendar,
        selectedDate: EditText
    ): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate.setText(calendar.time.dateToString())

            startDate = calendar.time
        }
    }

    private fun setDoneButton() {
        flight_done_button.setOnClickListener { v ->
            //checkDataIsComplete TODO

            val flight = TripTimeLineInfo(
                name = flight_airline_edit.text.toString(),
                type = "Vuelo",
                start_date = startDate!!,
                end_date = startDate!!
            )
            addFlightToTrip(flight)
        }
    }

    private fun addFlightToTrip(flight: TripTimeLineInfo) {
        viajesService.addDestinationToTrip(trip.id!!, flight,
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    Toast.makeText(this@NewFlightActivity, "Vuelo agregado", Toast.LENGTH_LONG).show()
//                    destination.id = idCreated

                    val intent = Intent(this@NewFlightActivity, TripTimeLineActivity::class.java)
                    intent.putExtra("EXTRA_NEW_DEST", flight)
                    setResult(Activity.RESULT_OK, intent)

                    finish()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@NewFlightActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }


}