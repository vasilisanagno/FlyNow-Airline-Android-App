package com.example.flynow.ui.screens.baggageFromMoreDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that communicates with the server to update the new options of the user
//and keeps the state and initializes the variables that are used in this screen
@HiltViewModel
class BaggageDetailsViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val baggageAndPetsViewModel: BaggageAndPetsViewModel,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    var baggagePrice by mutableDoubleStateOf(0.0)
    var buttonClicked by mutableStateOf(false)

    //function to activate the show dialog variable and
    fun finishUpdate(): Boolean {
        buttonClicked = true
        sharedViewModel.showDialog = true

        return false
    }

    fun updateBaggage() {
        viewModelScope.launch {
            repository.updateBaggagePerPassenger(
                bookingId = sharedViewModel.textBookingId,
                numOfPassengers = sharedViewModel.passengersCounter,
                oneWay = sharedViewModel.oneWayInBaggage,
                passengersInfo = sharedViewModel.passengers,
                selectedBaggage = sharedViewModel.baggagePerPassenger,
                baggagePrice = baggagePrice
            )
        }
    }

    fun initializeVariables() {
        sharedViewModel.selectedIndex = 0
        sharedViewModel.textLastname = ""
        sharedViewModel.textBookingId = ""
        sharedViewModel.buttonClickedCredentials = false
        sharedViewModel.showProgressBar = false
        sharedViewModel.showDialog = false
        sharedViewModel.baggageFromMore = false
        sharedViewModel.updateBaggage = false
        buttonClicked = false
        baggagePrice = 0.0
        sharedViewModel.baggagePerPassenger.clear()
        sharedViewModel.passengers.clear()
        sharedViewModel.limitBaggageFromMore.clear()
        sharedViewModel.tempBaggagePrice = 0
        sharedViewModel.oneWayInBaggage = false
        sharedViewModel.passengersCounter = 0
        baggageAndPetsViewModel.goToPreviousScreen()
    }
}