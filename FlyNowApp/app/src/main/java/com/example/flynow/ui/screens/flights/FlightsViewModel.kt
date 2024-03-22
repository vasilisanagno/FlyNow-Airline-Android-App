package com.example.flynow.ui.screens.flights

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight
import com.example.flynow.model.PassengerInfo
import com.example.flynow.model.ReservationType
import com.example.flynow.model.SelectedFlightDetails
import com.example.flynow.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//view model class that keeps the state of the variables of the flight screen
//and has two function "handleClassButtons" and "prepareForTheNextScreen"
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class FlightsViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel
): ViewModel() {
    //these two variables are for the failure of the searching, if there are not flights
    var noResults by mutableIntStateOf(1)
    //these variables are useful for the sorting by price and sorting by departure time
    var sortPrice by mutableStateOf(false)
    var sortDepartureTime by mutableStateOf(false)
    var sortPriceReturn by mutableStateOf(false)
    var sortDepartureTimeReturn by mutableStateOf(false)

    //handles the press of the class buttons(economy,flex,business) and change
    //changes the total price each time according each class
    fun handleClassButtons(
        directFlight: DirectFlight?,
        oneStopFlight: OneStopFlight?,
        flightClass: String,
        type: String,
        listOfClassButtons: MutableList<ReservationType>,
        index: Int
    ) {
        listOfClassButtons.forEach { item ->
            if(type=="outbound") {
                if (item.economyClassClicked.value) {
                    sharedViewModel.totalPriceOneWay = 0.0
                } else if (item.flexClassClicked.value) {
                    sharedViewModel.totalPriceOneWay = 0.0
                } else if (item.businessClassClicked.value) {
                    sharedViewModel.totalPriceOneWay = 0.0
                }
            }
            else if(type=="inbound") {
                if (item.economyClassClicked.value) {
                    sharedViewModel.totalPriceReturn = 0.0
                } else if (item.flexClassClicked.value) {
                    sharedViewModel.totalPriceReturn = 0.0
                } else if (item.businessClassClicked.value) {
                    sharedViewModel.totalPriceReturn = 0.0
                }
            }
        }
        when (flightClass) {
            "economy" -> {
                listOfClassButtons[index].economyClassClicked.value =
                    !listOfClassButtons[index].economyClassClicked.value
            }
            "flex" -> {
                listOfClassButtons[index].flexClassClicked.value =
                    !listOfClassButtons[index].flexClassClicked.value
            }
            else -> {
                listOfClassButtons[index].businessClassClicked.value =
                    !listOfClassButtons[index].businessClassClicked.value
            }
        }
        if((flightClass=="economy"&&listOfClassButtons[index].economyClassClicked.value)||
            (flightClass=="flex"&&listOfClassButtons[index].flexClassClicked.value)||
            (flightClass=="business"&&listOfClassButtons[index].businessClassClicked.value)) {
            if(type=="outbound") {
                when (flightClass) {
                    "economy" -> {
                        sharedViewModel.totalPriceOneWay = directFlight?.economyPrice
                            ?: (oneStopFlight!!.firstEconomyPrice + oneStopFlight.secondEconomyPrice)
                    }
                    "flex" -> {
                        sharedViewModel.totalPriceOneWay = directFlight?.flexPrice
                            ?: (oneStopFlight!!.firstFlexPrice + oneStopFlight.secondFlexPrice)
                    }
                    "business" -> {
                        sharedViewModel.totalPriceOneWay = directFlight?.businessPrice
                            ?: (oneStopFlight!!.firstBusinessPrice + oneStopFlight.secondBusinessPrice)
                    }
                }
            }
            else if(type=="inbound") {
                when (flightClass) {
                    "economy" -> {
                        sharedViewModel.totalPriceReturn = directFlight?.economyPrice
                            ?: (oneStopFlight!!.firstEconomyPrice + oneStopFlight.secondEconomyPrice)
                    }
                    "flex" -> {
                        sharedViewModel.totalPriceReturn = directFlight?.flexPrice
                            ?: (oneStopFlight!!.firstFlexPrice + oneStopFlight.secondFlexPrice)
                    }
                    "business" -> {
                        sharedViewModel.totalPriceReturn = directFlight?.businessPrice
                            ?: (oneStopFlight!!.firstBusinessPrice + oneStopFlight.secondBusinessPrice)
                    }
                }
            }
        }
        listOfClassButtons.forEachIndexed { indexClass, item ->
            when (flightClass) {
                "economy" -> {
                    if(indexClass!=index) {
                        item.economyClassClicked.value = false
                    }
                    item.flexClassClicked.value = false
                    item.businessClassClicked.value = false
                }
                "flex" -> {
                    if(indexClass!=index) {
                        item.flexClassClicked.value = false
                    }
                    item.economyClassClicked.value = false
                    item.businessClassClicked.value = false
                }
                "business" -> {
                    if(indexClass!=index) {
                        item.businessClassClicked.value = false
                    }
                    item.economyClassClicked.value = false
                    item.flexClassClicked.value = false
                }
            }
        }
    }

    //this function is called before the navigation to the next screen
    //to make the proper initialization of the variables to be ready for the next screen
    fun prepareForTheNextScreen(): Boolean {
        sharedViewModel.passengers.clear()
        repeat(sharedViewModel.passengersCounter) {
            val passenger = PassengerInfo(
                mutableStateOf(""),mutableStateOf(""),
                mutableStateOf(""), mutableStateOf(""),
                mutableStateOf(""),mutableStateOf("")
            )
            sharedViewModel.passengers.add(passenger)
        }
        repeat(4) {
            val selectedFlight = SelectedFlightDetails(
                mutableStateOf(""),mutableStateOf(""),mutableStateOf(""),
                mutableStateOf("")
            )
            sharedViewModel.selectedFlights.add(selectedFlight)
        }
        sharedViewModel.totalPrice = sharedViewModel.totalPriceOneWay + sharedViewModel.totalPriceReturn
        var indexDirect = 0
        var indexOneStop = 0
        //takes the selected prices of the flights and the selected flight details for outbound and inbound
        sharedViewModel.listOfClassButtonsOutbound.forEachIndexed { index, button ->
            if(button.economyClassClicked.value || button.flexClassClicked.value || button.businessClassClicked.value) {
                if(sharedViewModel.oneWayDirectFlights.size > index) {
                    sharedViewModel.selectedFlightOutbound = 0
                    sharedViewModel.selectedFlights[0].flightId.value = sharedViewModel.oneWayDirectFlights[indexDirect]!!.flightId
                    sharedViewModel.selectedFlights[0].departureCity.value = sharedViewModel.oneWayDirectFlights[indexDirect]!!.departureCity
                    sharedViewModel.selectedFlights[0].arrivalCity.value = sharedViewModel.oneWayDirectFlights[indexDirect]!!.arrivalCity
                    sharedViewModel.selectedFlights[0].airplaneModel.value = sharedViewModel.oneWayDirectFlights[indexDirect]!!.airplaneModel
                    indexDirect++
                }
                else {
                    sharedViewModel.selectedFlightOutbound = 1
                    sharedViewModel.selectedFlights[0].flightId.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.firstFlightId
                    sharedViewModel.selectedFlights[0].departureCity.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.firstDepartureCity
                    sharedViewModel.selectedFlights[0].arrivalCity.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.firstArrivalCity
                    sharedViewModel.selectedFlights[0].airplaneModel.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.firstAirplaneModel
                    sharedViewModel.selectedFlights[1].flightId.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.secondFlightId
                    sharedViewModel.selectedFlights[1].departureCity.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.secondDepartureCity
                    sharedViewModel.selectedFlights[1].arrivalCity.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.secondArrivalCity
                    sharedViewModel.selectedFlights[1].airplaneModel.value = sharedViewModel.oneWayOneStopFlights[indexOneStop]!!.secondAirplaneModel
                    indexOneStop++
                }
                if(button.economyClassClicked.value) {
                    sharedViewModel.classTypeOutbound = "Economy"
                }
                else if(button.flexClassClicked.value) {
                    sharedViewModel.classTypeOutbound = "Flex"
                }
                else {
                    sharedViewModel.classTypeOutbound = "Business"
                }
            }
            else {
                if(sharedViewModel.listOfClassButtonsOutbound[index].directOrOneStop.intValue == 0) {
                    indexDirect++
                }
                else {
                    indexOneStop++
                }
            }
        }
        indexDirect = 0
        indexOneStop = 0
        if(sharedViewModel.page == 1) {
            sharedViewModel.listOfClassButtonsInbound.forEachIndexed { index, button ->
                if(button.economyClassClicked.value || button.flexClassClicked.value || button.businessClassClicked.value) {
                    if(sharedViewModel.returnDirectFlights.size > index) {
                        sharedViewModel.selectedFlightInbound = 0
                        if(sharedViewModel.selectedFlightOutbound == 0) {
                            sharedViewModel.selectedFlights[1].flightId.value = sharedViewModel.returnDirectFlights[indexDirect]!!.flightId
                            sharedViewModel.selectedFlights[1].departureCity.value = sharedViewModel.returnDirectFlights[indexDirect]!!.departureCity
                            sharedViewModel.selectedFlights[1].arrivalCity.value = sharedViewModel.returnDirectFlights[indexDirect]!!.arrivalCity
                            sharedViewModel.selectedFlights[1].airplaneModel.value = sharedViewModel.returnDirectFlights[indexDirect]!!.airplaneModel
                            indexDirect++
                        }
                        else {
                            sharedViewModel.selectedFlights[2].flightId.value = sharedViewModel.returnDirectFlights[indexDirect]!!.flightId
                            sharedViewModel.selectedFlights[2].departureCity.value = sharedViewModel.returnDirectFlights[indexDirect]!!.departureCity
                            sharedViewModel.selectedFlights[2].arrivalCity.value = sharedViewModel.returnDirectFlights[indexDirect]!!.arrivalCity
                            sharedViewModel.selectedFlights[2].airplaneModel.value = sharedViewModel.returnDirectFlights[indexDirect]!!.airplaneModel
                            indexDirect++
                        }
                    }
                    else {
                        sharedViewModel.selectedFlightInbound = 1
                        if(sharedViewModel.selectedFlightOutbound == 0) {
                            sharedViewModel.selectedFlights[1].flightId.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstFlightId
                            sharedViewModel.selectedFlights[1].departureCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstDepartureCity
                            sharedViewModel.selectedFlights[1].arrivalCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstArrivalCity
                            sharedViewModel.selectedFlights[1].airplaneModel.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstAirplaneModel
                            sharedViewModel.selectedFlights[2].flightId.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondFlightId
                            sharedViewModel.selectedFlights[2].departureCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondDepartureCity
                            sharedViewModel.selectedFlights[2].arrivalCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondArrivalCity
                            sharedViewModel.selectedFlights[2].airplaneModel.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondAirplaneModel
                            indexOneStop++
                        }
                        else {
                            sharedViewModel.selectedFlights[2].flightId.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstFlightId
                            sharedViewModel.selectedFlights[2].departureCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstDepartureCity
                            sharedViewModel.selectedFlights[2].arrivalCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstArrivalCity
                            sharedViewModel.selectedFlights[2].airplaneModel.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.firstAirplaneModel
                            sharedViewModel.selectedFlights[3].flightId.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondFlightId
                            sharedViewModel.selectedFlights[3].departureCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondDepartureCity
                            sharedViewModel.selectedFlights[3].arrivalCity.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondArrivalCity
                            sharedViewModel.selectedFlights[3].airplaneModel.value = sharedViewModel.returnOneStopFlights[indexOneStop]!!.secondAirplaneModel
                            indexOneStop++
                        }
                    }
                    if(button.economyClassClicked.value) {
                        sharedViewModel.classTypeInbound = "Economy"
                    }
                    else if(button.flexClassClicked.value) {
                        sharedViewModel.classTypeInbound = "Flex"
                    }
                    else {
                        sharedViewModel.classTypeInbound = "Business"
                    }
                }
                else {
                    if(sharedViewModel.listOfClassButtonsInbound[index].directOrOneStop.intValue == 0) {
                        indexDirect++
                    }
                    else {
                        indexOneStop++
                    }
                }
            }
        }
        sortPrice = false
        sortPriceReturn = false
        sortDepartureTime = false
        sortDepartureTimeReturn = false
        sharedViewModel.oneWayDirectFlights = sharedViewModel.oneWayDirectFlightsOriginal.toMutableStateList()
        sharedViewModel.oneWayOneStopFlights = sharedViewModel.oneWayOneStopFlightsOriginal.toMutableStateList()
        sharedViewModel.returnDirectFlights = sharedViewModel.returnDirectFlightsOriginal.toMutableStateList()
        sharedViewModel.returnOneStopFlights = sharedViewModel.returnOneStopFlightsOriginal.toMutableStateList()
        //total price is multiplied by num of passengers
        sharedViewModel.totalPrice = sharedViewModel.passengersCounter*sharedViewModel.totalPrice
        sharedViewModel.seats.clear()
        sharedViewModel.baggagePerPassenger.clear()

        return true
    }

    //if sortPrice is true sorting by price,
    //else assigns the original flights to the lists
    //the same with the sortDepartureTime that sorting by departure time,
    //these are for outbound flights
    fun sortingOutbound() {
        sharedViewModel.oneWayDirectFlights = sharedViewModel.oneWayDirectFlightsOriginal.toMutableStateList()
        sharedViewModel.oneWayOneStopFlights = sharedViewModel.oneWayOneStopFlightsOriginal.toMutableStateList()
        if(sortPrice) {
            sharedViewModel.oneWayDirectFlights.sortBy {
                it!!.economyPrice
            }
            sharedViewModel.oneWayOneStopFlights.sortBy {
                it!!.firstEconomyPrice+ it.secondEconomyPrice
            }
        }
        if(sortDepartureTime) {
            sharedViewModel.oneWayDirectFlights.sortBy {
                it!!.departureTime
            }
            sharedViewModel.oneWayOneStopFlights.sortBy {
                it!!.firstDepartureTime
            }
        }
    }

    //the same with the above but for the inbound flights
    fun sortingInbound() {
        sharedViewModel.returnDirectFlights = sharedViewModel.returnDirectFlightsOriginal.toMutableStateList()
        sharedViewModel.returnOneStopFlights = sharedViewModel.returnOneStopFlightsOriginal.toMutableStateList()
        if(sortPriceReturn) {
            sharedViewModel.returnDirectFlights.sortBy {
                it!!.economyPrice
            }
            sharedViewModel.returnOneStopFlights.sortBy {
                it!!.firstEconomyPrice+ it.secondEconomyPrice
            }
        }
        if(sortDepartureTimeReturn) {
            sharedViewModel.returnDirectFlights.sortBy {
                it!!.departureTime
            }
            sharedViewModel.returnOneStopFlights.sortBy {
                it!!.firstDepartureTime
            }
        }
    }

    //initialization the variables of current screen
    fun goToPreviousScreen() {
        sharedViewModel.totalPriceOneWay = 0.0
        sharedViewModel.totalPriceReturn = 0.0
        noResults = 1
        sortPrice = false
        sortPriceReturn = false
        sortDepartureTime = false
        sortDepartureTimeReturn = false
        sharedViewModel.passengersCounter = 1
        sharedViewModel.listOfClassButtonsOutbound.clear()
        sharedViewModel.listOfClassButtonsInbound.clear()
        sharedViewModel.seats.clear()
        sharedViewModel.passengers.clear()
        sharedViewModel.baggagePerPassenger.clear()
        sharedViewModel.selectedFlights.forEach { flight ->
            flight.flightId.value = ""
            flight.departureCity.value = ""
            flight.arrivalCity.value = ""
            flight.airplaneModel.value = ""
        }
    }
}