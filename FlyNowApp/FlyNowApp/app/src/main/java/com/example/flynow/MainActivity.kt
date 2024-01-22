package com.example.flynow

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.animation.LinearEasing
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.BottomNavigation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//main activity that starts the app
class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for splash screen to apply the correct icon and some delay
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.loading.value
            }
        }

        setContent {
            App()
        }
        @SuppressLint("SourceLockedOrientationActivity")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

class MyViewModel : ViewModel() {
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    init {
        viewModelScope.launch {
            // run background task here
            delay(800)
            _loading.value = false
        }
    }
}

//the center App that start the whole app with different pages
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    //navController to navigate to different pages
    val navController = rememberNavController()
    //selectedIndex for the bottom bar
    val selectedIndex = remember {
        mutableIntStateOf(0)
    }
    val pageNow = remember {
        mutableIntStateOf(0)
    }
    //bottomBar state true or false accordingly if one page has bottom bar or not,
    //variables airportFrom and airportTo for "From" and "To" input fields
    val airportFrom = remember {
        mutableStateOf("")
    }
    val airportTo = remember {
        mutableStateOf("")
    }
    val bottomBarState = remember {
        mutableStateOf(true)
    }
    //whatAirport variable is if the click of the button of the text field is from "From" or "To"
    val whatAirport = remember {
        mutableIntStateOf(0)
    }
    //rentCar and locationToRent car are variables for screen "Rent a car"
    val rentCar = remember {
        mutableStateOf(false)
    }
    val locationToRentCar = remember {
        mutableStateOf("")
    }

    val oneWayDirectFlights = remember {
        mutableStateOf(mutableStateListOf<DirectFlight?>())
    }
    val returnDirectFlights = remember {
        mutableStateOf(mutableStateListOf<DirectFlight?>())
    }
    val oneWayOneStopFlights = remember {
        mutableStateOf(mutableStateListOf<OneStopFlight?>())
    }
    val returnOneStopFlights = remember {
        mutableStateOf(mutableStateListOf<OneStopFlight?>())
    }
    //variables that is stored the values of the textfields
    val departureDate = remember {
        mutableStateOf("")
    }
    val returnDate = remember {
        mutableStateOf("")
    }
    //counter for how many passengers are selected
    val passengersCounter = remember {
        mutableIntStateOf(1)
    }
    //variables for the completion of the booking of a reservation
    val totalPrice = remember {
        mutableDoubleStateOf(0.0)
    }
    val prevTotalPrice = remember {
        mutableDoubleStateOf(0.0)
    }
    val passengers: MutableList<PassengerInfo> = remember {
        mutableListOf()
    }
    val seats: MutableList<MutableList<MutableState<String>>> = remember{
        mutableListOf()
    }
    val listOfClassButtonsOutbound = remember {
        mutableListOf<ReservationType>()
    }
    val listOfClassButtonsInbound = remember {
        mutableListOf<ReservationType>()
    }
    val selectedFlights: MutableList<SelectedFlightDetails> = remember {
        mutableListOf()
    }
    repeat(4) {
        val selectedFlight = SelectedFlightDetails(
            mutableStateOf(""),mutableStateOf(""),mutableStateOf(""),
            mutableStateOf("")
        )
        selectedFlights.add(selectedFlight)
    }
    val selectedFlightOutbound = remember {
        mutableIntStateOf(0) //0 means that is direct flight, 1 is for one-stop flight
    }
    val selectedFlightInbound = remember {
        mutableIntStateOf(0) //0 means that is direct flight, 1 is for one-stop flight
    }
    val baggagePerPassenger: MutableList<MutableList<MutableIntState>> = remember {
        mutableStateListOf()
    }
    val bookingFailed = remember {
        mutableStateOf(false)
    }
    val classTypeOutbound = remember {
        mutableStateOf("")
    }
    val classTypeInbound = remember {
        mutableStateOf("")
    }
    val petSize = remember {
        mutableStateOf("")
    }
    //variables for the rent of car
    val listOfCars = remember {
        mutableListOf<CarDetails>()
    }
    val bookingIdFromCar = remember {
        mutableStateOf("")
    }
    val pickUpDateCar = remember {
        mutableStateOf("")
    }
    val pickUpHour = remember {
        mutableStateOf("10")
    }
    val pickUpMins = remember {
        mutableStateOf("30")
    }
    val returnDateCar = remember {
        mutableStateOf("")
    }
    val returnHour = remember {
        mutableStateOf("10")
    }
    val returnMins = remember {
        mutableStateOf("30")
    }
    val daysDifference = remember {
        mutableIntStateOf(1)
    }
    //variables for the my booking
    val petSizeMyBooking = remember {
        mutableStateOf("")
    }
    val bookingId = remember {
        mutableStateOf("")
    }
    val numOfPassengers = remember {
        mutableIntStateOf(0)
    }
    val wifiOnBoard = remember {
        mutableIntStateOf(-1)
    }
    val passengersMyBooking = remember {
        mutableStateOf(mutableStateListOf<PassengerInfo?>())
    }
    val flightsMyBooking = remember {
        mutableStateOf(mutableStateListOf<BasicFlight?>())
    }
    val baggageAndSeatMyBooking = remember {
        mutableStateOf(mutableStateListOf<BaggageAndSeatPerPassenger?>())
    }
    val carsMyBooking = remember {
        mutableStateOf(mutableStateListOf<CarDetailsMyBooking?>())
    }
    val oneWay = remember {
        mutableStateOf(false)
    }
    val outboundDirect = remember {
        mutableStateOf(false)
    }
    val inboundDirect = remember {
        mutableStateOf(false)
    }
    val totalPriceMyBooking = remember {
        mutableDoubleStateOf(0.0)
    }
    val rentingTotalPrice = remember {
        mutableDoubleStateOf(0.0)
    }
    //variables for the check in
    val directFlight = remember {
        mutableStateOf(false)
    }
    val bookingIdCheckIn = remember {
        mutableStateOf("")
    }
    val numOfPassengersCheckIn = remember {
        mutableIntStateOf(0)
    }
    val wifiOnBoardCheckIn = remember {
        mutableIntStateOf(-1)
    }
    val passengersCheckIn = remember {
        mutableStateOf(mutableStateListOf<PassengerInfo?>())
    }
    val flightsCheckIn = remember {
        mutableStateOf(mutableStateListOf<BasicFlight?>())
    }
    val baggageAndSeatCheckIn = remember {
        mutableStateOf(mutableStateListOf<BaggageAndSeatPerPassenger?>())
    }
    val petSizeCheckIn = remember {
        mutableStateOf("")
    }
    //initializes the bottom bar state for each page
    val navBackStackEntry = navController.currentBackStackEntry
    when (navBackStackEntry?.destination?.route) {
        "Home" -> {
            bottomBarState.value = true
        }
        "Book" -> {
            bottomBarState.value = true
        }
        "MyBooking" -> {
            bottomBarState.value = true
        }
        "MyBookingDetails" -> {
            bottomBarState.value = false
        }
        "More" -> {
            bottomBarState.value = true
        }
        "Airports" -> {
            bottomBarState.value = false
        }
        "CheckIn" -> {
            bottomBarState.value = false
        }
        "CheckInDetails" -> {
            bottomBarState.value = false
        }
        "Car" -> {
            bottomBarState.value = false
        }
        "Flights" -> {
            bottomBarState.value = false
        }
        "Passengers" -> {
            bottomBarState.value = false
        }
        "Seats" -> {
            bottomBarState.value = false
        }
        "BaggageAndPets" -> {
            bottomBarState.value = false
        }
        "SearchingCars" -> {
            bottomBarState.value = false
        }
        "WifiOnBoard" -> {
            bottomBarState.value = false
        }
        "UpgradeClass" -> {
            bottomBarState.value = false
        }
        "PetsFromMore" -> {
            bottomBarState.value = false
        }
        "BaggageFromMore" -> {
            bottomBarState.value = false
        }
    }

    //navigation between pages and connection with .kt files for each route
    Scaffold(bottomBar = {
        MyBottomNavigation(navController = navController,
            selectedIndex = selectedIndex,
            bottomBarState = bottomBarState,
            passengersCounter = passengersCounter)
    }) {
        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = Home.route) {
                composable(Home.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = true
                    }
                    HomeScreen(navController = navController,
                        pageNow = pageNow,
                        selectedIndex = selectedIndex,
                        airportFrom = airportFrom, airportTo = airportTo,
                        whatAirport = whatAirport, rentCar = rentCar)
                }
                composable(Book.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = true
                    }
                    BookScreen(
                        navController = navController,
                        pageNow = pageNow,
                        airportFrom = airportFrom, airportTo = airportTo,
                        whatAirport = whatAirport, rentCar = rentCar,
                        oneWayDirectFlights = oneWayDirectFlights,
                        returnDirectFlights = returnDirectFlights,
                        oneWayOneStopFlights = oneWayOneStopFlights,
                        returnOneStopFlights = returnOneStopFlights,
                        departureDate = departureDate,
                        returnDate = returnDate,
                        passengersCounter = passengersCounter,
                        listOfClassButtonsOutbound = listOfClassButtonsOutbound,
                        listOfClassButtonsInbound = listOfClassButtonsInbound)
                }
                composable(MyBooking.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = true
                    }
                    MyBookingScreen(
                        navController = navController,
                        oneWay = oneWay,
                        outboundDirect = outboundDirect,
                        inboundDirect = inboundDirect,
                        flightsMyBooking = flightsMyBooking,
                        passengersMyBooking = passengersMyBooking,
                        numOfPassengers = numOfPassengers,
                        petSizeMyBooking = petSizeMyBooking,
                        wifiOnBoard = wifiOnBoard,
                        bookingId = bookingId,
                        baggageAndSeatMyBooking = baggageAndSeatMyBooking,
                        carsMyBooking = carsMyBooking,
                        totalPriceMyBooking = totalPriceMyBooking,
                        rentingTotalPrice = rentingTotalPrice
                    )
                }
                composable(MyBookingDetails.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    MyBookingDetailsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        oneWay = oneWay,
                        outboundDirect = outboundDirect,
                        inboundDirect = inboundDirect,
                        flightsMyBooking = flightsMyBooking,
                        passengersMyBooking = passengersMyBooking,
                        numOfPassengers = numOfPassengers,
                        petSizeMyBooking = petSizeMyBooking,
                        wifiOnBoard = wifiOnBoard,
                        bookingId = bookingId,
                        baggageAndSeatMyBooking = baggageAndSeatMyBooking,
                        carsMyBooking = carsMyBooking,
                        totalPriceMyBooking = totalPriceMyBooking,
                        rentingTotalPrice = rentingTotalPrice
                    )
                }
                composable(More.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = true
                    }
                    MoreScreen(navController = navController,
                        selectedIndex = selectedIndex)
                }
                composable(Airports.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    AirportsScreen(navController = navController,
                        selectedIndex = selectedIndex,
                        airportFrom = airportFrom, airportTo = airportTo,
                        whatAirport = whatAirport, rentCar = rentCar,
                        locationToRentCar = locationToRentCar)
                }
                composable(CheckIn.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    CheckInScreen(
                        navController = navController,
                        bookingIdCheckIn = bookingIdCheckIn,
                        directFlight = directFlight,
                        flightsCheckIn = flightsCheckIn,
                        passengersCheckIn = passengersCheckIn,
                        numOfPassengersCheckIn = numOfPassengersCheckIn,
                        petSizeCheckIn = petSizeCheckIn,
                        wifiOnBoardCheckIn = wifiOnBoardCheckIn,
                        baggageAndSeatCheckIn = baggageAndSeatCheckIn
                    )
                }
                composable(CheckInDetails.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    CheckInDetailsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        bookingIdCheckIn = bookingIdCheckIn,
                        directFlight = directFlight,
                        flightsCheckIn = flightsCheckIn,
                        passengersCheckIn = passengersCheckIn,
                        numOfPassengersCheckIn = numOfPassengersCheckIn,
                        petSizeCheckIn = petSizeCheckIn,
                        wifiOnBoardCheckIn = wifiOnBoardCheckIn,
                        baggageAndSeatCheckIn = baggageAndSeatCheckIn
                    )
                }
                composable(Car.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    CarScreen(
                        navController = navController,
                        rentCar = rentCar,
                        locationToRentCar = locationToRentCar,
                        whatAirport = whatAirport,
                        pickUpDateCar = pickUpDateCar,
                        pickUpHour = pickUpHour,
                        pickUpMins = pickUpMins,
                        returnDateCar = returnDateCar,
                        returnHour = returnHour,
                        returnMins = returnMins,
                        bookingId = bookingIdFromCar,
                        listOfCars = listOfCars,
                        daysDifference = daysDifference
                    )
                }
                composable(Flights.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    FlightsScreen(
                        navController = navController,
                        oneWayDirectFlights = oneWayDirectFlights,
                        returnDirectFlights = returnDirectFlights,
                        oneWayOneStopFlights = oneWayOneStopFlights,
                        returnOneStopFlights = returnOneStopFlights,
                        pagePrevious = pageNow,
                        totalPrice = totalPrice,
                        passengersCounter = passengersCounter,
                        listOfClassButtonsOutbound = listOfClassButtonsOutbound,
                        listOfClassButtonsInbound = listOfClassButtonsInbound,
                        selectedFlights = selectedFlights,
                        selectedFlightOutbound = selectedFlightOutbound,
                        selectedFlightInbound = selectedFlightInbound,
                        seats = seats,
                        passengers = passengers,
                        classTypeOutbound = classTypeOutbound,
                        classTypeInbound = classTypeInbound,
                        baggagePerPassenger = baggagePerPassenger
                    )
                }
                composable(Passengers.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    PassengersScreen(
                        navController = navController,
                        totalPrice = totalPrice,
                        passengersCount = passengersCounter,
                        passengers = passengers,
                        listOfClassButtonsOutbound = listOfClassButtonsOutbound,
                        listOfClassButtonsInbound = listOfClassButtonsInbound,
                        selectedFlights = selectedFlights,
                        classTypeOutbound = classTypeOutbound,
                        classTypeInbound = classTypeInbound
                    )
                }
                composable(Seats.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    SeatsScreen(
                        navController = navController,
                        totalPrice = totalPrice,
                        passengersCount = passengersCounter,
                        passengers = passengers,
                        seats = seats,
                        pagePrevious = pageNow,
                        selectedFlights = selectedFlights,
                        selectedFlightOutbound = selectedFlightOutbound,
                        selectedFlightInbound = selectedFlightInbound,
                        prevTotalPrice = prevTotalPrice,
                        bookingFailed = bookingFailed
                    )
                }
                composable(BaggageAndPets.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    BaggageAndPetsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        totalPrice = totalPrice,
                        passengersCount = passengersCounter,
                        passengers = passengers,
                        seats = seats,
                        pagePrevious = pageNow,
                        selectedFlights = selectedFlights,
                        selectedFlightOutbound = selectedFlightOutbound,
                        selectedFlightInbound = selectedFlightInbound,
                        baggagePerPassenger = baggagePerPassenger,
                        classTypeOutbound = classTypeOutbound,
                        classTypeInbound = classTypeInbound,
                        prevTotalPrice = prevTotalPrice,
                        petSize = petSize,
                        airportFrom = airportFrom,
                        airportTo = airportTo,
                        whatAirport = whatAirport,
                        bookingFailed = bookingFailed
                    )
                }
                composable(SearchingCars.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    SearchingCarsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        listOfCars = listOfCars,
                        locationToRentCar = locationToRentCar,
                        pickUpDateCar = pickUpDateCar,
                        pickUpHour = pickUpHour,
                        pickUpMins = pickUpMins,
                        returnDateCar = returnDateCar,
                        returnHour = returnHour,
                        returnMins = returnMins,
                        bookingId = bookingIdFromCar,
                        daysDifference = daysDifference
                    )
                }
                composable(BaggageFromMore.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    NavigateWithCredentialsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        state = BaggageFromMore.route)
                }
                composable(PetsFromMore.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    NavigateWithCredentialsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        state = PetsFromMore.route)
                }
                composable(UpgradeClass.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    NavigateWithCredentialsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        state = UpgradeClass.route
                    )
                }
                composable(WifiOnBoard.route) {
                    LaunchedEffect(Unit) {
                        bottomBarState.value = false
                    }
                    NavigateWithCredentialsScreen(
                        navController = navController,
                        selectedIndex = selectedIndex,
                        state = WifiOnBoard.route
                    )
                }
            }
        }
    }
}

//function that removes or add the bottom navigation and contains
//four categories "Home", "Book", "My Booking", "More", each navigates to another page.
//In bottom bar each text contains and an descriptive icon
@Composable
fun MyBottomNavigation(navController: NavHostController,
                       selectedIndex: MutableState<Int>,
                       bottomBarState: MutableState<Boolean>,
                       passengersCounter: MutableIntState
) {
    val destinationList = listOf(
        Home,
        Book,
        MyBooking,
        More
    )

   AnimatedVisibility(visible = bottomBarState.value,
       enter = expandVertically(animationSpec = tween(durationMillis = 800,
           easing = LinearEasing), expandFrom = Alignment.Bottom),
       exit = shrinkVertically(animationSpec = tween(durationMillis = 800,
           easing = LinearEasing), shrinkTowards = Alignment.Top),
       content = {
           BottomNavigation(
               modifier = Modifier.size(450.dp, 57.dp),
               backgroundColor = Color(0xFF0096C7)
           ) {
               destinationList.forEachIndexed { index, destination ->
                   BottomNavigationItem(
                       selected = index == selectedIndex.value,
                       label = {
                           Text(
                               text = destination.title,
                               color = if (selectedIndex.value == index) Color.White else Color(
                                   0xAAFFFFFF
                               ),
                               fontSize = 12.sp,
                               fontFamily = FontFamily(
                                   fonts = listOf(
                                       Font(
                                           resId = R.font.opensans
                                       )
                                   )
                               ),
                               softWrap = false
                           )
                       },
                       icon = {
                           Icon(
                               painter = painterResource(id = destination.icon),
                               contentDescription = destination.title,
                               tint = if (selectedIndex.value == index) Color.White else Color(
                                   0xAAFFFFFF
                               ),
                               modifier = Modifier.size(27.dp)
                           )
                       },
                       onClick = {
                           selectedIndex.value = index
                           if(index == 0) {
                               passengersCounter.intValue = 1
                           }
                           navController.navigate(destination.route) {
                               popUpTo(Home.route)
                               launchSingleTop = true
                           }
                       },
                       selectedContentColor = Color(0xFFADE8F4)
                   )
               }
           }
       }
   )
}