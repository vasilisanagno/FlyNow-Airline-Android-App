package com.example.flynow

import androidx.compose.runtime.MutableState

//data class for flights that is direct to reach in a destination
data class BasicFlight(
    val flightDate: MutableState<String>,
    val departureTime: MutableState<String>,
    val arrivalTime: MutableState<String>,
    val departureCity: MutableState<String>,
    val arrivalCity: MutableState<String>,
    val departureAirp: MutableState<String>,
    val arrivalAirp: MutableState<String>,
    val flightDuration: MutableState<String>,
    val flightId: MutableState<String>,
    val airplaneModel: MutableState<String>,
    val classType: MutableState<String>
)
