package utn.kotlin.travelkeeper

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.DBServices.ViajesService
import java.util.*


class NewTripActivity : AppCompatActivity() {

    private lateinit var tripName: EditText
    private lateinit var destinationsAdapter: DestinationsAdapter
    private lateinit var viajesService: ViajesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        this.supportActionBar!!.setTitle(R.string.new_trip_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        viajesService = ServiceProvider.viajesService

        destinationsAdapter = DestinationsAdapter()

        tripName = findViewById(R.id.enter_trip_name_edit)

        findViewById<RecyclerView>(R.id.new_destination_recycler_view).apply {
            adapter = destinationsAdapter
        }

        val addDestinationButton = findViewById<Button>(R.id.add_destination_button)
        addDestinationButton.setOnClickListener {
            destinationsAdapter.data.add(NewDestination())
            destinationsAdapter.notifyDataSetChanged()
            hideKeyboard(it)
        }

        val doneButton = findViewById<Button>(R.id.done_button)
        doneButton.setOnClickListener {
            if (isDataComplete()) {
                saveNewTrip()
            }
        }
    }

    private fun saveNewTrip() {
        viajesService.createTrip(
            tripName.text.toString(),
            getTripStartDate(),
            getTripEndDate(),
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    UsuariosService().addTripToUser(
                        FirebaseAuth.getInstance().currentUser!!.email!!,
                        idCreated,
                        tripName.text.toString(),
                        getTripStartDate(),
                        getTripEndDate(),
                        object : UsuariosService.SimpleServiceListener {
                            override fun onSuccess() {
                                Toast.makeText(this@NewTripActivity, "Nuevo viaje guardado!", Toast.LENGTH_LONG)
                                    .show()

                                //TODO volver a la pantalla principal: mis viajes
                            }

                            override fun onError(exception: Exception) {
                                Toast.makeText(this@NewTripActivity, exception.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@NewTripActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            }

        )
    }

    private fun getTripEndDate(): Date {
        return destinationsAdapter.data.last().endDate!!
    }

    private fun getTripStartDate(): Date {
        return destinationsAdapter.data[0].startDate!!
    }

    private fun isDataComplete(): Boolean {
        if (!isTripNameComplete()) {
            Toast.makeText(
                this,
                "Falta completar el nombre del viaje",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (!hasAtLeastOneDestination()) {
            Toast.makeText(
                this,
                "Falta especificar al menos un destino",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }

    private fun hasAtLeastOneDestination(): Boolean {
        if (destinationsAdapter.data.isEmpty()) return false
        val firstDestination = destinationsAdapter.data[0]
        return !firstDestination.destination.isNullOrBlank() && firstDestination.endDate != null && firstDestination.startDate != null
        //todo: pasar la validacion de las fechas a otro lado y validar fchas de inicio y fin
    }

    private fun isTripNameComplete() = !tripName.text.isNullOrBlank()

    private fun hideKeyboard(view: View) {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (ignored: Exception) {
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //para back button
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

