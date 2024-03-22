package com.example.flynow.data.model

import com.example.flynow.model.PassengerInfo

//data class for the response from the server about baggage per passenger
data class BaggagePerPassengerResponse(
    var passengersCounter: Int = 1,
    var oneWayInBaggage: Boolean = false,
    val passengers: MutableList<PassengerInfo> = mutableListOf(),
    val limitBaggageFromMore: MutableList<Int> = mutableListOf()
)
