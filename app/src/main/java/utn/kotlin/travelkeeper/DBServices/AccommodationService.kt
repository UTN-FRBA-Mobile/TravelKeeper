package utn.kotlin.travelkeeper.DBServices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import utn.kotlin.travelkeeper.models.Accommodation
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.text.SimpleDateFormat
import java.util.*

class AccommodationService {
    private val TABLA_VIAJES = "viajes"
    private val SUBTABLA_DESTINOS = "destinos_en_viaje"
    private val SUBTABLA_ALOJAMIENTOS = "alojamientos"
    private val DATE_ONLY = "yyyy-MM-dd"

    interface GetAccommodationsViajeServiceListener {
        fun onSuccess(accommodations: MutableList<Accommodation>)
        fun onError(exception: Exception)
    }

    interface CreateAccommodationServiceListener {
        fun onSuccess(idCreated: String)
        fun onError(exception: Exception)
    }

    fun getAccomodationFromDestination(tripId: String, destinationId: String, listener: GetAccommodationsViajeServiceListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destinationId)
            .collection(SUBTABLA_ALOJAMIENTOS)
            .orderBy("start_date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val accommodationList = mutableListOf<Accommodation>()
                val dateParser = SimpleDateFormat(DATE_ONLY, Locale.getDefault())
                querySnapshot.documents.forEach {
                    val accommodation = Accommodation.createObjectFromSnapshot(it, dateParser, it.id)
                    accommodationList.add(accommodation)
                }
                listener.onSuccess(accommodationList)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun addAccommodationToDestination(tripId: String, destId: String, accommodation: Accommodation, listener: CreateAccommodationServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val newAccommodationToAdd = accommodation.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId).collection(SUBTABLA_ALOJAMIENTOS)
            .add(newAccommodationToAdd)
            .addOnSuccessListener { documentReference ->
                listener.onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }
}

