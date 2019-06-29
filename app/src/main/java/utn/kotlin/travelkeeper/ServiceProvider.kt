package utn.kotlin.travelkeeper

import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.DBServices.FlightService
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.DBServices.ViajesService

object ServiceProvider {

    val viajesService by lazy {
        ViajesService()
    }

    val accommodationService by lazy {
        AccommodationService()
    }

    val usuariosService by lazy {
        UsuariosService()
    }

    val flightService by lazy {
        FlightService()
    }
}