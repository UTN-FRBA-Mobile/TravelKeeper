package utn.kotlin.travelkeeper

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
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import utn.kotlin.travelkeeper.utils.DatePicker
import utn.kotlin.travelkeeper.utils.dateToString
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
//        cal.time = trip.startDate

        setBackArrow()
        setNewDestinationButton()

        val calendar = Calendar.getInstance() //todo: ver el tema de Locale-Instance al pedir la instancia
        from_date_edit.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText, false)
            DatePicker.showDialog(onDateSetListener, calendar, this)
        }

        to_date_edit.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText, true)
            DatePicker.showDialog(onDateSetListener, calendar, this)
        }

//        destination_edit.setOnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                saveDestinationText((v as TextView).text.toString(), position)
//            }
//        }
//
//        destination_edit.setOnEditorActionListener { v, actionId, event ->
//            /* https://stackoverflow.com/questions/8063439/android-edittext-finished-typing-event */
//            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                actionId == EditorInfo.IME_ACTION_DONE ||
//                event != null &&
//                event.action == KeyEvent.ACTION_DOWN &&
//                event.keyCode == KeyEvent.KEYCODE_ENTER
//            ) {
//                if (event == null || !event.isShiftPressed) {
//                    true
//                }
//            }
//            false
//        }


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
            selectedDate.setText(calendar.time.dateToString())

            if (isEndDate) endDate = calendar.time else startDate = calendar.time
        }
    }

    private fun setNewDestinationButton() {
        done_destination_button_id.setOnClickListener { view ->
//            if (isValid()) {
                val newDest = TripTimeLineInfo(
                    name = destination_edit.text.toString(),
                    type = "Lugar",
                    start_date = startDate!!,
                    end_date = endDate!!
                )
//                enter_name_error.visibility = View.GONE
//                start_date_error.visibility = View.GONE
//                end_date_error.visibility = View.GONE
                addDestinationToFirebase(newDest)
//            }
        }
    }

//    private fun isValid(): Boolean {
//        var valid = true
//
//        if (enter_destination_name.text == null || enter_destination_name.text.toString() == "") {
//            enter_name_error.visibility = View.VISIBLE
//            valid = false
//        }
//
//        if (destination_start_date_selected == null || destination_start_date_selected.text.toString() == "" || destination_start_date_selected.text.toString() == "Seleccione una fecha") {
//            start_date_error.visibility = View.VISIBLE
//            valid = false
//        }
//
//        if (destination_end_date_selected == null || destination_end_date_selected.text.toString() == "" || destination_end_date_selected.text.toString() == "Seleccione una fecha") {
//            end_date_error.visibility = View.VISIBLE
//            valid = false
//        }
//
//        if (startDate != null && endDate != null && endDate!! < startDate!!) {
//            end_date_error.setText(R.string.end_date_before_start_date)
//            end_date_error.visibility = View.VISIBLE
//            valid = false
//        }
//
//        return valid
//    }

    private fun addDestinationToFirebase(destination: TripTimeLineInfo) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //todo: verificar que hace esto
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
