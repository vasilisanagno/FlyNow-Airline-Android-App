package com.example.flynow.ui.screens.petsFromMoreDetails

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

//view model that completes the upgrade of the pet selection and
// keeps/initializes the state of the variables
@HiltViewModel
class PetsDetailsViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val baggageAndPetsViewModel: BaggageAndPetsViewModel,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    var buttonClicked by mutableStateOf(false)
    var petsPrice by mutableDoubleStateOf(0.0)

    fun finishUpdate(): Boolean {
        buttonClicked = true
        sharedViewModel.showDialog = true

        return false
    }

    fun updatePets() {
        viewModelScope.launch {
            repository.updatePetsInTheReservation(
                bookingId = sharedViewModel.textBookingId,
                selectedPetSize = sharedViewModel.selectedPetSize,
                petsPrice = petsPrice
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
        buttonClicked = false
        sharedViewModel.tempPetPrice = 0
        petsPrice = 0.0
        sharedViewModel.selectedPetSize = ""
        sharedViewModel.updatePets = false
        sharedViewModel.petsFromMore = false
        sharedViewModel.baggagePerPassenger.clear()
        sharedViewModel.passengers.clear()
        sharedViewModel.limitBaggageFromMore.clear()
        sharedViewModel.tempBaggagePrice = 0
        sharedViewModel.oneWayInBaggage = false
        sharedViewModel.passengersCounter = 0
        baggageAndPetsViewModel.goToPreviousScreen()
    }
}