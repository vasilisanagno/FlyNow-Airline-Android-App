package com.example.flynow.ui.screens.baggageAndPets

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.FinishReservationRepository
import com.example.flynow.model.Buttons
import com.example.flynow.model.IsClickedBaggage
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that keeps the state of some variables and has some functions
//for different functionality each time and one function that communicates with
//the api of the server to insert the new booking
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class BaggageAndPetsViewModel @Inject constructor(
    private val repository: FinishReservationRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    //variables that helps for the alert dialog, price, baggage, pets and finishing the reservation
    var showDialog by mutableStateOf(false)
    private var prevPetPrice by mutableIntStateOf(0)
    private var prevBaggagePrice by mutableIntStateOf(0)

    //oneTimeExecution is to initialize some variables only once, the first time
    var oneTimeExecution by mutableStateOf(false)
    val radioOptions = listOf("Yes","No")
    var selectedOption by mutableStateOf(radioOptions[1])
    var selectedOptionForYes by mutableStateOf("")

    var bookingReference by mutableStateOf("")

    //for each passenger to have what baggage has selected
    val passengersBaggage by mutableStateOf(mutableListOf<MutableList<Buttons>>())
    var isClickPerPassenger by mutableStateOf(mutableListOf<MutableList<IsClickedBaggage>>())

    var baggage23kgPriceInbound by mutableStateOf("")
    var baggage23kgPriceOutbound by mutableStateOf("")
    var baggage32kgPriceInbound by mutableStateOf("")
    var baggage32kgPriceOutbound by mutableStateOf("")

    //initialization variables the first time that comes into the BaggageFields component
    //according state and if there is inbound or not
    fun initializationVariablesInBaggageFields(
        state: String
    ) {
        if(!oneTimeExecution) {
            oneTimeExecution = true
            if((state == "Baggage&Pets" && sharedViewModel.page == 0)||
                (state == "BaggageFromMore" && sharedViewModel.oneWay)) {
                repeat(sharedViewModel.passengersCounter) {
                    passengersBaggage.add(mutableStateListOf(Buttons()))
                }
                repeat(sharedViewModel.passengersCounter) {
                    isClickPerPassenger.add(mutableStateListOf(IsClickedBaggage()))
                }
            }
            else {
                repeat(2*sharedViewModel.passengersCounter) {
                    passengersBaggage.add(mutableStateListOf(Buttons()))
                }
                repeat(2*sharedViewModel.passengersCounter) {
                    isClickPerPassenger.add(mutableStateListOf(IsClickedBaggage()))
                }
            }
        }
        if(state == "Baggage&Pets") {
            when(sharedViewModel.classTypeInbound) {
                "Economy" -> {
                    baggage23kgPriceInbound = "15€"
                    baggage32kgPriceInbound = "25€"
                }
                "Flex" -> {
                    baggage23kgPriceInbound = "Free"
                    baggage32kgPriceInbound = "25€"
                }
                "Business" -> {
                    baggage23kgPriceInbound = "Free"
                    baggage32kgPriceInbound = "Free"
                }
            }
            when(sharedViewModel.classTypeOutbound) {
                "Economy" -> {
                    baggage23kgPriceOutbound = "15€"
                    baggage32kgPriceOutbound = "25€"
                }
                "Flex" -> {
                    baggage23kgPriceOutbound = "Free"
                    baggage32kgPriceOutbound = "25€"
                }
                "Business" -> {
                    baggage23kgPriceOutbound = "Free"
                    baggage32kgPriceOutbound = "Free"
                }
            }
        }
        else {
            baggage23kgPriceInbound = "15€"
            baggage23kgPriceOutbound = "15€"
            baggage32kgPriceInbound = "25€"
            baggage32kgPriceOutbound = "25€"
        }
    }

    //function that makes true the dialog to finish the user the reservation
    fun finishReservation(): Boolean {
        showDialog = true

        return false
    }

    //initialization variables when go to the home screen
    fun initializationVariables() {
        showDialog = false
        sharedViewModel.passengersCounter = 1
        sharedViewModel.passengers.clear()
        sharedViewModel.seats.clear()
        sharedViewModel.baggagePerPassenger.clear()
        sharedViewModel.selectedFlights.forEach { flight ->
            flight.flightId.value = ""
            flight.airplaneModel.value = ""
            flight.arrivalCity.value = ""
            flight.departureCity.value = ""
        }
        sharedViewModel.selectedFlightOutbound = 0
        sharedViewModel.selectedFlightInbound = 0
        sharedViewModel.classTypeOutbound = ""
        sharedViewModel.classTypeInbound = ""
        sharedViewModel.petSize = ""
        sharedViewModel.totalPrice = 0.0
        sharedViewModel.page = 0
        sharedViewModel.prevTotalPrice = 0.0
        sharedViewModel.selectedIndex = 0
        sharedViewModel.airportFrom = ""
        sharedViewModel.airportTo = ""
        sharedViewModel.whatAirport = 0
        sharedViewModel.bookingFailed = false
        //initializes some local variables of this view model
        goToPreviousScreen()
        sharedViewModel.finishReservation = 1
    }

    //adding prices for baggage and pets fields
    fun addingPrices() {
        //add pet price
        if(sharedViewModel.tempPetPrice != prevPetPrice && prevPetPrice != 0){
            sharedViewModel.totalPrice -= prevPetPrice
            sharedViewModel.totalPrice += sharedViewModel.tempPetPrice
        }
        else if(sharedViewModel.tempPetPrice != prevPetPrice && prevPetPrice == 0){
            sharedViewModel.totalPrice += sharedViewModel.tempPetPrice
        }
        else if(sharedViewModel.tempPetPrice == 0 && prevPetPrice != 0){
            sharedViewModel.totalPrice -= prevPetPrice
        }
        prevPetPrice = sharedViewModel.tempPetPrice
        //add baggage price
        if(sharedViewModel.tempBaggagePrice != prevBaggagePrice && prevBaggagePrice != 0){
            sharedViewModel.totalPrice -= prevBaggagePrice
            sharedViewModel.totalPrice += sharedViewModel.tempBaggagePrice
        }
        else if(sharedViewModel.tempBaggagePrice != prevBaggagePrice && prevBaggagePrice == 0){
            sharedViewModel.totalPrice += sharedViewModel.tempBaggagePrice
        }
        prevBaggagePrice = sharedViewModel.tempBaggagePrice
    }

    //communicates with the server to insert the new booking
    fun insertNewBooking() {
        viewModelScope.launch {
            val response = repository.makeReservation(
                passengers = sharedViewModel.passengers,
                seats = sharedViewModel.seats,
                selectedFlightOutbound = sharedViewModel.selectedFlightOutbound,
                selectedFlightInbound = sharedViewModel.selectedFlightInbound,
                page = sharedViewModel.page,
                numOfPassengers = sharedViewModel.passengersCounter,
                selectedFlights = sharedViewModel.selectedFlights,
                baggagePerPassenger = sharedViewModel.baggagePerPassenger,
                classTypeOutbound = sharedViewModel.classTypeOutbound,
                classTypeInbound = sharedViewModel.classTypeInbound,
                petSize = sharedViewModel.selectedPetSize,
                totalPrice = sharedViewModel.totalPrice
            )
            bookingReference = response
        }
    }

    //initializes the variables when go back to the previous screen
    fun goToPreviousScreen() {
        sharedViewModel.baggagePerPassenger.clear()
        sharedViewModel.petSize = ""
        sharedViewModel.totalPrice = sharedViewModel.prevTotalPrice
        showDialog = false
        sharedViewModel.tempPetPrice = 0
        prevPetPrice = 0
        sharedViewModel.tempBaggagePrice = 0
        prevBaggagePrice = 0
        oneTimeExecution = false
        selectedOption = radioOptions[1]
        selectedOptionForYes = ""
        sharedViewModel.finishReservation = 0
        bookingReference = ""
        passengersBaggage.clear()
        isClickPerPassenger.clear()
        sharedViewModel.limitBaggageFromMore.clear()
        baggage23kgPriceInbound = ""
        baggage23kgPriceOutbound = ""
        baggage32kgPriceInbound = ""
        baggage32kgPriceOutbound = ""
    }
}