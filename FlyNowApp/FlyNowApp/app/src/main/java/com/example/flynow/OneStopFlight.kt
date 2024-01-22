package com.example.flynow

//data class for flights that has one stop to reach in a destination
data class OneStopFlight(
    val firstFlightId: String,
    val firstFlightDate: String,
    val firstDepartureTime: String,
    val firstArrivalTime: String,
    val firstEconomyPrice: Double,
    val firstFlexPrice: Double,
    val firstBusinessPrice: Double,
    val firstDepartureAirport: String,
    val firstArrivalAirport: String,
    val firstDepartureCity: String,
    val firstArrivalCity: String,
    val firstAirplaneModel: String,
    val firstFlightDuration: String,

    val secondFlightId: String,
    val secondFlightDate: String,
    val secondDepartureTime: String,
    val secondArrivalTime: String,
    val secondEconomyPrice: Double,
    val secondFlexPrice: Double,
    val secondBusinessPrice: Double,
    val secondDepartureAirport: String,
    val secondArrivalAirport: String,
    val secondDepartureCity: String,
    val secondArrivalCity: String,
    val secondAirplaneModel: String,
    val secondFlightDuration: String
)