package com.example.flynow.ui.screens.myBookingDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MyBookingRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that deletes the booking details from the database and
// keeps/initializes the state of the variables of the screen
@HiltViewModel
class MyBookingDetailsViewModel @Inject constructor(
    private val repository: MyBookingRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    //variables for showing dialog for more details for flights and passengers
    var showDialogPassenger by mutableStateOf(false)
    var showDialogFlights by mutableStateOf(false)
    var outboundFlight by mutableStateOf(false)
    var selectedIndexDetails by mutableIntStateOf(0)
    var flightIndex by mutableIntStateOf(0)
    var dateInNums by mutableStateOf("")
    var dateInWords by mutableStateOf("")
    var outboundDuration by mutableStateOf("")
    var inboundDuration by mutableStateOf("")
    var deleteBooking by mutableStateOf(false)

    var showDialog by mutableStateOf(false)
    var showDialogConfirm by mutableStateOf(false)

    fun deleteBookingDetails() {
        viewModelScope.launch {
            repository.deleteBookingData(sharedViewModel.textBookingId)
        }
    }

    fun initializeVariables(){
        showDialogPassenger = false
        showDialogFlights = false
        outboundFlight = false
        selectedIndexDetails = 0
        flightIndex = 0
        dateInNums = ""
        dateInWords = ""
        outboundDuration = ""
        inboundDuration = ""
        deleteBooking = false
        showDialog = false
        showDialogConfirm = false
        sharedViewModel.backButton = true
        sharedViewModel.oneWay = false
        sharedViewModel.outboundDirect = false
        sharedViewModel.inboundDirect = false
        sharedViewModel.flightsMyBooking.clear()
        sharedViewModel.passengersMyBooking.clear()
        sharedViewModel.numOfPassengers = 0
        sharedViewModel.totalPriceMyBooking = 0.0
        sharedViewModel.rentingTotalPrice = 0.0
        sharedViewModel.petSizeMyBooking = ""
        sharedViewModel.wifiOnBoard = -1
        sharedViewModel.baggageAndSeatMyBooking.clear()
        sharedViewModel.carsMyBooking.clear()
        sharedViewModel.showProgressBar = false
        sharedViewModel.textBookingId = ""
        sharedViewModel.textLastname = ""
        sharedViewModel.buttonClickedCredentials = false
        sharedViewModel.hasError = false
        sharedViewModel.myBooking = false
    }
}