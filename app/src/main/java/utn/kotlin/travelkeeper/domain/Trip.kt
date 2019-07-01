package utn.kotlin.travelkeeper.domain

import java.io.Serializable
import java.util.*

class Trip(var id: String?, var title: String, var startDate: Date, var endDate: Date): Serializable {

    fun isTripNow(): Boolean {
        val now = Date()
        if (now.after(startDate) && now.before(endDate)) {
            return true
        }
        return false
    }
}