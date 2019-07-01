package utn.kotlin.travelkeeper.features.flights.airlines

data class AirlinesResponse(val airlines: List<Airline>)

data class Airline(val name: String)