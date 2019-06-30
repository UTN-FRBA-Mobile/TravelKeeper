package utn.kotlin.travelkeeper


data class AirlinesResponse(val airlines: List<Airline>)

data class Airline(val iata_code: String? = null, val name: String? = null)