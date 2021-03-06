package utn.kotlin.travelkeeper.features.accomodations

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.compat.Place
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_accommodation.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.database.AccommodationService
import utn.kotlin.travelkeeper.domain.Accommodation
import utn.kotlin.travelkeeper.utils.InternetConnection
import utn.kotlin.travelkeeper.utils.parserWithFormat
import java.util.*

class NewAccommodationActivity : AppCompatActivity(), PlaceSelectionListener {
    private var cal = Calendar.getInstance()
    private var startDate: Date? = null
    private var endDate: Date? = null
    private lateinit var destinationId: String
    private lateinit var tripId: String
    private var latLngSelected: LatLng = LatLng(0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation)

        destinationId = intent.getStringExtra("DESTINATION_ID")
        tripId = intent.getStringExtra("TRIP_ID")

        setBackArrow()
        setStartDatePicker()
        setEndDatePicker()
        setNewAccommodationButton()
        setSearchAddresses()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        return parserWithFormat(myFormat).format(date)
    }

    private fun setNewAccommodationButton() {
        accommodation_button_id.setOnClickListener { view ->
            if (isValid()) {
                val newAccommodation = Accommodation(
                    null, enter_accommodation_name.text.toString(), enter_accommodation_address.text.toString(),
                    latLngSelected.latitude, latLngSelected.longitude,
                    startDate!!, endDate!!, enter_accommodation_telephone_number.text.toString(),
                    enter_accommodation_reservation_number.text.toString()
                )
                enter_name_error.visibility = View.GONE
                checkin_date_error.visibility = View.GONE
                checkout_date_error.visibility = View.GONE

                if(InternetConnection.verifyAvailableNetwork(this)) {
                    addAccommodationToFirebase(newAccommodation)
                }
                else {
                    InternetConnection.alertNoInternet(this)
                }
            }
        }
    }

    private fun setSearchAddresses() {
        val autocompleteFragment = fragmentManager.findFragmentById(R.id.address_autocomplete_fragment)
                as PlaceAutocompleteFragment
        autocompleteFragment.setOnPlaceSelectedListener(this)
    }

    override fun onPlaceSelected(p0: Place?) {
        if(p0 != null && p0.name != null) {
            enter_accommodation_name.setText(p0!!.name)
        }

        if(p0 != null && p0.address != null) {
            enter_accommodation_address.setText(p0!!.address)
        }

        if(p0 != null && p0.latLng != null) {
            latLngSelected = p0!!.latLng
        }
    }

    override fun onError(status: Status) {
        Toast.makeText(applicationContext,""+status.toString(),Toast.LENGTH_LONG).show();
    }

    private fun isValid(): Boolean {
        var valid = true

        if (enter_accommodation_name.text == null || enter_accommodation_name.text.toString() == "") {
            enter_name_error.visibility = View.VISIBLE
            valid = false
        }

        if (accommodation_checkin_date_selected == null || accommodation_checkin_date_selected.text.toString() == "" || accommodation_checkin_date_selected.text.toString() == "Seleccione una fecha") {
            checkin_date_error.visibility = View.VISIBLE
            valid = false
        }

        if (accommodation_checkout_date_selected == null || accommodation_checkout_date_selected.text.toString() == "" || accommodation_checkout_date_selected.text.toString() == "Seleccione una fecha") {
            checkout_date_error.visibility = View.VISIBLE
            valid = false
        }

        if (startDate != null && endDate != null && endDate!! < startDate!!) {
            checkout_date_error.setText(R.string.end_date_before_start_date)
            checkout_date_error.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

    private fun addAccommodationToFirebase(accommodation: Accommodation) {
        loading.visibility = View.VISIBLE
        AccommodationService().addAccommodationToDestination(tripId, destinationId, accommodation,
            object : AccommodationService.CreateAccommodationServiceListener {
                override fun onSuccess(idCreated: String) {
                    loading.visibility = View.GONE
                    Toast.makeText(this@NewAccommodationActivity, "Alojamiento agregado", Toast.LENGTH_LONG).show()
                    accommodation.id = idCreated

                    val accommodationIntent =
                        Intent(this@NewAccommodationActivity, AccommodationsListActivity::class.java)
                    accommodationIntent.putExtra("EXTRA_NEW_ACCOMMODATION", accommodation)
                    setResult(Activity.RESULT_OK, accommodationIntent)

                    finish()
                }

                override fun onError(exception: Exception) {
                    loading.visibility = View.GONE
                    Toast.makeText(this@NewAccommodationActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }


}
