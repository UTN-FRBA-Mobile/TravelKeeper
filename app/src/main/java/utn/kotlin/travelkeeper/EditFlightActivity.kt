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
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import utn.kotlin.travelkeeper.utils.createCalendar
import utn.kotlin.travelkeeper.utils.dateToString
import java.util.*

class EditFlightActivity : AppCompatActivity() {

    private lateinit var previousAirline: String
    private lateinit var previousFlightNumber: String
    private lateinit var previousDepartureAirport: String
    private lateinit var previousArrivalAirport: String
    private lateinit var previousStartDate: Date
    private lateinit var previousReservationNumber: String
    private var previousHour: Int = 0
    private var previousMinutes: Int = 0

    private lateinit var startDate: Date
    private var hourOfFlight: Int = 0
    private var minuteOfFlight: Int = 0

    private lateinit var destination: TripTimeLineInfo
    private lateinit var trip: Trip
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_flight)

        destination = intent.extras["DEST_EDIT"] as TripTimeLineInfo
        trip = intent.extras["TRIP_DEST_EDIT"] as Trip
        position = intent.extras["EDIT_DEST_POSITION"] as Int

        configureActionBar()
        setPreviousData()
        setFlightData()
        setEditButton()
    }

    private fun setPreviousData() {
        previousAirline = destination.name
        previousStartDate = destination.start_date

        previousFlightNumber = "Prueba" //todo: cambiar
        previousDepartureAirport = "Prueba"
        previousArrivalAirport = "Prueba"
        previousReservationNumber = "Prueba"
        previousHour = 5
        previousMinutes = 5
    }

    private fun configureActionBar() {
        this.supportActionBar!!.setTitle(R.string.edit_destination_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun setFlightData() {
        flight_airline_edit.setText(previousAirline)
        startDate = previousStartDate
        setDatePicker()
        setTimePicker()

        flight_number_edit.setText(previousFlightNumber)
        flight_reservation_number_edit.setText(previousReservationNumber)
        flight_arrival_airport_edit.setText(previousArrivalAirport)
        flight_departure_airport_edit.setText(previousDepartureAirport)
    }

    private fun setTimePicker() {
        //todo: ver como sacar la hora y minuti inicial desde el stardate

        hourOfFlight = previousHour
        minuteOfFlight = previousMinutes

        val minutoFormateado = if (minuteOfFlight < 10) "0$minuteOfFlight" else "$minuteOfFlight"
        val horaFormateada = "$hourOfFlight:$minutoFormateado"
        flight_takeoff_hour.setText(horaFormateada)

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
            timePickerDialog.setInitialSelection(hourOfFlight, minuteOfFlight)

            timePickerDialog.show(this.supportFragmentManager, "Hora de salida")
        }

    }

    private fun setDatePicker() {
        startDate.also { flight_takeoff_date.setText(startDate.dateToString()) }

        flight_takeoff_date.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    (it as EditText).setText(calendar.time.dateToString())

                    startDate = calendar.time
                },

                startDate.createCalendar().get(Calendar.YEAR),
                startDate.createCalendar().get(Calendar.MONTH),
                startDate.createCalendar().get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.minDate = trip.startDate.createCalendar()
            datePickerDialog.maxDate = trip.endDate.createCalendar()

            datePickerDialog.show(this.supportFragmentManager, "Fecha de salida")
        }
    }

    private fun showAlertIfNeeded() {
        if (anyItemHasChanged()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.leave_activity_alert)
            builder.setPositiveButton(R.string.yes) { _, _ -> onBackPressed() }
            builder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            builder.create().show()
            return
        }

        onBackPressed()
    }

    private fun anyItemHasChanged(): Boolean {
        if (previousAirline != flight_airline_edit.text.toString())
            return true
        if (previousStartDate != startDate)
            return true
        if (previousFlightNumber != flight_number_edit.text.toString())
            return true
        if (previousDepartureAirport != flight_departure_airport_edit.text.toString())
            return true
        if (previousArrivalAirport != flight_arrival_airport_edit.text.toString())
            return true
        if (previousReservationNumber != flight_reservation_number_edit.text.toString())
            return true
//        if(previousHour != TODO validar hora y minutos si cambia
//        previousMinutes = 5

        return false
    }

    private fun setEditButton() {
        flight_done_button.setOnClickListener { view ->
            if (isDataComplete()) {
                val editDest = TripTimeLineInfo(
                    destination.id,
                    flight_airline_edit.text.toString(),
                    "Vuelo",
                    startDate,
                    startDate
                )

                editDestinationInFirebase(editDest)
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

        return true
    }

    private fun editDestinationInFirebase(dest: TripTimeLineInfo) {
        ViajesService().editDestinationInTrip(trip.id!!, dest,
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    Toast.makeText(this@EditFlightActivity, "Vuelo editado", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@EditFlightActivity, TripTimeLineActivity::class.java)
                    intent.putExtra("DEST_EDIT", dest)
                    intent.putExtra("EDIT_DEST_POSITION", position)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@EditFlightActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
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