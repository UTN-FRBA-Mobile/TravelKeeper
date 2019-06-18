package utn.kotlin.travelkeeper

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_destination.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class NewDestinationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var destTypes = arrayOf("Lugar", "Vuelo")
    private var selectedDestType = "Lugar"
    private var cal = Calendar.getInstance()
    private lateinit var trip: Trip
    private lateinit var tripDate: LocalDate
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_destination)

        trip = intent.extras["TRIP"] as Trip
        cal.time = trip.startDate

        setBackArrow()
        setDestTypeSpinner()
        setStartDatePicker()
        setEndDatePicker()
        setNewDestinationButton()
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle(R.string.new_destination_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun setDestTypeSpinner() {
        spinner_destination_type!!.setOnItemSelectedListener(this)
        val destTypesArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destTypes)
        destTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_destination_type!!.setAdapter(destTypesArrayAdapter)
    }

    private fun setStartDatePicker() {
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                startDate = cal.time
                destination_start_date_selected!!.text = getDate(startDate!!)
            }
        }

        destination_start_date_selected!!.setOnClickListener { view ->
            DatePickerDialog(
                this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun setEndDatePicker() {
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                endDate = cal.time
                destination_end_date_selected!!.text = getDate(endDate!!)
            }
        }

        destination_end_date_selected!!.setOnClickListener { view ->
            DatePickerDialog(
                this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun getDate(date: Date): String {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(date)
    }

    private fun setNewDestinationButton() {
        destination_button_id.setOnClickListener { view ->
            val newDest = TripTimeLineInfo(null, enter_destination_name.text.toString(), selectedDestType, startDate!!, endDate!!)

            if(isValid(newDest)) {
                enter_name_error.visibility = View.GONE
                start_date_error.visibility = View.GONE
                end_date_error.visibility = View.GONE
                addDestinationToFirebase(newDest)
            }
        }
    }

    private fun isValid(dest: TripTimeLineInfo): Boolean {
        var valid = true

        if (enter_destination_name.text == null || enter_destination_name.text.toString() == "") {
            enter_name_error.visibility = View.VISIBLE
            valid = false
        }

        if(destination_start_date_selected == null || destination_start_date_selected.text.toString() == "" || destination_start_date_selected.text.toString() == "Selecciona una fecha") {
            start_date_error.visibility = View.VISIBLE
            valid = false
        }

        if(destination_end_date_selected == null || destination_end_date_selected.text.toString() == "" || destination_end_date_selected.text.toString() == "Selecciona una fecha") {
            end_date_error.visibility = View.VISIBLE
            valid = false
        }

        if(startDate != null && endDate != null && endDate!! < startDate!!) {
            end_date_error.setText(R.string.end_date_before_start_date)
            end_date_error.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

    private fun addDestinationToFirebase(dest: TripTimeLineInfo) {
        ViajesService().addDestinationToTrip(trip.id!!, dest,
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    Toast.makeText(this@NewDestinationActivity, "Destino agregado", Toast.LENGTH_LONG).show()
                    dest.id = idCreated

                    val intent = Intent(this@NewDestinationActivity, TripTimeLineActivity::class.java)
                    intent.putExtra("EXTRA_NEW_DEST", dest)
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

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        selectedDestType = destTypes[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
    }
}
