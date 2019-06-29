package utn.kotlin.travelkeeper

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import `in`.madapps.placesautocomplete.model.Place
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_accommodation.*
import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.models.Accommodation
import java.text.SimpleDateFormat
import java.util.*

class EditAccommodationActivity : AppCompatActivity() {
    private var cal = Calendar.getInstance()
    private var startDate: Date? = null
    private var endDate: Date? = null
    private lateinit var destinationId: String
    private lateinit var tripId: String
    private lateinit var accomodation: Accommodation
    private var position: Int = 0

    val placesApi = PlaceAPI.Builder().apiKey("YOUR_API_KEY").build(this@EditAccommodationActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_accommodation)

        accomodation = intent.extras["ACCOMMODATION_EDIT"] as Accommodation
        destinationId = intent.getStringExtra("DESTID_ACCOMMODATION_EDIT")
        tripId = intent.getStringExtra("TRIPID_ACCOMMODATION_EDIT")
        position = intent.extras["EDIT_ACCOMMODATION_POSITION"] as Int

        setBackArrow()
        setView()
        setEditAccommodationButton()
        setSearchAddresses()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle(R.string.accommodations_list)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    private fun setView() {
        enter_accommodation_name.setText(accomodation.name)
        enter_accommodation_address.setText(accomodation.address)

        if(accomodation.reservationCode != null) {
            enter_accommodation_reservation_number.setText(accomodation.reservationCode)
        }

        if(accomodation.telephoneNumber != null) {
            enter_accommodation_telephone_number.setText(accomodation.telephoneNumber)
        }

        cal.time = accomodation.startDate
        startDate = accomodation.startDate
        endDate = accomodation.endDate
        accommodation_checkin_date_selected!!.text = getDate(accomodation.startDate)
        accommodation_checkout_date_selected!!.text = getDate(accomodation.endDate)
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
                accommodation_checkin_date_selected!!.text = getDate(startDate!!)
            }
        }

        accommodation_checkin_date_selected!!.setOnClickListener { view ->
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
                accommodation_checkout_date_selected!!.text = getDate(endDate!!)
            }
        }

        accommodation_checkout_date_selected!!.setOnClickListener { view ->
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

    private fun setEditAccommodationButton() {
//        edit_accommodation_button_id.setOnClickListener { _ ->
//            if(isValid()) {
//                val editAccomodation = Accommodation(accomodation.id, enter_accommodation_name.text.toString(), enter_accommodation_address.text.toString(),
//                    startDate!!, endDate!!, enter_accommodation_telephone_number.text.toString(),
//                    enter_accommodation_reservation_number.text.toString())
//                enter_name_error.visibility = View.GONE
//                checkin_date_error.visibility = View.GONE
//                checkout_date_error.visibility = View.GONE
//                editAccommodationInFirebase(editAccomodation)
//            }
//        }
    }

    private fun setSearchAddresses() {
        enter_accommodation_address.setAdapter(PlacesAutoCompleteAdapter(this, placesApi))
        enter_accommodation_address.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val place = parent.getItemAtPosition(position) as Place
                enter_accommodation_address.setText(place.description)
            }
    }

    private fun isValid(): Boolean {
        var valid = true

        if (enter_accommodation_name.text == null || enter_accommodation_name.text.toString() == "") {
            enter_name_error.visibility = View.VISIBLE
            valid = false
        }

        if(accommodation_checkin_date_selected == null || accommodation_checkin_date_selected.text.toString() == "" || accommodation_checkin_date_selected.text.toString() == "Seleccione una fecha") {
            checkin_date_error.visibility = View.VISIBLE
            valid = false
        }

        if(accommodation_checkout_date_selected == null || accommodation_checkout_date_selected.text.toString() == "" || accommodation_checkout_date_selected.text.toString() == "Seleccione una fecha") {
            checkout_date_error.visibility = View.VISIBLE
            valid = false
        }

        if(startDate != null && endDate != null && endDate!! < startDate!!) {
            checkout_date_error.setText(R.string.end_date_before_start_date)
            checkout_date_error.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

    private fun editAccommodationInFirebase(accommodation: Accommodation) {
        AccommodationService().editAccommodation(tripId, destinationId, accommodation,
            object : AccommodationService.CreateAccommodationServiceListener {
                override fun onSuccess(idCreated: String) {
                    Toast.makeText(this@EditAccommodationActivity, "Alojamiento editado", Toast.LENGTH_LONG).show()

                    val accommodationIntent = Intent(this@EditAccommodationActivity, AccommodationsListActivity::class.java)
                    accommodationIntent.putExtra("ACCOMMODATION_EDIT", accommodation)
                    accommodationIntent.putExtra("ACCOMMODATION_EDIT_DEST_POSITION", position)
                    setResult(Activity.RESULT_OK, accommodationIntent)
                    finish()
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@EditAccommodationActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }
}
