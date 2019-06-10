package utn.kotlin.travelkeeper.DBServices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
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

    interface GetDestinationsViajeServiceListener {
        fun onSuccess(destinations: MutableList<TripTimeLineInfo>)
        fun onError(exception: Exception)
    }

    fun getDestinationsFromTrip(tripId: String, listener: GetDestinationsViajeServiceListener) {
        val db = FirebaseFirestore.getInstance()

        val reference = db.collection(TABLA_VIAJES).document(tripId)
        reference.collection(SUBTABLA_DESTINOS)
            .orderBy("start_date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val destsList = mutableListOf<TripTimeLineInfo>()
                val dateParser = SimpleDateFormat(DATE_ONLY, Locale.getDefault())
                querySnapshot.documents.forEach {
                    val destInfo = TripTimeLineInfo.createObjectFromSnapshot(it, dateParser, it.id)

                    destsList.add(destInfo)
                }

                listener.onSuccess(destsList)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun addDestinationToTrip(tripId: String, dest: TripTimeLineInfo, listener: CreateTripServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val newDestToAdd = dest.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS)
            .add(newDestToAdd)
            .addOnSuccessListener { documentReference ->
                listener.onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun editDestinationInTrip(tripId: String, dest: TripTimeLineInfo, listener: CreateTripServiceListener) {

        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val destToEdit = dest.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(dest.id!!)
            .set(destToEdit)
            .addOnSuccessListener {
                listener.onSuccess("Documento editado")
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }
}