package com.example.flynow

import androidx.compose.runtime.MutableState

data class BaggageAndSeatPerPassenger(
        val gender: MutableState<String>,
        val firstname: MutableState<String>,
        val lastname: MutableState<String>,
        val flightid: MutableState<String>,
        val reservationid: MutableState<String>,
        val baggage23kg: MutableState<Int>,
        val baggage32kg: MutableState<Int>,
        val seatnumber: MutableState<String>,
        val departurecity: MutableState<String>,
        val arrivalcity: MutableState<String>,
        val checkin: MutableState<Boolean>
)