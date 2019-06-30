package utn.kotlin.travelkeeper

data class AirlinesResponse(val airlines: List<Airline>)

data class Airline(val name: String)