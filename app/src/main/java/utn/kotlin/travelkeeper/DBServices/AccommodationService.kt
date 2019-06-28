package utn.kotlin.travelkeeper.DBServices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
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

    interface GetAccommodation{
        fun onSuccess(accommodations:Accommodation)
        fun onError(exception: Exception)
    }

    interface SimpleServiceListener {
        fun onSuccess()
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

    fun getAccomodation(tripId: String, destinationId: String, accommodationId: String,listener: GetAccommodation) {
        val db = FirebaseFirestore.getInstance()

        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destinationId)
            .collection(SUBTABLA_ALOJAMIENTOS)
            .document(accommodationId)
            .get()
            .addOnSuccessListener { documentSnapshot->
                val dateParser = SimpleDateFormat(DATE_ONLY, Locale.getDefault())
                val accommodation  = Accommodation.createObjectFromSnapshot(documentSnapshot, dateParser, documentSnapshot.id)
                listener.onSuccess(accommodation)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun addAccommodationToDestination(tripId: String, destId: String, accommodation: Accommodation, listener: CreateAccommodationServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val newAccommodationToAdd = accommodation.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId)
            .collection(SUBTABLA_ALOJAMIENTOS)
            .add(newAccommodationToAdd)
            .addOnSuccessListener { documentReference ->
                listener.onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun editAccommodation(tripId: String, destId: String, accomodation: Accommodation, listener: CreateAccommodationServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val accommodationToEdit = accomodation.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId).collection(SUBTABLA_ALOJAMIENTOS).document(accomodation.id!!)
            .set(accommodationToEdit)
            .addOnSuccessListener {
                listener.onSuccess("Alojamiento editado")
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun deleteAccommodation(tripId: String, destId: String, accommodationId: String, listener: AccommodationService.SimpleServiceListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId).collection(SUBTABLA_ALOJAMIENTOS)
            .document(accommodationId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }
}

