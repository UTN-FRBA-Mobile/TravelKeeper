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
import kotlinx.android.synthetic.main.activity_view_destination.*
import kotlinx.android.synthetic.main.destination_view.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Destination
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.utils.InternetConnection
import utn.kotlin.travelkeeper.utils.createCalendar
import utn.kotlin.travelkeeper.utils.toStringDateOnly
import java.util.*

class EditDestinationActivity : AppCompatActivity() {

    private lateinit var previousStartDate: Date
    private lateinit var previousEndDate: Date
    private lateinit var previousDestinationDescription: String

    private lateinit var startDate: Date
    private lateinit var endDate: Date

    private lateinit var destination: Destination
    private lateinit var trip: Trip
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_destination)

        destination = intent.extras["DEST_EDIT"] as Destination
        trip = intent.extras["TRIP_DEST_EDIT"] as Trip
        position = intent.extras["EDIT_DEST_POSITION"] as Int

        configureActionBar()
        setPreviousData()
        setDestinationData()
        setEditDestinationButton()
    }

    private fun setPreviousData() {
        previousDestinationDescription = destination.name!!
        previousStartDate = destination.startDate!!
        previousEndDate = destination.endDate!!
    }

    private fun configureActionBar() {
        this.supportActionBar!!.setTitle(R.string.edit_destination_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun setDestinationData() {
        destination_edit.setText(destination.name)
        startDate = destination.startDate!!
        endDate = destination.endDate!!
        setStartDate()
        setEndDate()
    }

    private fun setStartDate() {
        startDate.also { from_date_edit.setText(startDate.toStringDateOnly()) }

        from_date_edit.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    (it as EditText).setText(calendar.time.toStringDateOnly())

                    startDate = calendar.time
                },

                startDate.createCalendar().get(Calendar.YEAR),
                startDate.createCalendar().get(Calendar.MONTH),
                startDate.createCalendar().get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.minDate = trip.startDate.createCalendar()
            datePickerDialog.maxDate = trip.endDate.createCalendar()

            datePickerDialog.show(this.supportFragmentManager, "Fecha de llegada")
        }
    }

    private fun setEndDate() {
        endDate.also { to_date_edit.setText(endDate.toStringDateOnly()) }

        to_date_edit.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    (it as EditText).setText(calendar.time.toStringDateOnly())

                    endDate = calendar.time
                },

                endDate.createCalendar().get(Calendar.YEAR),
                endDate.createCalendar().get(Calendar.MONTH),
                endDate.createCalendar().get(Calendar.DAY_OF_MONTH)
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
        if (previousDestinationDescription != destination_edit.text.toString())
            return true
        if (previousEndDate != endDate)
            return true
        if (previousStartDate != startDate)
            return true
        return false
    }

    private fun setEditDestinationButton() {
        done_destination_button_id.setOnClickListener { view ->
            if (dataIsValid()) {
                val editDest = Destination(
                    destination_edit.text.toString(),
                    startDate,
                    endDate,
                    destination.id
                )

                if(InternetConnection.verifyAvailableNetwork(this)) {
                    editDestinationInFirebase(editDest)
                }
                else {
                    InternetConnection.alertNoInternet(this)
                }
            }

        }
    }

    private fun dataIsValid(): Boolean {
        if (destination_edit.text.isNullOrBlank()) {
            Toast.makeText(this, "Especifique un destino", Toast.LENGTH_LONG).show()
            return false
        }

        if (startDate >= endDate) {
            Toast.makeText(this, "La fecha de fin debe ser mayor a la fecha de inicio", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun editDestinationInFirebase(dest: Destination) {
        ViajesService().editDestinationInTrip(trip.id!!, dest,
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    Toast.makeText(this@EditDestinationActivity, "Destino editado", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@EditDestinationActivity, TripTimeLineActivity::class.java)
                    intent.putExtra("DEST_EDIT", dest)
                    intent.putExtra("EDIT_DEST_POSITION", position)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@EditDestinationActivity, exception.message, Toast.LENGTH_LONG).show()
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
