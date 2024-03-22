package com.example.flynow.ui.screens.flights.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.flynow.model.OneStopFlight
import com.example.flynow.model.ReservationType
import com.example.flynow.ui.screens.flights.FlightsViewModel
import com.example.flynow.utils.Time

//component that shows the one stop flights in cards with their info
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun <T: OneStopFlight?> ShowOneStopFlights(
    flight: T,
    flightsViewModel: FlightsViewModel,
    index: Int,
    topPadding: Boolean,
    listOfClassButtons: MutableList<ReservationType>,
    bottomPadding: Boolean,
    returnOrNot: Boolean,
    type: String
) {
    val showDialog = remember {
        mutableStateOf(false)
    }
    if (flight != null) {
        val (totalHours, totalMinutes) = Time.findTotalHoursMinutes(flight)
        //alert dialog for the flight details with more info about the flight
        FlightDetails(
            directFlight = null,
            oneStopFlight = flight,
            returnOrNot = returnOrNot,
            totalHours = totalHours,
            totalMinutes = totalMinutes,
            showDialog = showDialog
        )
        FlightCard(
            directFlight = null,
            oneStopFlight = flight,
            flightsViewModel = flightsViewModel,
            index = index,
            listOfClassButtons = listOfClassButtons,
            topPadding = topPadding,
            bottomPadding = bottomPadding,
            type = type,
            totalHours = totalHours,
            totalMinutes = totalMinutes,
            showDialog = showDialog
        )
    }
}