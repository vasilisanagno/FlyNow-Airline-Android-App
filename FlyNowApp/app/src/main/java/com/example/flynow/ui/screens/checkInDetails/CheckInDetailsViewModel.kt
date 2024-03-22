package com.example.flynow.ui.screens.checkInDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.CheckInRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that updates the check-in and
// keeps/initializes the state of the variables in the screen
@HiltViewModel
class CheckInDetailsViewModel @Inject constructor(
    private val repository: CheckInRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    var flightDuration by mutableStateOf("")
    var showDialogCheckIn by mutableStateOf(false)
    var showDialogFlights by mutableStateOf(false)
    //variable to start updating the database with the check-in
    var updateCheckIn by mutableStateOf(false)
    var showDialogConfirmed by mutableStateOf(false)

    fun updateCheckIn() {
        viewModelScope.launch {
            repository.updateCheckInData(
                sharedViewModel.textBookingId,
                sharedViewModel.flightsCheckIn
            )
        }
    }

    fun initializeVariables() {
        flightDuration = ""
        showDialogCheckIn = false
        updateCheckIn = false
        showDialogFlights = false
        showDialogConfirmed = false
        sharedViewModel.checkIn = false
        sharedViewModel.checkInOpen = true
        sharedViewModel.showProgressBar = false
        sharedViewModel.checkedState.clear()
        sharedViewModel.backButton = true
        sharedViewModel.directFlight = false
        sharedViewModel.selectedIndex = 0
        sharedViewModel.flightsCheckIn.clear()
        sharedViewModel.passengersCheckIn.clear()
        sharedViewModel.numOfPassengersCheckIn = 0
        sharedViewModel.petSizeCheckIn = ""
        sharedViewModel.textBookingId = ""
        sharedViewModel.textLastname = ""
        sharedViewModel.wifiOnBoardCheckIn = -1
        sharedViewModel.baggageAndSeatCheckIn.clear()
    }
}