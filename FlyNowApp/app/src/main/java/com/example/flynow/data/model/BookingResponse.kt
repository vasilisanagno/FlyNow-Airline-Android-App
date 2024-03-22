package com.example.flynow.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.model.BaggageAndSeatPerPassenger
import com.example.flynow.model.BasicFlight
import com.example.flynow.model.CarDetailsMyBooking
import com.example.flynow.model.PassengerInfo

//data class for the response from the server about booking details
data class BookingResponse(
    var oneWay: Boolean = false,
    var outboundDirect: Boolean = false,
    var inboundDirect: Boolean = false,
    var flightsMyBooking: SnapshotStateList<BasicFlight?> = mutableStateListOf(),
    var baggageAndSeatMyBooking: SnapshotStateList<BaggageAndSeatPerPassenger?> = mutableStateListOf(),
    var numOfPassengers: Int = 0,
    var passengersMyBooking: SnapshotStateList<PassengerInfo?> = mutableStateListOf(),
    var petSizeMyBooking: String = "",
    var wifiOnBoard: Int = -1,
    var carsMyBooking: SnapshotStateList<CarDetailsMyBooking?> = mutableStateListOf(),
    var rentingTotalPrice: Double = 0.0,
    var totalPriceMyBooking: Double = 0.0
)
