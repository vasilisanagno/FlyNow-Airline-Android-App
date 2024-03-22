package com.example.flynow.ui.screens.seats

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.SeatRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that keeps the state for variables
//of the seats screen and has functions that initialization the variables for the next screen
//and the current screen and two functions that communicate with the server through a api
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class SeatsViewModel @Inject constructor(
    private val repository: SeatRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    var buttonClicked by mutableStateOf(false)

    var isClickedSeat by mutableStateOf(mutableListOf<MutableList<Boolean>>())
    val seatListOutboundDirect by mutableStateOf(mutableListOf<String>())
    val seatListInboundDirect by mutableStateOf(mutableListOf<String>())
    val seatListOutboundOneStop by mutableStateOf(mutableListOf<MutableList<String>>())
    val seatListInboundOneStop by mutableStateOf(mutableListOf<MutableList<String>>())

    private var checkForEmptySeats by mutableStateOf(false)
    var bookedSeats by mutableStateOf(mutableListOf<MutableList<String>>())
    var capacity by mutableStateOf(mutableListOf<Int>())

    init {
        repeat(4) {
            isClickedSeat.add(mutableListOf())
        }
        repeat(2) {
            seatListOutboundOneStop.add(mutableListOf())
            seatListInboundOneStop.add(mutableListOf())
        }
    }

    fun fetchBookedSeatsFromApi(
        flightId: String
    ) {
        viewModelScope.launch {
            bookedSeats.add(repository.getAirplaneBookedSeats(flightId))
            Log.d("helen1", bookedSeats.toString())
        }
    }

    fun fetchAirplaneCapacityFromApi(
        airplaneModel: String
    ) {
        viewModelScope.launch {
            capacity.add(repository.getAirplaneCapacity(airplaneModel))
            Log.d("maria1",  capacity.toString())
        }
    }

    //initialization the variables for the next screen
    fun prepareForTheNextScreen(): Boolean {
        buttonClicked = true
        sharedViewModel.prevTotalPrice = sharedViewModel.totalPrice
        sharedViewModel.finishReservation = 0
        checkForEmptySeats = false
        var passenger = 0
        sharedViewModel.seats.forEach { seat->
            var counter = 0
            if(passenger < sharedViewModel.passengersCounter) {
                if(sharedViewModel.selectedFlightOutbound == 0) {
                    seat.forEach {seatPerFlight ->
                        if(seatPerFlight.value == "" && counter == 0) {
                            checkForEmptySeats = true
                        }
                        counter++
                    }
                }
                else {
                    seat.forEach {seatPerFlight ->
                        if(seatPerFlight.value == "") {
                            checkForEmptySeats = true
                        }
                    }
                }
            }
            counter = 0
            if(passenger >= sharedViewModel.passengersCounter) {
                if(sharedViewModel.selectedFlightInbound == 0) {
                    seat.forEach {seatPerFlight ->
                        if(seatPerFlight.value == "" && counter == 0) {
                            checkForEmptySeats = true
                        }
                        counter++
                    }
                }
                else {
                    seat.forEach {seatPerFlight ->
                        if(seatPerFlight.value == "") {
                            checkForEmptySeats = true
                        }
                    }
                }
            }
            passenger++
        }
        if(sharedViewModel.baggagePerPassenger.size == 0) {
            if(sharedViewModel.page == 0) {
                repeat(sharedViewModel.passengersCounter) {
                    sharedViewModel.baggagePerPassenger.add(mutableStateListOf(
                        mutableIntStateOf(0),
                        mutableIntStateOf(0)
                    ))
                }
            }
            else {
                repeat(sharedViewModel.passengersCounter*2) {
                    sharedViewModel.baggagePerPassenger.add(mutableStateListOf(
                        mutableIntStateOf(0),
                        mutableIntStateOf(0)
                    ))
                }
            }
        }
        return !checkForEmptySeats
    }

    //initialization the variables of the current screen
    fun goToPreviousScreen() {
        sharedViewModel.seats.clear()
        sharedViewModel.bookingFailed = false
        buttonClicked = false
        isClickedSeat.clear()
        bookedSeats.clear()
        capacity.clear()
        seatListOutboundDirect.clear()
        seatListInboundDirect.clear()
        seatListOutboundOneStop.clear()
        seatListInboundOneStop.clear()
        repeat(4) {
            isClickedSeat.add(mutableListOf())
        }
        repeat(2) {
            seatListOutboundOneStop.add(mutableListOf())
            seatListInboundOneStop.add(mutableListOf())
        }
        checkForEmptySeats = false
    }
}