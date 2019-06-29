package utn.kotlin.travelkeeper.DBServices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import utn.kotlin.travelkeeper.models.Flight
import utn.kotlin.travelkeeper.utils.DATE_AND_HOUR_PATTERN
import utn.kotlin.travelkeeper.utils.dateAndHourFormat
import java.text.SimpleDateFormat
import java.util.*

//TODO: cambiar todo lo de locale para que quede igual

class FlightService {
    private val TABLA_VIAJES = "viajes"
    private val COLECCION_VUELOS = "vuelos"

    interface GetFlightsListener {
        fun onSuccess(flights: MutableList<Flight>)
        fun onError(exception: Exception)
    }

    fun getAllFromTrip(tripId: String, listener: GetFlightsListener) {
        val db = FirebaseFirestore.getInstance()

        val reference = db.collection(TABLA_VIAJES).document(tripId)
        reference.collection(COLECCION_VUELOS)
            .orderBy("takeoff_date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val flights = mutableListOf<Flight>()
                val dateParser = SimpleDateFormat(DATE_AND_HOUR_PATTERN, Locale.getDefault())
                querySnapshot.documents.forEach {
                    val flight = Flight.createObjectFromSnapshot(it, dateParser, it.id)
                    flights.add(flight)
                }
                listener.onSuccess(flights)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    interface AddFlightListener {
        fun onSuccess(flightId: String)
        fun onError(exception: Exception)
    }

    fun add(flightToAdd: Flight, tripId: String, listener: AddFlightListener) {
        val flight = flightToAdd.createMapFromObject(dateAndHourFormat())

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES)
            .document(tripId)
            .collection(COLECCION_VUELOS)
            .add(flight)
            .addOnSuccessListener { documentReference ->
                listener.onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }

    }

    interface EditOrDeleteFlightListener {
        fun onSuccess()
        fun onError(exception: Exception)
    }

    fun edit(tripId: String, flight: Flight, listener: EditOrDeleteFlightListener) {
        val flightToEdit = flight.createMapFromObject(dateAndHourFormat())

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES)
            .document(tripId)
            .collection(COLECCION_VUELOS)
            .document(flight.id!!)
            .set(flightToEdit)
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun delete(tripId: String, flightId: String, listener: EditOrDeleteFlightListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection(TABLA_VIAJES)
            .document(tripId)
            .collection(COLECCION_VUELOS)
            .document(flightId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }


}