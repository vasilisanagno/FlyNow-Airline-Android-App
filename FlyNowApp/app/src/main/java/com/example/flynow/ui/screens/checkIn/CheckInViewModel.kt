package com.example.flynow.ui.screens.checkIn

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.CheckInRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that retrieves the check-in details
// and checks if the check-in is open
@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val repository: CheckInRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    fun getCheckInDetails() {
        viewModelScope.launch {
            val (
                checkInOpen,
                directFlight,
                numOfPassengersCheckIn,
                wifiOnBoardCheckIn,
                passengersCheckIn,
                flightsCheckIn,
                baggageAndSeatCheckIn,
                petSizeCheckIn
            ) = repository.getCheckInData(sharedViewModel.textBookingId)

            sharedViewModel.checkInOpen = checkInOpen
            sharedViewModel.directFlight = directFlight
            sharedViewModel.numOfPassengersCheckIn = numOfPassengersCheckIn
            sharedViewModel.wifiOnBoardCheckIn = wifiOnBoardCheckIn
            sharedViewModel.passengersCheckIn = passengersCheckIn
            sharedViewModel.flightsCheckIn = flightsCheckIn
            sharedViewModel.baggageAndSeatCheckIn = baggageAndSeatCheckIn
            sharedViewModel.petSizeCheckIn = petSizeCheckIn
            sharedViewModel.backButton = false

            if(sharedViewModel.checkedState.size == 0 && sharedViewModel.checkInOpen) {
                repeat(numOfPassengersCheckIn) {
                    sharedViewModel.checkedState.add(mutableStateOf(false))
                }
            }
        }
    }
}