package utn.kotlin.travelkeeper.database

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