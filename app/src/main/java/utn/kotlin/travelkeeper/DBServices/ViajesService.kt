package utn.kotlin.travelkeeper.DBServices

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import utn.kotlin.travelkeeper.models.Destination
import utn.kotlin.travelkeeper.models.DocumentationInfo
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.storage.FileStorageService
import utn.kotlin.travelkeeper.utils.parserWithFormat
import java.util.*
import kotlin.collections.HashMap

class ViajesService {
    private val TABLA_VIAJES = "viajes"
    private val SUBTABLA_DESTINOS = "destinos_en_viaje"
    private val SUBTABLA_DOCUMENTOS_ASCOCIADOS = "documentos_asociados"

    private val TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss*SSSZZZZ"
    private val DATE_ONLY = "yyyy-MM-dd"

    interface CreateTripServiceListener {
        fun onSuccess(idCreated: String)
        fun onError(exception: Exception)
    }

    fun createTrip(tripName: String, dateStart: Date, dateEnd: Date, listener: CreateTripServiceListener) {
        val dateFormatter = parserWithFormat(DATE_ONLY)

        val newTripToAdd = HashMap<String, String>()
        newTripToAdd["name"] = tripName
        newTripToAdd["date_start"] = dateFormatter.format(dateStart)
        newTripToAdd["date_end"] = dateFormatter.format(dateEnd)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES)
            .add(newTripToAdd)
            .addOnSuccessListener { documentReference ->
                listener.onSuccess(documentReference.id)
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
                val dateParser = parserWithFormat(DATE_ONLY)

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

    interface GetDocumentationListener {
        fun onSuccess(fileList: MutableList<DocumentationInfo>)
        fun onError(exception: Exception)
    }

    fun getDocumentsFromTrip(tripId: String, listener: GetDocumentationListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_VIAJES).document(tripId)
            .collection(SUBTABLA_DOCUMENTOS_ASCOCIADOS)
            .get().addOnSuccessListener {
                var documentation: MutableList<DocumentationInfo> = mutableListOf()
                it.forEach {
                    val documentInfo = DocumentationInfo.createObjectFromSnapshot(it.data, it.id)
                    FileStorageService().getFile(tripId, documentInfo.fileName)
                    documentation.add(documentInfo)
                }
                listener.onSuccess(documentation)
            }
            .addOnFailureListener {
                listener.onError(it)
            }
    }


    interface AddDocumentationListener {
        fun onSuccess(id: String)
        fun onError(exception: Exception)
    }

    fun addDocumentToTrip(tripId: String, documentationInfo: DocumentationInfo, listener: AddDocumentationListener): Task<DocumentReference> {
        val db = FirebaseFirestore.getInstance()
        val newDocument = HashMap<String, String>()
        newDocument.put("fileName", documentationInfo.fileName)
        newDocument.put("type", documentationInfo.type)
        return db.collection(TABLA_VIAJES).document(tripId).collection(SUBTABLA_DOCUMENTOS_ASCOCIADOS)
            .add(documentationInfo)
            .addOnSuccessListener {
                listener.onSuccess(it.id)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun deleteDocumentFromTrip(tripId: String, documentationInfo: DocumentationInfo): Task<Void>{
        val db = FirebaseFirestore.getInstance()
        return db.collection(TABLA_VIAJES).document(tripId)
            .collection(SUBTABLA_DOCUMENTOS_ASCOCIADOS).document(documentationInfo.id).delete()
    }

    interface GetDestinationsListener {
        fun onSuccess(destinations: MutableList<Destination>)
        fun onError(exception: Exception)
    }

    fun getDestinationsFromTrip(tripId: String, listener: GetDestinationsListener) {
        val db = FirebaseFirestore.getInstance()

        val reference = db.collection(TABLA_VIAJES).document(tripId)
        reference.collection(SUBTABLA_DESTINOS)
            .orderBy("start_date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val destsList = mutableListOf<Destination>()
                val dateParser = parserWithFormat(DATE_ONLY)
                querySnapshot.documents.forEach {
                    val destInfo = Destination.createObjectFromSnapshot(it, dateParser, it.id)

                    destsList.add(destInfo)
                }

                listener.onSuccess(destsList)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun addDestinationToTrip(tripId: String, dest: Destination, listener: CreateTripServiceListener) {
        val dateFormatter = parserWithFormat(DATE_ONLY)

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

    fun editDestinationInTrip(tripId: String, dest: Destination, listener: CreateTripServiceListener) {
        val dateFormatter = parserWithFormat(DATE_ONLY)

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
}