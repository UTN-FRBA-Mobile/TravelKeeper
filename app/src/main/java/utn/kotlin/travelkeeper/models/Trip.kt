package utn.kotlin.travelkeeper.models

import java.util.*
import java.io.Serializable

class Trip(var id: String?, var title: String, var startDate: Date, var endDate: Date): Serializable {

    fun isTripNow(): Boolean {
        val now = Date()
        if (now.after(startDate) && now.before(endDate)) {
            return true
        }
        return false
    }
}