package com.example.flynow

//data class for flights that is direct to reach in a destination
data class DirectFlight(
    val flightId: String,
    val flightDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val economyPrice: Double,
    val flexPrice: Double,
    val businessPrice: Double,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureCity: String,
    val arrivalCity: String,
    val airplaneModel: String,
    val flightDuration: String
)
