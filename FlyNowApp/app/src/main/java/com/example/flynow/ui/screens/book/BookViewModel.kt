package com.example.flynow.ui.screens.book

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.BookRepository
import com.example.flynow.model.ReservationType
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that keeps the state of the variables of the book screen
//and through an api get the flights for the next screen
@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    //variables that is stored the values of the textfields
    var departureDate by mutableStateOf("")
    var returnDate by mutableStateOf("")
    //for switch button
    var checked by mutableStateOf(false)
    //if the button clicked to see if some textfields are empty
    var buttonClicked by mutableStateOf(false)
    //check boxes about am or pm flights
    var amChecked by mutableStateOf(false)
    var pmChecked by mutableStateOf(false)

    //function that communicates with the server through an api
    //and gets the flights from the database according the options of the user
    fun fetchFlightsFromApi() {
        viewModelScope.launch {
            val (
                oneWayDirectFlights,
                oneWayOneStopFlights,
                returnDirectFlights,
                returnOneStopFlights
            ) = repository.getAllFlights(
                sharedViewModel.airportFrom,
                sharedViewModel.airportTo,
                departureDate,
                returnDate,
                checked,
                amChecked,
                pmChecked,
                sharedViewModel.passengersCounter
            )
            sharedViewModel.oneWayDirectFlights = oneWayDirectFlights
            sharedViewModel.oneWayOneStopFlights = oneWayOneStopFlights
            sharedViewModel.returnDirectFlights = returnDirectFlights
            sharedViewModel.returnOneStopFlights = returnOneStopFlights

            sharedViewModel.oneWayDirectFlightsOriginal = oneWayDirectFlights
            sharedViewModel.oneWayOneStopFlightsOriginal = oneWayOneStopFlights
            sharedViewModel.returnDirectFlightsOriginal = returnDirectFlights
            sharedViewModel.returnOneStopFlightsOriginal = returnOneStopFlights
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
            sharedViewModel.seeBottomBar = !((
                    sharedViewModel.oneWayDirectFlights.size == 0
                    && sharedViewModel.oneWayOneStopFlights.size == 0
                    && sharedViewModel.returnDirectFlights.size== 0
                    && sharedViewModel.returnOneStopFlights.size == 0)
                    ||((sharedViewModel.oneWayOneStopFlights.size == 0
                    && sharedViewModel.oneWayDirectFlights.size  == 0 &&
                    (sharedViewModel.returnDirectFlights.size !=0
                    || sharedViewModel.returnOneStopFlights.size !=0))||
                    (sharedViewModel.returnDirectFlights.size == 0
                    && sharedViewModel.returnOneStopFlights.size == 0 && sharedViewModel.page == 1 &&(
                    sharedViewModel.oneWayOneStopFlights.size != 0
                    || sharedViewModel.oneWayDirectFlights.size != 0)
                    ))
            )
        }
    }

    //initialization of variables of this view model
    fun initializeVariables() {
        departureDate = ""
        returnDate = ""
        checked = false
        buttonClicked = false
        amChecked = false
        pmChecked = false
    }
}