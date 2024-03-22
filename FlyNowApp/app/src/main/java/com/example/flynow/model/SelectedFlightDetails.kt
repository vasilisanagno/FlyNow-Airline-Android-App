package com.example.flynow.model

import androidx.compose.runtime.MutableState

//data class that is saved the selected flight from the user and with its info
data class SelectedFlightDetails(
    val flightId: MutableState<String>,
    val departureCity: MutableState<String>,
    val arrivalCity: MutableState<String>,
    val airplaneModel: MutableState<String>
)