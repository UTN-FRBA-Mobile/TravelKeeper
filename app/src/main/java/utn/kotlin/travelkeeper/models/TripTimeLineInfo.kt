package utn.kotlin.travelkeeper.models

import java.io.Serializable
import java.time.LocalDate
import java.util.*


class TripTimeLineInfo(
    var date: Date,
    var detail: String,
    var type: String
): Serializable
