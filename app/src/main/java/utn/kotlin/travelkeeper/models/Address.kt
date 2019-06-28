package utn.kotlin.travelkeeper.models

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.HashMap

data class Address (
    var address: String,
    var lat: Double,
    var long: Double
) : Serializable {
    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["address"] = this.address
        returnMap["lat"] = this.lat.toString()
        returnMap["long"] = this.long.toString()

        return returnMap
    }

    companion object {
        fun createObjectFromSnapshot(
            snapshot: DocumentSnapshot
        ): Address {
            return Address(
                snapshot.getString("address")!!,
                (snapshot.getString("lat")!!).toDouble(),
                (snapshot.getString("long")!!).toDouble()
            )
        }
    }
}
