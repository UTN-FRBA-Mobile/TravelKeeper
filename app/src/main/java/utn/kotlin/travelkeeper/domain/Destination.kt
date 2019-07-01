package utn.kotlin.travelkeeper.domain

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Destination(
    var name: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var id: String? = null
) : Serializable, TripElement {

    override fun getBeginDate(): Date {
        return startDate!!
    }

    override fun getType(): TripElementType {
        return TripElementType.DESTINATION
    }

    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["name"] = this.name!!
        returnMap["type"] = "Lugar"
        returnMap["start_date"] = dateFormatter.format(this.startDate)
        returnMap["end_date"] = dateFormatter.format(this.endDate)

        return returnMap
    }

    companion object {
        fun createObjectFromSnapshot(
            snapshot: DocumentSnapshot,
            dateParser: SimpleDateFormat,
            id: String
        ): Destination {
            return Destination(
                snapshot.getString("name")!!,
                dateParser.parse(snapshot.getString("start_date")),
                dateParser.parse(snapshot.getString("end_date")),
                id
            )
        }
    }
}
