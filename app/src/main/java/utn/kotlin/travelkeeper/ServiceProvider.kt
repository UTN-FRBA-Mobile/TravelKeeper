package utn.kotlin.travelkeeper

import utn.kotlin.travelkeeper.DBServices.ViajesService

object ServiceProvider {

    val viajesService by lazy {
        ViajesService()
    }
}