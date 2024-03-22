package com.example.flynow.ui.screens.carCredentials

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.CarRepository
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that keeps the state of the screen variables
//and communicates with the server to check the credentials and retrieves
//the available cars from the database
@HiltViewModel
class CarCredentialsViewModel @Inject constructor(
    private val repository: CarRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    //variables that help to know in function for what cause the function is called
    var pickUpHourBool by mutableStateOf(true)
    var returnHourBool by mutableStateOf(false)

    var pickUpBool by mutableStateOf(true)
    var returnBool by mutableStateOf(false)
    var hourBool by mutableStateOf(true)
    var minBool by mutableStateOf(false)
    //to see if the dates are the same,to check the pick up
    //and return datetime to be after the return from the pick up
    var timeError by mutableStateOf(false)

    //is the variable that checks if the booking exists
    var bookingExists by mutableStateOf(false)
    //variables that shows if an error has come from the booking id or arrival airport
    var bookingError by mutableStateOf(false)
    var airportError by mutableStateOf(false)
    var rentingTimeError by mutableStateOf(false)
    //variable to start the query
    var searchCarsQuery by mutableStateOf(false)

    fun checkCredentials() {
        viewModelScope.launch {
            val (
                checkBookingError,
                checkAirportError,
                checkRentingTimeError
            ) = repository.checkCarData(
                sharedViewModel.textBookingId,
                sharedViewModel.locationToRentCar,
                sharedViewModel.pickUpDateCar,
                sharedViewModel.pickUpHour,
                sharedViewModel.pickUpMins,
                sharedViewModel.returnDateCar,
                sharedViewModel.returnHour,
                sharedViewModel.returnMins
            )
            bookingError = checkBookingError
            airportError = checkAirportError
            rentingTimeError = checkRentingTimeError
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAvailableCars() {
        viewModelScope.launch {
            sharedViewModel.listOfCars = repository.getCars(
                sharedViewModel.locationToRentCar,
                sharedViewModel.pickUpDateCar,
                sharedViewModel.pickUpHour,
                sharedViewModel.pickUpMins,
                sharedViewModel.returnDateCar,
                sharedViewModel.returnHour,
                sharedViewModel.returnMins
            )
            if(sharedViewModel.listOfButtonsCars.size == 0) {
                repeat(sharedViewModel.listOfCars.size) {
                    sharedViewModel.listOfButtonsCars.add(mutableStateOf(false))
                }
            }
            sharedViewModel.seeBottomBar = sharedViewModel.listOfCars.size != 0
        }
    }

    fun initializeVariables() {
        pickUpHourBool = true
        returnHourBool = false
        sharedViewModel.buttonClickedCredentials = false
        pickUpBool = true
        returnBool = false
        hourBool = true
        minBool = false
        timeError = false
        bookingExists = false
        bookingError = false
        airportError = false
        rentingTimeError = false
        searchCarsQuery = false
        sharedViewModel.seeBottomBar = true
        sharedViewModel.rentCar = false
        sharedViewModel.textBookingId = ""
        sharedViewModel.locationToRentCar = ""
        sharedViewModel.whatAirport = 0
        sharedViewModel.pickUpDateCar = ""
        sharedViewModel.pickUpHour = "10"
        sharedViewModel.pickUpMins = "30"
        sharedViewModel.returnDateCar = ""
        sharedViewModel.returnHour = "10"
        sharedViewModel.returnMins = "30"
        sharedViewModel.listOfCars.clear()
    }
}