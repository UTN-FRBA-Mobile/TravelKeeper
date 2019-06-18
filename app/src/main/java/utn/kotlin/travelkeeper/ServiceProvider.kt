package utn.kotlin.travelkeeper

import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.DBServices.ViajesService

object ServiceProvider {

    val viajesService by lazy {
        ViajesService()
    }

    val accommodationService by lazy {
        AccommodationService()
    }
}