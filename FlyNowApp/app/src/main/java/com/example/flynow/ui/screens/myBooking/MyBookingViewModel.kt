package com.example.flynow.ui.screens.myBooking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MyBookingRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that retrieves the booking details
@HiltViewModel
class MyBookingViewModel @Inject constructor(
    private val repository: MyBookingRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    fun getBookingDetails() {
        viewModelScope.launch {
            val (
                oneWay,
                outboundDirect,
                inboundDirect,
                flightsMyBooking,
                baggageAndSeatMyBooking,
                numOfPassengers,
                passengersMyBooking,
                petSizeMyBooking,
                wifiOnBoard,
                carsMyBooking,
                rentingTotalPrice,
                totalPriceMyBooking
            ) = repository.getBookingData(sharedViewModel.textBookingId)

            sharedViewModel.oneWay = oneWay
            sharedViewModel.outboundDirect = outboundDirect
            sharedViewModel.inboundDirect = inboundDirect
            sharedViewModel.flightsMyBooking = flightsMyBooking
            //baggage
            sharedViewModel.baggageAndSeatMyBooking = baggageAndSeatMyBooking
            sharedViewModel.numOfPassengers = numOfPassengers
            //passengers
            sharedViewModel.passengersMyBooking = passengersMyBooking
            //pet
            sharedViewModel.petSizeMyBooking = petSizeMyBooking
            //wifi
            sharedViewModel.wifiOnBoard = wifiOnBoard
            //cars
            sharedViewModel.carsMyBooking = carsMyBooking
            Log.d("view", sharedViewModel.petSizeMyBooking)
            Log.d("view1", sharedViewModel.wifiOnBoard.toString())
            Log.d("view2", sharedViewModel.carsMyBooking.toList().toString())
            sharedViewModel.rentingTotalPrice = rentingTotalPrice
            sharedViewModel.totalPriceMyBooking = totalPriceMyBooking
            sharedViewModel.backButton = false
        }
    }
}