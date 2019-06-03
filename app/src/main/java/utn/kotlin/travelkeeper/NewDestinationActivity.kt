package utn.kotlin.travelkeeper

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_new_destination.*
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class NewDestinationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var destTypes = arrayOf("Lugar","Vuelo")
    private var selectedDestType = "Lugar"
    private var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_destination)

        setBackArrow()
        setDestTypeSpinner()
        setDatePicker()
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

    private fun setDatePicker() {
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        destination_date_selected!!.setOnClickListener { view ->
            DatePickerDialog(this@NewDestinationActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun updateDateInView() {
        destination_date_selected!!.text = getDate()
    }

    private fun getDate(): String {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(cal.time)
    }

    private fun setNewDestinationButton() {
        destination_button_id.setOnClickListener { view ->
            val newDest = TripTimeLineInfo(enter_destination_name.text.toString(), selectedDestType, cal.time, cal.time)

            val intent = Intent(this@NewDestinationActivity, TripTimeLineActivity::class.java)
            intent.putExtra("EXTRA_NEW_DEST", newDest)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
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
