package com.example.flynow.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.CheckRepository
import com.example.flynow.model.BaggageAndSeatPerPassenger
import com.example.flynow.model.BasicFlight
import com.example.flynow.model.CarDetails
import com.example.flynow.model.CarDetailsMyBooking
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight
import com.example.flynow.model.PassengerInfo
import com.example.flynow.model.ReservationType
import com.example.flynow.model.SelectedFlightDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that keeps the state of shared variables that are used for many screens
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: CheckRepository
): ViewModel() {
    //selectedIndex for the bottom bar
    var selectedIndex by mutableIntStateOf(0)
    //variables airportFrom and airportTo for "From" and "To" input fields
    var airportFrom by mutableStateOf("")
    var airportTo by mutableStateOf("")
    //whatAirport variable is if the click of the button of the text field is from "From" or "To"
    var whatAirport by mutableIntStateOf(0)
    //is used for horizontal pager because there are two pages "one-way" and "round" trip
    var page by mutableIntStateOf(0)
    //rentCar and locationToRent car are variables for screen "Rent a car"
    var rentCar by mutableStateOf(false)
    var locationToRentCar by mutableStateOf("")

    var showProgressBar by mutableStateOf(false)
    //variables for storing the two text inputs values for booking id and lastname
    var textBookingId by mutableStateOf("")
    var textLastname by mutableStateOf("")
    var hasError by mutableStateOf(false)
    var buttonClickedCredentials by mutableStateOf(false)

    //all the possible flights that come from the database
    var oneWayDirectFlights by mutableStateOf(mutableStateListOf<DirectFlight?>())
    var returnDirectFlights by mutableStateOf(mutableStateListOf<DirectFlight?>())
    var oneWayOneStopFlights by mutableStateOf(mutableStateListOf<OneStopFlight?>())
    var returnOneStopFlights by mutableStateOf(mutableStateListOf<OneStopFlight?>())
    //keeps the original flights
    var oneWayDirectFlightsOriginal by mutableStateOf(mutableStateListOf<DirectFlight?>())
    var oneWayOneStopFlightsOriginal by mutableStateOf(mutableStateListOf<OneStopFlight?>())
    var returnDirectFlightsOriginal by mutableStateOf(mutableStateListOf<DirectFlight?>())
    var returnOneStopFlightsOriginal by mutableStateOf(mutableStateListOf<OneStopFlight?>())

    var seeBottomBar by mutableStateOf(true)

    //total price for the outbound and inbound
    var totalPriceOneWay by mutableDoubleStateOf(0.00)
    var totalPriceReturn by mutableDoubleStateOf(0.00)
    //counter for how many passengers are selected
    var passengersCounter by mutableIntStateOf(1)
    //class buttons(economy,flex,business) for outbound and inbound
    var listOfClassButtonsOutbound by mutableStateOf(mutableListOf<ReservationType>())
    var listOfClassButtonsInbound by mutableStateOf(mutableListOf<ReservationType>())

    //variables for the completion of the booking of a reservation
    var totalPrice by mutableDoubleStateOf(0.0)

    //these variables are used during finishing the booking and making a new reservation
    var prevTotalPrice by mutableDoubleStateOf(0.0)
    val passengers by mutableStateOf(mutableListOf<PassengerInfo>())
    var seats by mutableStateOf(mutableListOf<MutableList<MutableState<String>>>())
    val selectedFlights by mutableStateOf(mutableListOf<SelectedFlightDetails>())
    //0 means that is direct flight, 1 is for one-stop flight
    var selectedFlightOutbound by mutableIntStateOf(0)
    var selectedFlightInbound by mutableIntStateOf(0)
    val baggagePerPassenger by mutableStateOf(mutableStateListOf<MutableList<MutableIntState>>())
    var bookingFailed by  mutableStateOf(false)
    var classTypeOutbound by mutableStateOf("")
    var classTypeInbound by mutableStateOf("")
    var petSize by mutableStateOf("")
    //this variable is not to crash when the reservation is finished and go to home screen
    var finishReservation by mutableIntStateOf(0)

    //more screen choices
    var showDialog by mutableStateOf(false)
    var showDialogConfirm by mutableStateOf(false)
    var bookingExists by mutableStateOf(false)
    var wifiMore by mutableStateOf(false)
    //info from database for wifi
    var wifiInfo by mutableIntStateOf(-1)
    var updateWifi by mutableStateOf(false)

    var upgradeToBusinessMore by mutableStateOf(false)
    //info from database for classes of flights
    var upgradeToBusinessInfo by mutableStateOf(mutableListOf<String>())
    var updateBusiness by mutableStateOf(false)

    //is for prices in baggage and pets screen and baggage from more screen
    var tempBaggagePrice by mutableIntStateOf(0)
    var baggageFromMore by mutableStateOf(false)
    var updateBaggage by mutableStateOf(false)
    var oneWayInBaggage by mutableStateOf(false)
    //how many baggage more can select, the maximum is five baggage
    val limitBaggageFromMore by mutableStateOf(mutableListOf<Int>())

    var petsFromMore by mutableStateOf(false)
    var tempPetPrice by mutableIntStateOf(0)
    var selectedPetSize by mutableStateOf("")
    var updatePets by mutableStateOf(false)

    //variables for the rent of car
    var listOfCars by mutableStateOf(mutableListOf<CarDetails>())
    var pickUpDateCar by mutableStateOf("")
    var pickUpHour by mutableStateOf("10")
    var pickUpMins by mutableStateOf("30")
    var returnDateCar by mutableStateOf("")
    var returnHour by mutableStateOf("10")
    var returnMins by mutableStateOf("30")
    var daysDifference by mutableIntStateOf(1)
    //buttons boolean values for each car to see what car is selected
    val listOfButtonsCars by mutableStateOf(mutableListOf<MutableState<Boolean>>())

    //variables for the my booking
    var myBooking by mutableStateOf(false)
    var petSizeMyBooking by mutableStateOf("")
    var numOfPassengers by mutableIntStateOf(0)
    var wifiOnBoard by mutableIntStateOf(-1)
    var passengersMyBooking by mutableStateOf(mutableStateListOf<PassengerInfo?>())
    var flightsMyBooking by mutableStateOf(mutableStateListOf<BasicFlight?>())
    var baggageAndSeatMyBooking by mutableStateOf(mutableStateListOf<BaggageAndSeatPerPassenger?>())
    var carsMyBooking by mutableStateOf(mutableStateListOf<CarDetailsMyBooking?>())
    var oneWay by mutableStateOf(false)
    var outboundDirect by mutableStateOf(false)
    var inboundDirect by mutableStateOf(false)
    var totalPriceMyBooking by mutableDoubleStateOf(0.0)
    var rentingTotalPrice by mutableDoubleStateOf(0.0)
    var backButton by mutableStateOf(false)

    //variables for the check in
    var checkIn by mutableStateOf(false)
    var checkInOpen by mutableStateOf(true)
    var directFlight by mutableStateOf(false)
    var numOfPassengersCheckIn by mutableIntStateOf(0)
    var wifiOnBoardCheckIn by mutableIntStateOf(-1)
    var passengersCheckIn by mutableStateOf(mutableStateListOf<PassengerInfo?>())
    var flightsCheckIn by mutableStateOf(mutableStateListOf<BasicFlight?>())
    var baggageAndSeatCheckIn by mutableStateOf(mutableStateListOf<BaggageAndSeatPerPassenger?>())
    var petSizeCheckIn by mutableStateOf("")
    val checkedState by mutableStateOf(mutableStateListOf<MutableState<Boolean>>())

    //function that communicates with the server to check if the booking exists
    fun checkBookingExists() {
        viewModelScope.launch {
            bookingExists = repository.checkIfBookingExists(
                bookingId = textBookingId,
                lastname = textLastname
            )
        }
    }
}
