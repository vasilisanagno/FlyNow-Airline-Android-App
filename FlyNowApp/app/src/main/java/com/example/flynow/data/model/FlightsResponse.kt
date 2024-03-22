package com.example.flynow.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight

//data class about flights that are returned from the server
//4 different cases -> outbound-direct, outbound-oneStop, inbound-direct, inbound-oneStop
data class FlightsResponse(
    var oneWayDirectFlights: SnapshotStateList<DirectFlight?> = mutableStateListOf(),
    var oneWayOneStopFlights: SnapshotStateList<OneStopFlight?> = mutableStateListOf(),
    var returnDirectFlights: SnapshotStateList<DirectFlight?> = mutableStateListOf(),
    var returnOneStopFlights: SnapshotStateList<OneStopFlight?> = mutableStateListOf()
)
