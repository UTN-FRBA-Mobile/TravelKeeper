package utn.kotlin.travelkeeper.models

import java.util.*

interface TripElement {
    fun getType(): TripElementType
    fun getBeginDate(): Date
}