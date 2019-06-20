package utn.kotlin.travelkeeper

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_destination.*
import kotlinx.android.synthetic.main.activity_trip_time_line.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.text.SimpleDateFormat
import java.util.*

class EditDestinationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var destTypes = arrayOf("Lugar","Vuelo")
    private lateinit var selectedDestType: String
    private lateinit var destination: TripTimeLineInfo
    private lateinit var destTypesArrayAdapter: ArrayAdapter<Any>
    private var cal = Calendar.getInstance()
    private var startDate : Date? = null
    private var endDate : Date? = null
    private lateinit var trip: Trip
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_destination)

        destination = intent.extras["DEST_EDIT"] as TripTimeLineInfo
        trip = intent.extras["TRIP_DEST_EDIT"] as Trip
        position = intent.extras["EDIT_DEST_POSITION"] as Int

        setBackArrow()
        setDestTypeSpinner()
        setView()
        setEditDestinationButton()
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle(R.string.edit_destination_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun setDestTypeSpinner() {
        spinner_destination_type!!.setOnItemSelectedListener(this)
        destTypesArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destTypes)
        destTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_destination_type!!.setAdapter(destTypesArrayAdapter)
    }

    private fun setView() {
        enter_destination_name.setText(destination.name)
        selectedDestType = destination.type
        spinner_destination_type.setSelection(destTypesArrayAdapter.getPosition(destination.type))
        cal.time = destination.start_date
        startDate = destination.start_date
        endDate = destination.end_date
        destination_start_date_selected!!.text = getDate(destination.start_date)
        destination_end_date_selected!!.text = getDate(destination.end_date)
        setStartDatePicker()
        setEndDatePicker()
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
            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
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
            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun getDate(date: Date): String {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(date)
    }

    private fun setEditDestinationButton() {
        destination_button_id.setOnClickListener { view ->
            val editDest = TripTimeLineInfo(destination.id, enter_destination_name.text.toString(), selectedDestType, startDate!!, endDate!!)

            val intent = Intent(this@EditDestinationActivity, TripTimeLineActivity::class.java)
            intent.putExtra("EXTRA_EDIT_DEST", editDest)
            setResult(Activity.RESULT_OK, intent)

            editDestinationInFirebase(editDest)
        }
    }

    private fun editDestinationInFirebase(dest: TripTimeLineInfo) {
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

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        selectedDestType = destTypes[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}
