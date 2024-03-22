package com.example.flynow.ui.screens.flights.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.ReservationType
import com.example.flynow.ui.screens.flights.FlightsViewModel

//component that shows the direct flights in cards with their info
@Composable
fun <T: DirectFlight?> ShowDirectFlights(
    flight: T,
    flightsViewModel: FlightsViewModel,
    index: Int,
    listOfClassButtons: MutableList<ReservationType>,
    bottomPadding: Boolean,
    returnOrNot: Boolean,
    type: String
) {
    val showDialog = remember {
        mutableStateOf(false)
    }
    if (flight != null) {
        FlightDetails(
            directFlight = flight,
            oneStopFlight = null,
            returnOrNot = returnOrNot,
            totalHours = 0,
            totalMinutes = 0,
            showDialog = showDialog
        )
        FlightCard(
            directFlight = flight,
            oneStopFlight = null,
            flightsViewModel = flightsViewModel,
            index = index,
            listOfClassButtons = listOfClassButtons,
            topPadding = false,
            bottomPadding = bottomPadding,
            type = type,
            totalHours = 0,
            totalMinutes = 0,
            showDialog = showDialog
        )
    }
}