package com.example.flynow.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.model.BaggageAndSeatPerPassenger
import com.example.flynow.model.BasicFlight
import com.example.flynow.model.PassengerInfo

//data class for the response from the server about check-in details
data class CheckInResponse(
    var checkInOpen:Boolean = false,
    var directFlight:Boolean = false,
    var numOfPassengersCheckIn:Int = 0,
    var wifiOnBoardCheckIn:Int = -1,
    var passengersCheckIn:SnapshotStateList<PassengerInfo?> = mutableStateListOf(),
    var flightsCheckIn:SnapshotStateList<BasicFlight?> = mutableStateListOf(),
    var baggageAndSeatCheckIn: SnapshotStateList<BaggageAndSeatPerPassenger?> = mutableStateListOf(),
    var petSizeCheckIn: String = ""
)