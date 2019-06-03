package utn.kotlin.travelkeeper.DBServices

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ViajesService {
    private val TABLA_VIAJES = "viajes"
    private val SUBTABLA_DESTINOS = "destinos_en_viaje"
    private val SUBTABLA_INTERDESTINOS = "traslados_entre_destinos"
    private val SUBTABLA_TRASLADOS = "traslados"
    private val SUBTABLA_ALOJAMIENTOS = "alojamientos"
    private val SUBTABLA_ACTIVIDADES = "actividades"
    private val SUBTABLA_OTROS = "otros"

    private val TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss*SSSZZZZ"
    private val DATE_ONLY = "yyyy-MM-dd"

    interface CreateTripServiceListener {
        fun onSuccess(idCreated: String)
        fun onError(exception: Exception)
    }

    fun createTrip(tripName: String, dateStart: Date, dateEnd: Date, listener: CreateTripServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val newTripToAdd = HashMap<String, String>()
        newTripToAdd["name"] = tripName
        newTripToAdd["date_start"] = dateFormatter.format(dateStart)
        newTripToAdd["date_end"] = dateFormatter.format(dateEnd)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES)
            .add(newTripToAdd)
            .addOnSuccessListener { documentRefference ->
                listener.onSuccess(documentRefference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun addDestinationToTrip(tripId: String, destName: String, destDateStart: Date, destDateEnd: Date,
                             listener: CreateTripServiceListener) {

        val dateFormatter = SimpleDateFormat(TIMESTAMP, Locale.getDefault())

        val newDestToAdd = HashMap<String, String>()
        newDestToAdd["name"] = destName
        newDestToAdd["date_start"] = dateFormatter.format(destDateStart)
        newDestToAdd["date_end"] = dateFormatter.format(destDateEnd)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS)
            .add(newDestToAdd)
            .addOnSuccessListener { documentRefference ->
                listener.onSuccess(documentRefference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }
}