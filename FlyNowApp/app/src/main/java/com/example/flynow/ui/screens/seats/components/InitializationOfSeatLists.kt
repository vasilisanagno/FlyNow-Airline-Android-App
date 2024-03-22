package com.example.flynow.ui.screens.seats.components

import androidx.compose.runtime.Composable
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.seats.SeatsViewModel

//component that creates the seat lists of the passenger for each different flight
//even the one stop flights
@Composable
fun InitializationOfSeatLists(
    sharedViewModel: SharedViewModel,
    seatsViewModel: SeatsViewModel
) {
    if(sharedViewModel.selectedFlightOutbound == 0) {
        GenerateSeatList(
            seatsViewModel = seatsViewModel,
            flightId = sharedViewModel.selectedFlights[0].flightId,
            seatList = seatsViewModel.seatListOutboundDirect,
            airplaneModel = sharedViewModel.selectedFlights[0].airplaneModel,
            isClickedSeat = seatsViewModel.isClickedSeat[0],
            index = 0
        )
    }
    else {
        GenerateSeatList(
            seatsViewModel = seatsViewModel,
            flightId = sharedViewModel.selectedFlights[0].flightId,
            seatList = seatsViewModel.seatListOutboundOneStop[0],
            airplaneModel = sharedViewModel.selectedFlights[0].airplaneModel,
            isClickedSeat = seatsViewModel.isClickedSeat[0],
            index = 0
        )
        GenerateSeatList(
            seatsViewModel = seatsViewModel,
            flightId = sharedViewModel.selectedFlights[1].flightId,
            seatList = seatsViewModel.seatListOutboundOneStop[1],
            airplaneModel = sharedViewModel.selectedFlights[1].airplaneModel,
            isClickedSeat = seatsViewModel.isClickedSeat[1],
            index = 1
        )
    }
    if (sharedViewModel.page == 1) {
        if(sharedViewModel.selectedFlightOutbound == 0 && sharedViewModel.selectedFlightInbound == 0) {
            GenerateSeatList(
                seatsViewModel = seatsViewModel,
                flightId = sharedViewModel.selectedFlights[1].flightId,
                seatList = seatsViewModel.seatListInboundDirect,
                airplaneModel = sharedViewModel.selectedFlights[1].airplaneModel,
                isClickedSeat = seatsViewModel.isClickedSeat[1],
                index = 1
            )
        }
        else if(sharedViewModel.selectedFlightOutbound == 1 && sharedViewModel.selectedFlightInbound == 0) {
            GenerateSeatList(
                seatsViewModel = seatsViewModel,
                flightId = sharedViewModel.selectedFlights[2].flightId,
                seatList = seatsViewModel.seatListInboundDirect,
                airplaneModel = sharedViewModel.selectedFlights[2].airplaneModel,
                isClickedSeat = seatsViewModel.isClickedSeat[2],
                index = 2
            )
        }
        else if(sharedViewModel.selectedFlightOutbound == 0 && sharedViewModel.selectedFlightInbound == 1) {
            GenerateSeatList(
                seatsViewModel = seatsViewModel,
                flightId = sharedViewModel.selectedFlights[1].flightId,
                seatList = seatsViewModel.seatListInboundOneStop[0],
                airplaneModel = sharedViewModel.selectedFlights[1].airplaneModel,
                isClickedSeat = seatsViewModel.isClickedSeat[1],
                index = 1
            )
            GenerateSeatList(
                seatsViewModel = seatsViewModel,
                flightId = sharedViewModel.selectedFlights[2].flightId,
                seatList = seatsViewModel.seatListInboundOneStop[1],
                airplaneModel = sharedViewModel.selectedFlights[2].airplaneModel,
                isClickedSeat = seatsViewModel.isClickedSeat[2],
                index = 2
            )
        }
        else {
            GenerateSeatList(
                seatsViewModel = seatsViewModel,
                flightId = sharedViewModel.selectedFlights[2].flightId,
                seatList = seatsViewModel.seatListInboundOneStop[0],
                airplaneModel = sharedViewModel.selectedFlights[2].airplaneModel,
                isClickedSeat = seatsViewModel.isClickedSeat[2],
                index = 2
            )
            GenerateSeatList(
                seatsViewModel = seatsViewModel,
                flightId = sharedViewModel.selectedFlights[3].flightId,
                seatList = seatsViewModel.seatListInboundOneStop[1],
                airplaneModel = sharedViewModel.selectedFlights[3].airplaneModel,
                isClickedSeat = seatsViewModel.isClickedSeat[3],
                index = 3
            )
        }
    }
}