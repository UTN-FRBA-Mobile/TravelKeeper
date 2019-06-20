package utn.kotlin.travelkeeper.DBServices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import utn.kotlin.travelkeeper.models.Hotel
import utn.kotlin.travelkeeper.models.Trip
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
    private val SUBTABLA_DOCUMENTOS_ASCOCIADOS = "documentos_asociados"

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

    interface GetTripServiceListener {
        fun onSuccess(trip: Trip)
        fun onError(exception: Exception)
    }

    fun getTripDetails(tripId: String, listener: GetTripServiceListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId)
            .get()
            .addOnSuccessListener {
                val dateParser = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

                val trip = Trip(
                    it.id,
                    it.getString("name")!!,
                    dateParser.parse(it.getString("date_start")),
                    dateParser.parse(it.getString("date_end"))
                )

                listener.onSuccess(trip)
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
                listener.onSuccess("Destino editado")
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun deleteDestinationInTrip(tripId: String, destId: String, listener: UsuariosService.SimpleServiceListener) {
        val db = FirebaseFirestore.getInstance()

        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }


    fun addHotelToDestination(tripId: String, destId: String, hotel: Hotel, listener: CreateTripServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val newHotelToAdd = hotel.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId)
            .collection(SUBTABLA_ALOJAMIENTOS)
            .add(newHotelToAdd)
            .addOnSuccessListener { documentReference ->
                listener.onSuccess(documentReference.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }


    fun editHotelToDestination(tripId: String, destId: String, hotel: Hotel, listener: CreateTripServiceListener) {
        val dateFormatter = SimpleDateFormat(DATE_ONLY, Locale.getDefault())

        val destToEdit = hotel.createMapFromObject(dateFormatter)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId)
            .collection(SUBTABLA_ALOJAMIENTOS).document(hotel.id!!)
            .set(destToEdit)
            .addOnSuccessListener {
                listener.onSuccess("Hotel editado")
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }


    fun deleteHotelToDestination(
        tripId: String,
        destId: String,
        hotelId: String,
        listener: UsuariosService.SimpleServiceListener
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DESTINOS).document(destId)
            .collection(SUBTABLA_ALOJAMIENTOS).document(hotelId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun getAccomodationFromDestination(destinationId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}