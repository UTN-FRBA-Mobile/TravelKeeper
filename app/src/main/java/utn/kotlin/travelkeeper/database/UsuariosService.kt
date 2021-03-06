package utn.kotlin.travelkeeper.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import utn.kotlin.travelkeeper.domain.Trip
import utn.kotlin.travelkeeper.utils.parserWithFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UsuariosService {
    private val TABLA_USUARIOS = "usuarios"
    private val SUBTABLA_VIAJES = "viajes"
    private val DATE_ONLY = "yyyy-MM-dd"

    interface GOCUsuarioServiceListener {
        fun onSuccess(trips: ArrayList<Trip>)
        fun onError(exception: Exception)
    }

    fun getOrCreateUser(username: String, listener: GOCUsuarioServiceListener) {
        val db = FirebaseFirestore.getInstance()

        val reference = db.collection(TABLA_USUARIOS).document(username)
        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    reference.collection(SUBTABLA_VIAJES)
                        .whereGreaterThan("date_end", parserWithFormat(DATE_ONLY).format(Date()))
                        .orderBy("date_end", Query.Direction.ASCENDING)
                        .orderBy("date_start", Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val tripsList = arrayListOf<Trip>()
                            val dateParser = parserWithFormat(DATE_ONLY)
                            querySnapshot.documents.forEach {
                                val trip = Trip(
                                    it.id,
                                    it.getString("name")!!,
                                    dateParser.parse(it.getString("date_start")),
                                    dateParser.parse(it.getString("date_end"))
                                )

                                tripsList.add(trip)
                            }

                            listener.onSuccess(tripsList)
                        }
                        .addOnFailureListener { exception ->
                            listener.onError(exception)
                        }
                } else {
                    val empty = HashMap<String, Any>()
                    db.collection(TABLA_USUARIOS).document(username).set(empty)
                        .addOnSuccessListener {
                            listener.onSuccess(ArrayList())
                        }
                        .addOnFailureListener {
                            listener.onError(it)
                        }
                }
            } else {
                listener.onError(Exception("No se pudo contactar la base de datos"))
            }
        }
    }

    fun getOldTrips(username: String, listener: GOCUsuarioServiceListener) {
        val db = FirebaseFirestore.getInstance()

        val reference = db.collection(TABLA_USUARIOS).document(username)
        reference.collection(SUBTABLA_VIAJES)
            .whereLessThan("date_end", parserWithFormat(DATE_ONLY).format(Date()))
            .orderBy("date_end", Query.Direction.ASCENDING)
            .orderBy("date_start", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tripsList = arrayListOf<Trip>()
                val dateParser = parserWithFormat(DATE_ONLY)
                querySnapshot.documents.forEach {
                    val trip = Trip(
                        it.id,
                        it.getString("name")!!,
                        dateParser.parse(it.getString("date_start")),
                        dateParser.parse(it.getString("date_end"))
                    )

                    tripsList.add(trip)
                }

                listener.onSuccess(tripsList)
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    interface SimpleServiceListener {
        fun onSuccess()
        fun onError(exception: Exception)
    }

    fun addTripToUser(
        username: String, tripId: String, tripName: String, dateStart: Date, dateEnd: Date,
        listener: SimpleServiceListener
    ) {
        val dateFormatter = parserWithFormat(DATE_ONLY)

        val newTripToAdd = HashMap<String, String>()
        newTripToAdd["name"] = tripName
        newTripToAdd["date_start"] = dateFormatter.format(dateStart)
        newTripToAdd["date_end"] = dateFormatter.format(dateEnd)

        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_USUARIOS).document(username).collection(SUBTABLA_VIAJES).document(tripId)
            .set(newTripToAdd)
            .addOnSuccessListener { querySnapshot ->
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }

    fun removeTripFromUser(username: String, tripId: String, listener: SimpleServiceListener) {
        val db = FirebaseFirestore.getInstance()
        db.collection(TABLA_USUARIOS).document(username).collection(SUBTABLA_VIAJES).document(tripId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { exception ->
                listener.onError(exception)
            }
    }
}