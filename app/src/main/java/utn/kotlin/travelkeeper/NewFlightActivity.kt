package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_view_flight.*
import utn.kotlin.travelkeeper.DBServices.FlightService
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Flight
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.utils.InternetConnection
import utn.kotlin.travelkeeper.utils.addHourAndMinutes
import utn.kotlin.travelkeeper.utils.createCalendar
import utn.kotlin.travelkeeper.utils.toStringDateOnly
import java.util.*


class NewFlightActivity : AppCompatActivity() {
    private var startDate: Date? = null
    private var hourOfFlight: Int? = null
    private var minuteOfFlight: Int? = null
    private lateinit var trip: Trip
    private lateinit var viajesService: ViajesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_flight)

        trip = intent.extras["TRIP"] as Trip
        viajesService = ServiceProvider.viajesService

        setDoneButton()
        setDatePickerForTakeoffDate()
        setTimePickerForTakeoffTime()
        setBackArrow()
        setAirlineSearch()
    }

    private fun setAirlineSearch() {
        flight_airline_edit.setOnClickListener {
            showSearch()
        }
    }

    private val AIRLINE_SELECTED: Int = 1

    private fun showSearch() {
        val intent = Intent(this@NewFlightActivity, AirlineSearchActivity::class.java)
        startActivityForResult(intent, AIRLINE_SELECTED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AIRLINE_SELECTED -> {
                setAirline(resultCode, data)
            }
        }
    }

    private fun setAirline(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.extras != null && data!!.extras.size() > 0) {
                val airline = data!!.extras["FLIGHT_AIRLINE"] as String
                flight_airline_edit.setText(airline)
            }
        }
    }

    private fun setTimePickerForTakeoffTime() {
        flight_takeoff_hour.setOnClickListener {
            val timePickerDialog = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
                val minutoFormateado = if (minute < 10) "0$minute" else "$minute"
                val horaFormateada = "$hourOfDay:$minutoFormateado"
                (it as EditText).setText(horaFormateada)

                hourOfFlight = hourOfDay
                minuteOfFlight = minute
            }, true)


            timePickerDialog.enableMinutes(true)
            timePickerDialog.enableSeconds(false)
            timePickerDialog.setInitialSelection(hourOfFlight ?: 12, minuteOfFlight ?: 0)

            timePickerDialog.show(this.supportFragmentManager, "Hora de salida")
        }

    }

    private fun setDatePickerForTakeoffDate() {
        flight_takeoff_date.setOnClickListener {
            val calendar = Calendar.getInstance() //todo: ver el tema de Locale-Instance al pedir la instancia

            val datePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    (it as EditText).setText(calendar.time.toStringDateOnly())

                    startDate = calendar.time
                },

                startDate?.createCalendar()?.get(Calendar.YEAR) ?: trip.startDate.createCalendar().get(Calendar.YEAR),
                startDate?.createCalendar()?.get(Calendar.MONTH) ?: trip.startDate.createCalendar().get(Calendar.MONTH),
                startDate?.createCalendar()?.get(Calendar.DAY_OF_MONTH)
                    ?: trip.startDate.createCalendar().get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.minDate = trip.startDate.createCalendar()
            datePickerDialog.maxDate = trip.endDate.createCalendar()

            datePickerDialog.show(this.supportFragmentManager, "Fecha de salida")
        }
    }

    private fun setDoneButton() {
        flight_done_button.setOnClickListener { v ->
            if (isDataComplete()) {

                val flight = Flight(
                    airline = flight_airline_edit.text.toString(),
                    flightNumber = flight_number_edit.text.toString(),
                    departureAirport = flight_departure_airport_edit.text.toString(),
                    arrivalAirport = flight_arrival_airport_edit.text.toString(),
                    takeOffDate = startDate!!.addHourAndMinutes(hourOfFlight!!, minuteOfFlight!!),
                    reservationNumber = flight_reservation_number_edit.text.toString()
                )

                if(InternetConnection.verifyAvailableNetwork(this)) {
                    addFlight(flight)
                }
                else {
                    InternetConnection.alertNoInternet(this)
                }
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun isDataComplete(): Boolean {
        if (flight_airline_edit.text.isNullOrBlank()) {
            showToast("Especifique una aerolínea")
            return false
        }
        if (flight_departure_airport_edit.text.isNullOrBlank()) {
            showToast("Especifique un aeropuerto de salida")
            return false
        }

        if (flight_arrival_airport_edit.text.isNullOrBlank()) {
            showToast("Especifique un aeropuerto de llegada")
            return false
        }

        if (flight_number_edit.text.isNullOrBlank()) {
            showToast("Especifique el número de vuelo")
            return false
        }

        if (startDate == null) {
            showToast("Especifique la fecha de salida")
            return false
        }

        if (hourOfFlight == null || minuteOfFlight == null) {
            showToast("Especifique la hora del vuelo")
            return false
        }

        return true
    }

    private fun showAlertIfNeeded() {
        if (anyItemIsNotNull()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.leave_activity_alert)
            builder.setPositiveButton(R.string.yes) { _, _ -> onBackPressed() }
            builder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            builder.create().show()
            return
        }

        onBackPressed()
    }

    private fun anyItemIsNotNull(): Boolean {
        if (!flight_airline_edit.text.isNullOrBlank()) {
            return true
        }
        if (!flight_departure_airport_edit.text.isNullOrBlank()) {
            return true
        }

        if (!flight_arrival_airport_edit.text.isNullOrBlank()) {
            return true
        }

        if (!flight_number_edit.text.isNullOrBlank()) {
            return true
        }

        if (startDate != null) {
            return true
        }

        if (hourOfFlight != null || minuteOfFlight != null) {
            return true
        }

        if (!flight_reservation_number_edit.text.isNullOrBlank()) {
            return true
        }

        return false
    }

    private fun addFlight(flight: Flight) {
        FlightService().add(flight, trip.id!!, object : FlightService.AddFlightListener {
            override fun onSuccess(flightId: String) {

                Toast.makeText(this@NewFlightActivity, "Vuelo agregado", Toast.LENGTH_LONG).show()

                flight.id = flightId

                val intent = Intent(this@NewFlightActivity, TripTimeLineActivity::class.java)
                intent.putExtra("EXTRA_NEW_FLIGHT", flight)
                setResult(Activity.RESULT_OK, intent)

                finish()
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@NewFlightActivity, exception.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle("Nuevo vuelo")
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                showAlertIfNeeded()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}