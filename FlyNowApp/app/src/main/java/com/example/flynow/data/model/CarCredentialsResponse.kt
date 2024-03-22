package com.example.flynow.data.model

//data class for the response about car credentials if everything is fine
data class CarCredentialsResponse(
    var bookingError: Boolean = false,
    var airportError: Boolean = false,
    var rentingTimeError: Boolean = false
)
