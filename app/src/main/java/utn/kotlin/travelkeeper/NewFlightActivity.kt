package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_new_flight.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import utn.kotlin.travelkeeper.utils.addHourAndMinutes
import utn.kotlin.travelkeeper.utils.createCalendar
import utn.kotlin.travelkeeper.utils.dateToString
import java.util.*

class NewFlightActivity : AppCompatActivity() {
    private var startDate: Date? = null
    private var hourOfFlight: Int? = null
    private var minuteOfFlight: Int? = null
    private lateinit var trip: Trip
    private lateinit var viajesService: ViajesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_flight)

        trip = intent.extras["TRIP"] as Trip
        viajesService = ServiceProvider.viajesService

        setDoneButton()
        setDatePickerForTakeoffDate()
        setTimePickerForTakeoffTime()
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
                    (it as EditText).setText(calendar.time.dateToString())

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
            //checkDataIsComplete TODO

            val flight = TripTimeLineInfo(
                name = flight_airline_edit.text.toString(),
                type = "Vuelo",
                start_date = startDate!!.addHourAndMinutes(hourOfFlight!!, minuteOfFlight!!).time,
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