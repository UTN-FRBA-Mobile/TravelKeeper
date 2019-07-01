package utn.kotlin.travelkeeper.features.trips

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_new_trip.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.database.ServiceProvider
import utn.kotlin.travelkeeper.database.UsuariosService
import utn.kotlin.travelkeeper.database.ViajesService
import utn.kotlin.travelkeeper.domain.Destination
import utn.kotlin.travelkeeper.domain.Trip
import utn.kotlin.travelkeeper.features.destinations.DestinationsAdapter
import utn.kotlin.travelkeeper.utils.InternetConnection
import utn.kotlin.travelkeeper.utils.Keyboard
import java.util.*


class NewTripActivity : AppCompatActivity() {

    private lateinit var tripName: EditText
    private lateinit var destinationsAdapter: DestinationsAdapter
    private lateinit var viajesService: ViajesService
    private lateinit var usuariosService: UsuariosService
    private lateinit var subject: Subject<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        this.supportActionBar!!.setTitle(R.string.new_trip_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        viajesService = ServiceProvider.viajesService
        usuariosService = ServiceProvider.usuariosService
        subject = PublishSubject.create()

        destinationsAdapter = DestinationsAdapter()

        tripName = findViewById(R.id.enter_trip_name_edit)

        findViewById<RecyclerView>(R.id.new_destination_recycler_view).apply {
            adapter = destinationsAdapter
        }

        setHideKeyboardOnEnterTripName()

        val addDestinationButton = findViewById<Button>(R.id.add_destination_button)
        addDestinationButton.setOnClickListener {
            destinationsAdapter.data.add(Destination())
            destinationsAdapter.notifyDataSetChanged()
            Keyboard.hide(it, this)
        }

        val doneButton = findViewById<Button>(R.id.done_button)
        doneButton.setOnClickListener {
            if (isDataComplete()) {
                if (InternetConnection.verifyAvailableNetwork(this)) {
                    saveNewTrip()
                } else {
                    InternetConnection.alertNoInternet(this)
                }
            }
        }

        onAllDestinationsAdded()
    }

    private fun setHideKeyboardOnEnterTripName() {
        enter_trip_name_edit.setOnEditorActionListener(Keyboard.onEnterHideKeyboard(this))
    }

    private lateinit var subscription: Disposable

    override fun onStart() {
        super.onStart()
        subscription = onAllDestinationsAdded().subscribe { showToastWhenAllDestinationsAreAdded(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.dispose()
    }

    private fun saveNewTrip() {
        add_destination_button.isEnabled = false
        done_button.isEnabled = false
        loading.visibility = View.VISIBLE
        viajesService.createTrip(
            tripName.text.toString(),
            getTripStartDate(),
            getTripEndDate(),
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    usuariosService.addTripToUser(
                        FirebaseAuth.getInstance().currentUser!!.email!!,
                        idCreated,
                        tripName.text.toString(),
                        getTripStartDate(),
                        getTripEndDate(),
                        object : UsuariosService.SimpleServiceListener {
                            override fun onSuccess() {
                                loading.visibility = View.GONE
                                add_destination_button.isEnabled = true
                                done_button.isEnabled = true
                                destinationsAdapter.data.forEach { addDestination(idCreated, it) }
                            }

                            override fun onError(exception: Exception) {
                                loading.visibility = View.GONE
                                add_destination_button.isEnabled = true
                                done_button.isEnabled = true
                                Toast.makeText(this@NewTripActivity, exception.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }

                override fun onError(exception: Exception) {
                    loading.visibility = View.GONE
                    add_destination_button.isEnabled = true
                    done_button.isEnabled = true
                    Toast.makeText(this@NewTripActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun addDestination(tripId: String, destination: Destination) {
        viajesService.addDestinationToTrip(
            tripId, destination,
            object : ViajesService.CreateTripServiceListener {
                override fun onSuccess(idCreated: String) {
                    Log.i("[destino-agregado-id]", idCreated)
                    destination.id = idCreated
                    subject.onNext(tripId)
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@NewTripActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun onAllDestinationsAdded(): Observable<String> {
        val destinationsAddedCount = destinationsAdapter.data.size - 1
        return subject.skip(destinationsAddedCount.toLong())
            .take(1)
    }

    private fun showToastWhenAllDestinationsAreAdded(tripId: String) {
        Toast.makeText(this@NewTripActivity, "Nuevo viaje guardado!", Toast.LENGTH_LONG).show()
        val intent = Intent(this@NewTripActivity, TripTimeLineActivity::class.java)
        intent.putExtra("TRIP", Trip(tripId, tripName.text.toString(), getTripStartDate(), getTripEndDate()))
        setResult(Activity.RESULT_OK, intent)

        finish()
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

        if (datesAreInvalid()) {
            Toast.makeText(
                this,
                "Fechas no son válidas",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }

    private fun datesAreInvalid(): Boolean {
        return destinationsAdapter.data.any { it.startDate == null }
                || destinationsAdapter.data.any { it.endDate == null }
                || destinationsAdapter.data.any { it.endDate != null && it.startDate != null && it.endDate!! < it.startDate }

    }

    private fun hasAtLeastOneDestination(): Boolean {
        if (destinationsAdapter.data.isEmpty()) return false
        val firstDestination = destinationsAdapter.data[0]
        return !firstDestination.name.isNullOrBlank()
    }

    private fun isTripNameComplete() = !tripName.text.isNullOrBlank()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

