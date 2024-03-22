package com.example.flynow.ui.screens.passengers

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flynow.model.ReservationType
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//view model class that keeps the state for two variables
//of the passengers screen and has two functions that initialize
//the variables for the next screen and the current screen
@HiltViewModel
class PassengersViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    var buttonClicked by mutableStateOf(false)
    private var checkForEmptyInputFields by mutableStateOf(false)

    //initialization the variables for the next screen
    fun prepareForTheNextScreen(): Boolean {
        buttonClicked = true
        checkForEmptyInputFields = false
        sharedViewModel.passengers.forEachIndexed{ index, passenger ->
            if((index + 1 <= sharedViewModel.passengersCounter) &&
                (passenger.gender.value == "" ||
                        passenger.firstname.value == "" ||
                        passenger.lastname.value == "" ||
                        passenger.birthdate.value == "" ||
                        passenger.email.value == "" ||
                        passenger.phonenumber.value == "")) {
                checkForEmptyInputFields = true
            }
        }
        if(sharedViewModel.seats.size==0) {
            if (sharedViewModel.page == 0) {
                for (i in 0 until sharedViewModel.passengersCounter) {
                    val innerList: MutableList<MutableState<String>> = mutableListOf()
                    for (j in 0 until 2) {
                        val seat = mutableStateOf("")
                        innerList.add(seat)
                    }
                    sharedViewModel.seats.add(innerList)
                }
            } else {
                for (i in 0 until 2 * sharedViewModel.passengersCounter) {
                    val innerList: MutableList<MutableState<String>> = mutableListOf()
                    for (j in 0 until 2) {
                        val seat = mutableStateOf("")
                        innerList.add(seat)
                    }
                    sharedViewModel.seats.add(innerList)
                }
            }
        }
        return !checkForEmptyInputFields
    }

    //initialization the variables of the current screen
    fun goToPreviousScreen() {
        buttonClicked = false
        checkForEmptyInputFields = false
        sharedViewModel.totalPriceOneWay = 0.0
        sharedViewModel.totalPriceReturn = 0.0
        sharedViewModel.listOfClassButtonsOutbound.clear()
        sharedViewModel.listOfClassButtonsInbound.clear()
        sharedViewModel.classTypeInbound = ""
        sharedViewModel.classTypeOutbound = ""
        sharedViewModel.totalPrice = 0.0
        sharedViewModel.selectedFlights.forEach { flight ->
            flight.flightId.value = ""
            flight.departureCity.value = ""
            flight.arrivalCity.value = ""
            flight.airplaneModel.value = ""
        }
        //initialization of the two list of buttons of the flights
        if(sharedViewModel.listOfClassButtonsOutbound.size == 0) {
            repeat(sharedViewModel.oneWayDirectFlights.size + sharedViewModel.oneWayOneStopFlights.size) {
                sharedViewModel.listOfClassButtonsOutbound.add(
                    ReservationType(
                        mutableStateOf(false),
                        mutableStateOf(false),
                        mutableStateOf(false),
                        mutableIntStateOf(0)
                    )
                )
            }
        }
        if(sharedViewModel.listOfClassButtonsInbound.size == 0) {
            repeat(sharedViewModel.returnDirectFlights.size + sharedViewModel.returnOneStopFlights.size) {
                sharedViewModel.listOfClassButtonsInbound.add(
                    ReservationType(
                        mutableStateOf(false),
                        mutableStateOf(false),
                        mutableStateOf(false),
                        mutableIntStateOf(0)
                    )
                )
            }
        }
    }
}