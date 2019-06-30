package utn.kotlin.travelkeeper

import retrofit2.Call
import retrofit2.http.GET

interface Airlines {

    @GET("airlines")
    fun getAll(): Call<AirlinesResponse>
}