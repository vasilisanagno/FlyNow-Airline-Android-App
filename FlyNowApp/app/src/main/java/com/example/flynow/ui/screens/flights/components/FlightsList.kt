package com.example.flynow.ui.screens.flights.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.flights.FlightsViewModel

//component that shows the list of flights and all their info,
//there is also the component when no flights exist in the result
//of the choices of the user
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlightsList(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    flightsViewModel: FlightsViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //if there is no flight show a button that says go back, that the user returns to the book page
        items(flightsViewModel.noResults) {
            if(!sharedViewModel.seeBottomBar) {
                NoFlightResults(
                    navController = navController,
                    flightsViewModel = flightsViewModel
                )
            }
        }
        //if finally there are flights show them first outbound and specifically direct and after the
        //flights with one stop and second the inbound if there is with the same pattern
        if(sharedViewModel.seeBottomBar) {
            items(sharedViewModel.oneWayDirectFlights.size) { index ->
                if(sharedViewModel.listOfClassButtonsOutbound.size!=0) {
                    sharedViewModel.listOfClassButtonsOutbound[index].directOrOneStop.intValue = 0
                }
                if(index == 0) {
                    FirstFlightInfo(
                        directFlight = sharedViewModel.oneWayDirectFlights[0],
                        oneStopFlight = null,
                        flightsViewModel = flightsViewModel,
                        sharedViewModel = sharedViewModel,
                        returnOrNot = false
                    )
                }
                //this function show the direct flights in cards
                ShowDirectFlights(
                    flight = sharedViewModel.oneWayDirectFlights[index],
                    flightsViewModel = flightsViewModel,
                    index = index,
                    listOfClassButtons = sharedViewModel.listOfClassButtonsOutbound,
                    bottomPadding = sharedViewModel.oneWayOneStopFlights.size == 0
                            && sharedViewModel.returnDirectFlights.size == 0
                            && index == sharedViewModel.oneWayDirectFlights.size-1,
                    returnOrNot = false,
                    type = "outbound"
                )
            }
            items(sharedViewModel.oneWayOneStopFlights.size) { index ->
                if(sharedViewModel.listOfClassButtonsOutbound.size!=0) {
                    sharedViewModel.listOfClassButtonsOutbound[index+sharedViewModel.oneWayDirectFlights.size].directOrOneStop.intValue = 1
                }
                //info about the outbound flights with one stop if there
                //are not any direct flights and the first flights now are these flights
                //and two buttons with the sorting
                if(index == 0 && sharedViewModel.oneWayDirectFlights.size == 0) {
                    FirstFlightInfo(
                        directFlight = null,
                        oneStopFlight = sharedViewModel.oneWayOneStopFlights[0],
                        flightsViewModel = flightsViewModel,
                        sharedViewModel = sharedViewModel,
                        returnOrNot = false
                    )
                }
                //this function show the flights with one stop in cards
                ShowOneStopFlights(
                    flight = sharedViewModel.oneWayOneStopFlights[index],
                    flightsViewModel = flightsViewModel,
                    index = sharedViewModel.oneWayDirectFlights.size + index,
                    topPadding = sharedViewModel.oneWayDirectFlights.size==0,
                    listOfClassButtons = sharedViewModel.listOfClassButtonsOutbound,
                    bottomPadding = sharedViewModel.returnDirectFlights.size == 0 && sharedViewModel.returnOneStopFlights.size == 0,
                    returnOrNot = false,
                    type = "outbound"
                )

            }
            items(sharedViewModel.returnDirectFlights.size) {index ->
                if(sharedViewModel.listOfClassButtonsInbound.size!=0) {
                    sharedViewModel.listOfClassButtonsInbound[index].directOrOneStop.intValue = 0
                }
                //info about the inbound flights and two buttons with the sorting
                if(index == 0) {
                    FirstFlightInfo(
                        directFlight = sharedViewModel.returnDirectFlights[0],
                        oneStopFlight = null,
                        flightsViewModel = flightsViewModel,
                        sharedViewModel = sharedViewModel,
                        returnOrNot = true
                    )
                }
                //this function show the direct flights in cards
                ShowDirectFlights(
                    flight = sharedViewModel.returnDirectFlights[index],
                    flightsViewModel = flightsViewModel,
                    index = index,
                    listOfClassButtons = sharedViewModel.listOfClassButtonsInbound,
                    bottomPadding = sharedViewModel.returnOneStopFlights.size == 0 && index == sharedViewModel.returnDirectFlights.size-1,
                    returnOrNot = true,
                    type = "inbound"
                )
            }
            items(sharedViewModel.returnOneStopFlights.size) { index ->
                if(sharedViewModel.listOfClassButtonsInbound.size!=0) {
                    sharedViewModel.listOfClassButtonsInbound[index+sharedViewModel.returnDirectFlights.size].directOrOneStop.intValue = 1
                }
                //info about the inbound flights with one stop if there
                //are not any direct flights and the first flights now are these flights
                //and two buttons with the sorting
                if(index == 0 && sharedViewModel.returnDirectFlights.size == 0) {
                    FirstFlightInfo(
                        directFlight = null,
                        oneStopFlight = sharedViewModel.returnOneStopFlights[0],
                        flightsViewModel = flightsViewModel,
                        sharedViewModel = sharedViewModel,
                        returnOrNot = true
                    )
                }
                //this function show the flights with one stop in cards
                ShowOneStopFlights(
                    flight = sharedViewModel.returnOneStopFlights[index],
                    flightsViewModel = flightsViewModel,
                    index = sharedViewModel.returnDirectFlights.size + index,
                    topPadding = sharedViewModel.returnDirectFlights.size==0,
                    listOfClassButtons = sharedViewModel.listOfClassButtonsInbound,
                    bottomPadding = index == sharedViewModel.returnOneStopFlights.size - 1,
                    returnOrNot = true,
                    type = "inbound"
                )
            }
        }
    }
}