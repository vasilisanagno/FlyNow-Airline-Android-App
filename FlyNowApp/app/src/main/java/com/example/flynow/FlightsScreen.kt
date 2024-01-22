package com.example.flynow

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import java.time.LocalTime
import java.time.format.DateTimeFormatter

//navController helps to navigate to previous page or next page,
//flights variables are the stored flights after select query in the database,
//pagePrevious show if the search begins from one way flights or round trip flights,
//total price is the total price of the reservation and is increasing according to next pages
//passengerCounter is the num of passengers in the reservation,
//the two variables with class buttons contains all the buttons of the outbound and inbound flights
//variable selected flights stores the flights that the user select,
//and selected flight outbound or inbound if it is 0 is the flight direct or 1 for flight with return,
//seats,passengers,classType inbound or outbound and baggagePerPassenger are for the next pages
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType",
    "MutableCollectionMutableState"
)
@Composable
fun FlightsScreen(
    navController: NavController,
    oneWayDirectFlights: MutableState<SnapshotStateList<DirectFlight?>>,
    returnDirectFlights: MutableState<SnapshotStateList<DirectFlight?>>,
    oneWayOneStopFlights: MutableState<SnapshotStateList<OneStopFlight?>>,
    returnOneStopFlights: MutableState<SnapshotStateList<OneStopFlight?>>,
    pagePrevious: MutableIntState,
    totalPrice: MutableDoubleState,
    passengersCounter: MutableIntState,
    listOfClassButtonsOutbound: MutableList<ReservationType>,
    listOfClassButtonsInbound: MutableList<ReservationType>,
    selectedFlights: MutableList<SelectedFlightDetails>,
    selectedFlightOutbound: MutableIntState,
    selectedFlightInbound: MutableIntState,
    seats: MutableList<MutableList<MutableState<String>>>,
    passengers: MutableList<PassengerInfo>,
    classTypeOutbound: MutableState<String>,
    classTypeInbound: MutableState<String>,
    baggagePerPassenger: MutableList<MutableList<MutableIntState>>
) {
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    //initialization of the two list of buttons of the flights
    if(listOfClassButtonsOutbound.size == 0) {
        repeat(oneWayDirectFlights.value.size + oneWayOneStopFlights.value.size) {
            listOfClassButtonsOutbound.add(
                ReservationType(
                    mutableStateOf(false),
                    mutableStateOf(false),
                    mutableStateOf(false),
                    mutableIntStateOf(0)
                )
            )
        }
    }
    if(listOfClassButtonsInbound.size == 0) {
        repeat(returnDirectFlights.value.size + returnOneStopFlights.value.size) {
            listOfClassButtonsInbound.add(
                ReservationType(
                    mutableStateOf(false),
                    mutableStateOf(false),
                    mutableStateOf(false),
                    mutableIntStateOf(0)
                )
            )
        }
    }
    //keeps the original flights
    val oneWayDirectFlightsOriginal:MutableState<SnapshotStateList<DirectFlight?>>  = remember {
        mutableStateOf(oneWayDirectFlights.value)
    }
    val oneWayOneStopFlightsOriginal:MutableState<SnapshotStateList<OneStopFlight?>>  = remember {
        mutableStateOf(oneWayOneStopFlights.value)
    }
    val returnDirectFlightsOriginal:MutableState<SnapshotStateList<DirectFlight?>>  = remember {
        mutableStateOf(returnDirectFlights.value)
    }
    val returnOneStopFlightsOriginal:MutableState<SnapshotStateList<OneStopFlight?>>  = remember {
        mutableStateOf(returnOneStopFlights.value)
    }

    //total price for the outbound and inbound
    val totalPriceOneWay = remember {
        mutableDoubleStateOf(0.00)
    }
    val totalPriceReturn = remember {
        mutableDoubleStateOf(0.00)
    }
    //these two variables are for the failure of the searching, if there are not flights
    val noResults = remember {
        mutableIntStateOf(1)
    }
    val seeBottomBar = remember {
        mutableStateOf(true)
    }
    //these variables are useful for the sorting by price and sorting by departure time
    val sortPrice = remember {
        mutableStateOf(false)
    }
    val sortDepartureTime = remember {
        mutableStateOf(false)
    }
    val sortPriceReturn = remember {
        mutableStateOf(false)
    }
    val sortDepartureTimeReturn = remember {
        mutableStateOf(false)
    }
    //if sortPrice is true sorting by price,
    //else assigns the original flights to the lists
    //the same with the sortDepartureTime that sorting by departure time,
    //these are for outbound flights
    LaunchedEffect(sortPrice.value, sortDepartureTime.value) {
        oneWayDirectFlights.value = oneWayDirectFlightsOriginal.value.toMutableStateList()
        oneWayOneStopFlights.value = oneWayOneStopFlightsOriginal.value.toMutableStateList()
        if(sortPrice.value) {
            oneWayDirectFlights.value.sortBy {
                it!!.economyPrice
            }
            oneWayOneStopFlights.value.sortBy {
                it!!.firstEconomyPrice+ it.secondEconomyPrice
            }
        }
        if(sortDepartureTime.value) {
            oneWayDirectFlights.value.sortBy {
                it!!.departureTime
            }
            oneWayOneStopFlights.value.sortBy {
                it!!.firstDepartureTime
            }
        }
    }
    //the same with the above but for the inbound flights
    LaunchedEffect(sortPriceReturn.value,sortDepartureTimeReturn.value) {
        returnDirectFlights.value = returnDirectFlightsOriginal.value.toMutableStateList()
        returnOneStopFlights.value = returnOneStopFlightsOriginal.value.toMutableStateList()
        if(sortPriceReturn.value) {
            returnDirectFlights.value.sortBy {
                it!!.economyPrice
            }
            returnOneStopFlights.value.sortBy {
                it!!.firstEconomyPrice+ it.secondEconomyPrice
            }
        }
        if(sortDepartureTimeReturn.value) {
            returnDirectFlights.value.sortBy {
                it!!.departureTime
            }
            returnOneStopFlights.value.sortBy {
                it!!.firstDepartureTime
            }
        }
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        if(seeBottomBar.value) {
            BottomAppBar(backgroundColor = Color.Transparent,
                contentColor = Color.Transparent,
                modifier = Modifier
                    .height(60.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentPadding = PaddingValues(0.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        "Total Price: ${totalPriceOneWay.doubleValue + totalPriceReturn.doubleValue} €",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    //Button "Continue" that navigates the user to the next step of the reservation
                    Button(
                        onClick = {
                            totalPrice.doubleValue = totalPriceOneWay.doubleValue + totalPriceReturn.doubleValue
                            var indexDirect = 0
                            var indexOneStop = 0
                            //takes the selected prices of the flights and the selected flight details for outbound and inbound
                            listOfClassButtonsOutbound.forEachIndexed { index, button ->
                                if(button.economyClassClicked.value || button.flexClassClicked.value || button.businessClassClicked.value) {
                                    if(oneWayDirectFlights.value.size > index) {
                                        selectedFlightOutbound.intValue = 0
                                        selectedFlights[0].flightId.value = oneWayDirectFlights.value[indexDirect]!!.flightId
                                        selectedFlights[0].departureCity.value = oneWayDirectFlights.value[indexDirect]!!.departureCity
                                        selectedFlights[0].arrivalCity.value = oneWayDirectFlights.value[indexDirect]!!.arrivalCity
                                        selectedFlights[0].airplaneModel.value = oneWayDirectFlights.value[indexDirect]!!.airplaneModel
                                        indexDirect++
                                    }
                                    else {
                                        selectedFlightOutbound.intValue = 1
                                        selectedFlights[0].flightId.value = oneWayOneStopFlights.value[indexOneStop]!!.firstFlightId
                                        selectedFlights[0].departureCity.value = oneWayOneStopFlights.value[indexOneStop]!!.firstDepartureCity
                                        selectedFlights[0].arrivalCity.value = oneWayOneStopFlights.value[indexOneStop]!!.firstArrivalCity
                                        selectedFlights[0].airplaneModel.value = oneWayOneStopFlights.value[indexOneStop]!!.firstAirplaneModel
                                        selectedFlights[1].flightId.value = oneWayOneStopFlights.value[indexOneStop]!!.secondFlightId
                                        selectedFlights[1].departureCity.value = oneWayOneStopFlights.value[indexOneStop]!!.secondDepartureCity
                                        selectedFlights[1].arrivalCity.value = oneWayOneStopFlights.value[indexOneStop]!!.secondArrivalCity
                                        selectedFlights[1].airplaneModel.value = oneWayOneStopFlights.value[indexOneStop]!!.secondAirplaneModel
                                        indexOneStop++
                                    }
                                    if(button.economyClassClicked.value) {
                                        classTypeOutbound.value = "Economy"
                                    }
                                    else if(button.flexClassClicked.value) {
                                        classTypeOutbound.value = "Flex"
                                    }
                                    else {
                                        classTypeOutbound.value = "Business"
                                    }
                                }
                                else {
                                    if(listOfClassButtonsOutbound[index].directOrOneStop.intValue == 0) {
                                        indexDirect++
                                    }
                                    else {
                                        indexOneStop++
                                    }
                                }
                            }
                            indexDirect = 0
                            indexOneStop = 0
                            if(pagePrevious.intValue == 1) {
                                listOfClassButtonsInbound.forEachIndexed { index, button ->
                                    if(button.economyClassClicked.value || button.flexClassClicked.value || button.businessClassClicked.value) {
                                        if(returnDirectFlights.value.size > index) {
                                            selectedFlightInbound.intValue = 0
                                            if(selectedFlightOutbound.intValue == 0) {
                                                selectedFlights[1].flightId.value = returnDirectFlights.value[indexDirect]!!.flightId
                                                selectedFlights[1].departureCity.value = returnDirectFlights.value[indexDirect]!!.departureCity
                                                selectedFlights[1].arrivalCity.value = returnDirectFlights.value[indexDirect]!!.arrivalCity
                                                selectedFlights[1].airplaneModel.value = returnDirectFlights.value[indexDirect]!!.airplaneModel
                                                indexDirect++
                                            }
                                            else {
                                                selectedFlights[2].flightId.value = returnDirectFlights.value[indexDirect]!!.flightId
                                                selectedFlights[2].departureCity.value = returnDirectFlights.value[indexDirect]!!.departureCity
                                                selectedFlights[2].arrivalCity.value = returnDirectFlights.value[indexDirect]!!.arrivalCity
                                                selectedFlights[2].airplaneModel.value = returnDirectFlights.value[indexDirect]!!.airplaneModel
                                                indexDirect++
                                            }
                                        }
                                        else {
                                            selectedFlightInbound.intValue = 1
                                            if(selectedFlightOutbound.intValue == 0) {
                                                selectedFlights[1].flightId.value = returnOneStopFlights.value[indexOneStop]!!.firstFlightId
                                                selectedFlights[1].departureCity.value = returnOneStopFlights.value[indexOneStop]!!.firstDepartureCity
                                                selectedFlights[1].arrivalCity.value = returnOneStopFlights.value[indexOneStop]!!.firstArrivalCity
                                                selectedFlights[1].airplaneModel.value = returnOneStopFlights.value[indexOneStop]!!.firstAirplaneModel
                                                selectedFlights[2].flightId.value = returnOneStopFlights.value[indexOneStop]!!.secondFlightId
                                                selectedFlights[2].departureCity.value = returnOneStopFlights.value[indexOneStop]!!.secondDepartureCity
                                                selectedFlights[2].arrivalCity.value = returnOneStopFlights.value[indexOneStop]!!.secondArrivalCity
                                                selectedFlights[2].airplaneModel.value = returnOneStopFlights.value[indexOneStop]!!.secondAirplaneModel
                                                indexOneStop++
                                            }
                                            else {
                                                selectedFlights[2].flightId.value = returnOneStopFlights.value[indexOneStop]!!.firstFlightId
                                                selectedFlights[2].departureCity.value = returnOneStopFlights.value[indexOneStop]!!.firstDepartureCity
                                                selectedFlights[2].arrivalCity.value = returnOneStopFlights.value[indexOneStop]!!.firstArrivalCity
                                                selectedFlights[2].airplaneModel.value = returnOneStopFlights.value[indexOneStop]!!.firstAirplaneModel
                                                selectedFlights[3].flightId.value = returnOneStopFlights.value[indexOneStop]!!.secondFlightId
                                                selectedFlights[3].departureCity.value = returnOneStopFlights.value[indexOneStop]!!.secondDepartureCity
                                                selectedFlights[3].arrivalCity.value = returnOneStopFlights.value[indexOneStop]!!.secondArrivalCity
                                                selectedFlights[3].airplaneModel.value = returnOneStopFlights.value[indexOneStop]!!.secondAirplaneModel
                                                indexOneStop++
                                            }
                                        }
                                        if(button.economyClassClicked.value) {
                                            classTypeInbound.value = "Economy"
                                        }
                                        else if(button.flexClassClicked.value) {
                                            classTypeInbound.value = "Flex"
                                        }
                                        else {
                                            classTypeInbound.value = "Business"
                                        }
                                    }
                                    else {
                                        if(listOfClassButtonsInbound[index].directOrOneStop.intValue == 0) {
                                            indexDirect++
                                        }
                                        else {
                                            indexOneStop++
                                        }
                                    }
                                }
                            }
                            //if the flights are sorted returns them to original view that is from database
                            oneWayDirectFlights.value = oneWayDirectFlightsOriginal.value.toMutableStateList()
                            oneWayOneStopFlights.value = oneWayOneStopFlightsOriginal.value.toMutableStateList()
                            returnDirectFlights.value = returnDirectFlightsOriginal.value.toMutableStateList()
                            returnOneStopFlights.value = returnOneStopFlightsOriginal.value.toMutableStateList()
                            //total price is multiplied by num of passengers
                            totalPrice.doubleValue = passengersCounter.intValue*totalPrice.doubleValue
                            seats.clear()
                            passengers.clear()
                            baggagePerPassenger.clear()
                            //navigates to passenger page
                            navController.navigate(Passengers.route) {
                                popUpTo(Flights.route)
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .padding(start = 45.dp)
                            .width(130.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00B4D8),
                            disabledContentColor = Color(0xFF023E8A)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp
                        ),
                        enabled = (totalPriceOneWay.doubleValue != 0.0 && totalPriceReturn.doubleValue != 0.0 && pagePrevious.intValue == 1)
                                || (pagePrevious.intValue == 0 && totalPriceOneWay.doubleValue != 0.0)
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    //clicking the back icon to go back in the previous page
                    //initializes to the original values of all variables
                    passengersCounter.intValue = 1
                    listOfClassButtonsOutbound.clear()
                    listOfClassButtonsInbound.clear()
                    seats.clear()
                    passengers.clear()
                    baggagePerPassenger.clear()
                    selectedFlights.forEach { flight ->
                        flight.flightId.value = ""
                        flight.departureCity.value = ""
                        flight.arrivalCity.value = ""
                        flight.airplaneModel.value = ""
                    }
                    //navigates back to the book page
                    navController.navigate(Book.route) {
                        popUpTo(Flights.route)
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        Icons.Outlined.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF023E8A)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Flights",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(end = 45.dp),
                        color = Color(0xFF023E8A),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
            Column(modifier = Modifier
                .fillMaxSize()
                .background(gradient)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //if there is no flight show a button that says go back, that the user returns to the book page
                    items(noResults.intValue) {
                        if((oneWayDirectFlights.value.size == 0
                        && oneWayOneStopFlights.value.size == 0
                        && returnDirectFlights.value.size== 0
                        && returnOneStopFlights.value.size == 0)||(
                        (oneWayOneStopFlights.value.size == 0
                         && oneWayDirectFlights.value.size  == 0 &&
                         (returnDirectFlights.value.size !=0
                         || returnOneStopFlights.value.size !=0))||
                         (returnDirectFlights.value.size == 0
                         && returnOneStopFlights.value.size == 0 && pagePrevious.intValue == 1 &&(
                         oneWayOneStopFlights.value.size != 0
                         || oneWayDirectFlights.value.size != 0)
                         )))
                        {
                            seeBottomBar.value = false
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(top = 250.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No Flights Found!",
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color(0xFF023E8A),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 15.dp)
                                )
                                Button(
                                    onClick = {
                                        //clicking the back icon to go back in the previous page
                                        //initializes to the original values of all variables
                                        passengersCounter.intValue = 1
                                        listOfClassButtonsOutbound.clear()
                                        listOfClassButtonsInbound.clear()
                                        seats.clear()
                                        passengers.clear()
                                        baggagePerPassenger.clear()
                                        selectedFlights.forEach { flight ->
                                            flight.flightId.value = ""
                                            flight.departureCity.value = ""
                                            flight.arrivalCity.value = ""
                                            flight.airplaneModel.value = ""
                                        }
                                        navController.navigate(Book.route) {
                                            popUpTo(Flights.route)
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00B4D8),
                                        disabledContentColor = Color(0xFF023E8A)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 5.dp
                                    )
                                ) {
                                    Text("Go Back",
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    //if finally there are flights show them first outbound and specifically direct and after the
                    //flights with one stop and second the inbound if there is with the same pattern
                    if(seeBottomBar.value) {
                        items(oneWayDirectFlights.value.size) { index ->
                            listOfClassButtonsOutbound[index].directOrOneStop.intValue = 0
                            if(index == 0) {
                                //info about the outbound flights and two buttons with the sorting
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Outbound",
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = oneWayDirectFlights.value[0]!!.flightDate,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 35.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = oneWayDirectFlights.value[0]!!.departureCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                    )
                                    Text(
                                        text = oneWayDirectFlights.value[0]!!.arrivalCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = {
                                            sortPrice.value = !sortPrice.value
                                            sortDepartureTime.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortPrice.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortPrice.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,end = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceOneWay.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortPrice.value) "Sorting by price" else "Sorted by price",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            sortDepartureTime.value = !sortDepartureTime.value
                                            sortPrice.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortDepartureTime.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortDepartureTime.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,start = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceOneWay.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortDepartureTime.value) "Sorting by departure time" else "Sorted by departure time",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            //this function show the direct flights in cards
                            ShowDirectFlights(
                                flight = oneWayDirectFlights.value[index],
                                index = index,
                                listOfClassButtons = listOfClassButtonsOutbound,
                                bottomPadding = oneWayOneStopFlights.value.size == 0
                                        && returnDirectFlights.value.size == 0
                                        && index == oneWayDirectFlights.value.size-1,
                                totalPrice = totalPriceOneWay,
                                returnOrNot = false
                            )
                        }
                        items(oneWayOneStopFlights.value.size) { index ->
                            listOfClassButtonsOutbound[index+oneWayDirectFlights.value.size].directOrOneStop.intValue = 1
                            //info about the outbound flights with one stop if there
                            //are not any direct flights and the first flights now are these flights
                            //and two buttons with the sorting
                            if(index == 0 && oneWayDirectFlights.value.size == 0) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Outbound",
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = oneWayOneStopFlights.value[0]!!.firstFlightDate,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 35.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = oneWayOneStopFlights.value[0]!!.firstDepartureCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                    )
                                    Text(
                                        text = oneWayOneStopFlights.value[0]!!.secondArrivalCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = {
                                            sortPrice.value = !sortPrice.value
                                            sortDepartureTime.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortPrice.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortPrice.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,end = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceOneWay.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortPrice.value) "Sorting by price" else "Sorted by price",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            sortDepartureTime.value = !sortDepartureTime.value
                                            sortPrice.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortDepartureTime.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortDepartureTime.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,start = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceOneWay.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortDepartureTime.value) "Sorting by departure time" else "Sorted by departure time",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            //this function show the flights with one stop in cards
                            ShowOneStopFlights(
                                flight = oneWayOneStopFlights.value[index],
                                index = oneWayDirectFlights.value.size + index,
                                topPadding = oneWayDirectFlights.value.size==0,
                                listOfClassButtons = listOfClassButtonsOutbound,
                                bottomPadding = returnDirectFlights.value.size == 0 && returnOneStopFlights.value.size == 0,
                                totalPrice = totalPriceOneWay,
                                returnOrNot = false
                            )

                        }
                        items(returnDirectFlights.value.size) {index ->
                            listOfClassButtonsInbound[index].directOrOneStop.intValue = 0
                            //info about the inbound flights and two buttons with the sorting
                            if(index == 0) {
                                Divider(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 30.dp),
                                    color = Color(0xFF023E8A))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Inbound",
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = returnDirectFlights.value[0]!!.flightDate,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 35.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = returnDirectFlights.value[0]!!.departureCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                    )
                                    Text(
                                        text = returnDirectFlights.value[0]!!.arrivalCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = {
                                            sortPriceReturn.value = !sortPriceReturn.value
                                            sortDepartureTimeReturn.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortPriceReturn.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortPriceReturn.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,end = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceReturn.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortPriceReturn.value) "Sorting by price" else "Sorted by price",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            sortDepartureTimeReturn.value = !sortDepartureTimeReturn.value
                                            sortPriceReturn.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortDepartureTimeReturn.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortDepartureTimeReturn.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,start = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceReturn.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortDepartureTimeReturn.value) "Sorting by departure time" else "Sorted by departure time",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            //this function show the direct flights in cards
                            ShowDirectFlights(
                                flight = returnDirectFlights.value[index],
                                index = index,
                                listOfClassButtons = listOfClassButtonsInbound,
                                bottomPadding = returnOneStopFlights.value.size == 0 && index == returnDirectFlights.value.size-1,
                                totalPrice = totalPriceReturn,
                                returnOrNot = true
                            )
                        }
                        items(returnOneStopFlights.value.size) { index ->
                            listOfClassButtonsInbound[index+returnDirectFlights.value.size].directOrOneStop.intValue = 1
                            //info about the inbound flights with one stop if there
                            //are not any direct flights and the first flights now are these flights
                            //and two buttons with the sorting
                            if(index == 0 && returnDirectFlights.value.size == 0) {
                                Divider(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 30.dp),
                                    color = Color(0xFF023E8A))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Inbound",
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = returnOneStopFlights.value[0]!!.firstFlightDate,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 35.dp, top = 20.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = returnOneStopFlights.value[0]!!.firstDepartureCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                    )
                                    Text(
                                        text = returnOneStopFlights.value[0]!!.secondArrivalCity,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center) {
                                    Button(
                                        onClick = {
                                            sortPriceReturn.value = !sortPriceReturn.value
                                            sortDepartureTimeReturn.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortPriceReturn.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortPriceReturn.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,end = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceReturn.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortPriceReturn.value) "Sorting by price" else "Sorted by price",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            sortDepartureTimeReturn.value = !sortDepartureTimeReturn.value
                                            sortPriceReturn.value = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (sortDepartureTimeReturn.value)
                                                Color(0xFF023E8A)
                                            else
                                                Color.White,
                                            contentColor =
                                            if(sortDepartureTimeReturn.value)
                                                Color.White
                                            else
                                                Color(0xFF023E8A)
                                        ),
                                        border = BorderStroke(width = 1.dp, color = Color(0xFF023E8A)),
                                        modifier = Modifier.padding(top = 10.dp,start = 5.dp),
                                        contentPadding = PaddingValues(10.dp),
                                        enabled = totalPriceReturn.doubleValue == 0.0
                                    ) {
                                        Text(text = if(!sortDepartureTimeReturn.value) "Sorting by departure time" else "Sorted by departure time",
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            //this function show the flights with one stop in cards
                            ShowOneStopFlights(
                                flight = returnOneStopFlights.value[index],
                                index = returnDirectFlights.value.size + index,
                                topPadding = returnDirectFlights.value.size==0,
                                listOfClassButtons = listOfClassButtonsInbound,
                                bottomPadding = index == returnOneStopFlights.value.size - 1,
                                totalPrice = totalPriceReturn,
                                returnOrNot = true
                            )
                        }
                    }


                }
            }
        }
    }
}

//this function helps to decode to hours and minutes the duration of the flights
fun parseTime(input: String): Pair<Int, Int> {
    return when {
        input.length == 2 -> Pair(input[0].digitToInt(),0)
        input.length == 3 -> Pair(input.substring(0, 2).toIntOrNull() ?: 0,0)
        input.length == 4 -> Pair(0, input.substring(0, 2).toIntOrNull() ?: 0)
        input.length == 5 -> Pair(0, input.substring(0, 2).toIntOrNull() ?: 0)
        input.length == 7 -> Pair(input[0].digitToInt(), input.substring(3, 4).toIntOrNull() ?: 0)
        input.length == 8 && input[1]=='h' -> Pair(input[0].digitToInt(), input.substring(3, 5).toIntOrNull() ?: 0)
        input.length == 8 && input[2]=='h' -> Pair(input.substring(0, 2).toIntOrNull() ?: 0, input.substring(4, 5).toIntOrNull() ?: 0)
        input.length == 9 -> Pair(input.substring(0, 2).toIntOrNull() ?: 0, input.substring(4, 6).toIntOrNull() ?: 0)
        else -> Pair(0,0)
    }
}

//this function finds the total hours and minutes for flights with one stop that is the adding
//of the flight duration of the first flight + flight duration of the second flight
//+ (time to depart the second flight - time to arrive the first flight
@RequiresApi(Build.VERSION_CODES.O)
fun findTotalHoursMinutes(flight: OneStopFlight?): Pair<Int,Int> {
    val time1 = flight!!.firstArrivalTime
    val time2 = flight.secondDepartureTime

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val localTime1 = LocalTime.parse(time1, formatter)
    val localTime2 = LocalTime.parse(time2, formatter)
    val (hour3, minutes3) = parseTime(flight.firstFlightDuration)
    val (hour4, minutes4) = parseTime(flight.secondFlightDuration)

    var hours = localTime2.hour - localTime1.hour + hour3 + hour4
    var minutes: Int
    if(localTime2.minute == 0 && localTime1.minute !=0 ) {
        minutes = 60 - localTime1.minute + minutes3 + minutes4
        hours -= 1
    }
    else {
        minutes = localTime2.minute - localTime1.minute + minutes3 + minutes4
    }
    if(minutes < 0 ) {
        minutes = -minutes
        hours -= 1
    }
    val totalHours = hours + (minutes / 60)
    val totalMinutes = minutes % 60
    return Pair(totalHours,totalMinutes)
}

//function that shows the one stop flights in cards with their info
//flight variable is one stop flight that is type of the data class OneStopFlight,
//total price if some flight has selected,
//listOfClassButtons is the list of buttons boolean type for all buttons in a flight (economy,flex,business)
//type of ReservationType data class
//returnOrNot shows if the function is called for outbound or inbound flight
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun <T: OneStopFlight?> ShowOneStopFlights(flight: T,
                                           index: Int,
                                           topPadding: Boolean,
                                           listOfClassButtons: MutableList<ReservationType>,
                                           bottomPadding: Boolean,
                                           totalPrice: MutableDoubleState,
                                           returnOrNot: Boolean
) {
    if (flight != null) {
        val (totalHours, totalMinutes) = findTotalHoursMinutes(flight)
        val showDialog = remember {
            mutableStateOf(false)
        }

        //alert dialog for the flight details with more info about the flight
        if(showDialog.value) {
            Box(contentAlignment = Alignment.Center) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = {
                        Text("Flight Details")
                    },
                    text = {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())) {
                            Text(
                                if(returnOrNot) "Inbound" else "Outbound",
                                fontSize = 20.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                Text("Total time: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = "${
                                    if(totalHours!=0&&totalMinutes!=0)
                                        "${totalHours}h ${totalMinutes}min"
                                    else if(totalHours!=0)
                                        "${totalHours}h"
                                    else
                                        "${totalMinutes}min"}, 1 stop",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),thickness = 1.dp, color = Color(0xFF023E8A))
                            Text(
                                "1st Flight",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Row {
                                Text("Flight date: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    flight.firstFlightDate,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            flight.firstDepartureTime,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 20.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${flight.firstDepartureCity} (${flight.firstDepartureAirport})",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.padding(start = 10.dp)) {
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                        }
                                        Row(modifier = Modifier.padding(start = 30.dp)) {
                                            Text(
                                                text = "Flight duration: ",
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp)
                                            )
                                            Text(
                                                text = flight.firstFlightDuration,
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            flight.firstArrivalTime,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, end = 20.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${flight.firstArrivalCity} (${flight.firstArrivalAirport})",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Row {
                                Text(
                                    text = "Flight number: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = flight.firstFlightId,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row {
                                Text(
                                    text = "Airplane model: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = flight.firstAirplaneModel,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),thickness = 1.dp, color = Color(0xFF023E8A))
                            Text(
                                "2nd Flight",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Row {
                                Text("Flight date: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    flight.secondFlightDate,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            flight.secondDepartureTime,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 20.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${flight.secondDepartureCity} (${flight.secondDepartureAirport})",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.padding(start = 10.dp)) {
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                        }
                                        Row(modifier = Modifier.padding(start = 30.dp)) {
                                            Text(
                                                text = "Flight duration: ",
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp)
                                            )
                                            Text(
                                                text = flight.secondFlightDuration,
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            flight.secondArrivalTime,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, end = 20.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${flight.secondArrivalCity} (${flight.secondArrivalAirport})",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Row {
                                Text(
                                    text = "Flight number: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = flight.secondFlightId,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row {
                                Text(
                                    text = "Airplane model: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = flight.secondAirplaneModel,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF023E8A)
                            ),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text("OK",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    },
                    containerColor = Color(0xFFEBF2FA),
                    textContentColor = Color(0xFF023E8A),
                    titleContentColor = Color(0xFF023E8A),
                    tonalElevation = 30.dp,
                    modifier = Modifier
                        .height(400.dp)
                        .width(400.dp),
                    properties = DialogProperties(dismissOnClickOutside = true)
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(
                    top = if (topPadding)
                        20.dp else 30.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = if (bottomPadding) 80.dp else 0.dp
                )
                .width(350.dp)
                .height(285.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = ShapeDefaults.Small,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .height(150.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = flight.firstFlightId,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF029fff),
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Icon(
                        Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Text(
                        text = flight.secondFlightId,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF029fff),
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = flight.firstDepartureTime,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 10.dp, top = 18.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = flight.firstDepartureAirport,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 15.dp, top = 10.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text =
                            if(totalHours!=0&&totalMinutes!=0)
                                "${totalHours}h ${totalMinutes}min"
                            else if(totalHours!=0)
                                "${totalHours}h"
                            else
                                "${totalMinutes}min",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Box(contentAlignment = Alignment.Center)
                        {
                            Divider(
                                modifier = Modifier.width(180.dp),
                                color = Color(0xFF023E8A),
                                thickness = 0.5.dp
                            )
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF0070f0)
                                ),
                                shape = ShapeDefaults.ExtraLarge,
                                border = BorderStroke(width = 0.5.dp, color = Color(0xFF023E8A)),
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(20.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "1 stop",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { showDialog.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Text(
                                text = "Flight details",
                                fontSize = 12.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                color = Color(0xFF028FFF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = flight.secondArrivalTime,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(end = 10.dp, top = 18.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = flight.secondArrivalAirport,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(end = 15.dp, top = 10.dp)
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp),
                        color = Color(0xFF023E8A),
                        thickness = 1.dp
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Economy",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF00b4d8)
                        )
                        Text(
                            text = "${flight.firstEconomyPrice+flight.secondEconomyPrice} €",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF00b4d8)
                        )
                        Button(
                            onClick = {
                                listOfClassButtons.forEach { item ->
                                    if (item.economyClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.flexClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.businessClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    }
                                }
                                listOfClassButtons[index].economyClassClicked.value =
                                    !listOfClassButtons[index].economyClassClicked.value
                                if(listOfClassButtons[index].economyClassClicked.value) {
                                    totalPrice.doubleValue = flight.firstEconomyPrice+flight.secondEconomyPrice
                                }
                                listOfClassButtons.forEachIndexed { indexClass, item ->
                                    if(indexClass!=index) {
                                        item.economyClassClicked.value = false
                                    }
                                    item.flexClassClicked.value = false
                                    item.businessClassClicked.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00b4d8)
                            ),
                            contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                            modifier = Modifier.height(25.dp)
                        ) {
                            Text(
                                text = if(listOfClassButtons[index].economyClassClicked.value) "Selected" else "Select",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp),
                        color = Color(0xFF023E8A),
                        thickness = 1.dp
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Flex",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0096c7)
                        )
                        Text(
                            text = "${flight.firstFlexPrice+flight.secondFlexPrice} €",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0096c7),
                            modifier = Modifier.padding(start = 40.dp)
                        )
                        Button(
                            onClick = {
                                listOfClassButtons.forEach { item ->
                                    if (item.economyClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.flexClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.businessClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    }
                                }
                                listOfClassButtons[index].flexClassClicked.value =
                                    !listOfClassButtons[index].flexClassClicked.value
                                if(listOfClassButtons[index].flexClassClicked.value) {
                                    totalPrice.doubleValue = flight.firstFlexPrice+flight.secondFlexPrice
                                }
                                listOfClassButtons.forEachIndexed { indexClass, item ->
                                    if(indexClass!=index) {
                                        item.flexClassClicked.value = false
                                    }
                                    item.economyClassClicked.value = false
                                    item.businessClassClicked.value = false
                                } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0096c7)
                            ),
                            contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                            modifier = Modifier.height(25.dp)
                        ) {
                            Text(
                                text = if(listOfClassButtons[index].flexClassClicked.value) "Selected" else "Select",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp),
                        color = Color(0xFF023E8A),
                        thickness = 1.dp
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Business",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0077b6)
                        )
                        Text(
                            text = "${flight.firstBusinessPrice+flight.secondBusinessPrice} €",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0077b6),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        Button(
                            onClick = {
                                listOfClassButtons.forEach { item ->
                                    if (item.economyClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.flexClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.businessClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    }
                                }
                                listOfClassButtons[index].businessClassClicked.value =
                                    !listOfClassButtons[index].businessClassClicked.value
                                if(listOfClassButtons[index].businessClassClicked.value) {
                                    totalPrice.doubleValue = flight.firstBusinessPrice+flight.secondBusinessPrice
                                }
                                listOfClassButtons.forEachIndexed { indexClass, item ->
                                    if(indexClass!=index) {
                                        item.businessClassClicked.value = false
                                    }
                                    item.economyClassClicked.value = false
                                    item.flexClassClicked.value = false
                                }},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0077b6)
                            ),
                            contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                            modifier = Modifier.height(25.dp)
                        ) {
                            Text(
                                text = if(listOfClassButtons[index].businessClassClicked.value) "Selected" else "Select",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

//function that shows the direct flights in cards with their info
//flight variable is direct flight that is type of the data class DirectFlight,
//total price if some flight has selected,
//listOfClassButtons is the list of buttons boolean type for all buttons in a flight (economy,flex,business)
//type of ReservationType data class,
//returnOrNot shows if the function is called for outbound or inbound flight
@Composable
fun <T: DirectFlight?> ShowDirectFlights(flight: T,
                                         index: Int,
                                         listOfClassButtons: MutableList<ReservationType>,
                                         bottomPadding: Boolean,
                                         totalPrice: MutableDoubleState,
                                         returnOrNot: Boolean
) {
    if (flight != null) {
        val showDialog = remember {
            mutableStateOf(false)
        }

        //alert dialog for the flight details with more info about the flight
        if(showDialog.value) {
            Box(contentAlignment = Alignment.Center) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = {
                        Text("Flight Details")
                    },
                    text = {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())) {
                            Text(
                                if(returnOrNot) "Inbound" else "Outbound",
                                fontSize = 20.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                Text("Total time: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = "${flight.flightDuration}, nonstop",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),thickness = 1.dp, color = Color(0xFF023E8A))
                            Row {
                                Text("Flight date: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    flight.flightDate,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            flight.departureTime,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 20.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${flight.departureCity} (${flight.departureAirport})",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.padding(start = 10.dp)) {
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                            Icon(
                                                Icons.Outlined.ArrowDownward,
                                                contentDescription = null,
                                                tint = Color(0xFF023E8A)
                                            )
                                        }
                                        Row(modifier = Modifier.padding(start = 30.dp)) {
                                            Text(
                                                text = "Flight duration: ",
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp)
                                            )
                                            Text(
                                                text = flight.flightDuration,
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            flight.arrivalTime,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, end = 20.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${flight.arrivalCity} (${flight.arrivalAirport})",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.opensans
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Row {
                                Text(
                                    text = "Flight number: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = flight.flightId,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row {
                                Text(
                                    text = "Airplane model: ",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = flight.airplaneModel,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF023E8A)
                            ),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text("OK",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    },
                    containerColor = Color(0xFFEBF2FA),
                    textContentColor = Color(0xFF023E8A),
                    titleContentColor = Color(0xFF023E8A),
                    tonalElevation = 30.dp,
                    modifier = Modifier.height(400.dp),
                    properties = DialogProperties(dismissOnClickOutside = true)
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(
                    top = if (index == 0)
                        20.dp else 30.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = if (bottomPadding) 80.dp else 0.dp
                )
                .width(350.dp)
                .height(285.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = ShapeDefaults.Small,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .height(150.dp)
            ) {
                Text(
                    text = flight.flightId,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF029fff),
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = flight.departureTime,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 10.dp, top = 18.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = flight.departureAirport,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 15.dp, top = 10.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = flight.flightDuration,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Box(contentAlignment = Alignment.Center)
                        {
                            Divider(
                                modifier = Modifier.width(180.dp),
                                color = Color(0xFF023E8A),
                                thickness = 0.5.dp
                            )
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF029fff)
                                ),
                                shape = ShapeDefaults.ExtraLarge,
                                border = BorderStroke(width = 0.5.dp, color = Color(0xFF023E8A)),
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(20.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "nonstop",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { showDialog.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            Text(
                                text = "Flight details",
                                fontSize = 12.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                color = Color(0xFF028FFF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = flight.arrivalTime,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(end = 10.dp, top = 18.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = flight.arrivalAirport,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(end = 15.dp, top = 10.dp)
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp),
                        color = Color(0xFF023E8A),
                        thickness = 1.dp
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Economy",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF00b4d8)
                        )
                        Text(
                            text = "${flight.economyPrice} €",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF00b4d8)
                        )
                        Button(
                            onClick = {
                                listOfClassButtons.forEach { item ->
                                    if (item.economyClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.flexClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.businessClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    }
                                }
                                listOfClassButtons[index].economyClassClicked.value =
                                    !listOfClassButtons[index].economyClassClicked.value
                                if(listOfClassButtons[index].economyClassClicked.value) {
                                    totalPrice.doubleValue = flight.economyPrice
                                }
                                listOfClassButtons.forEachIndexed { indexClass, item ->
                                    if(indexClass!=index) {
                                        item.economyClassClicked.value = false
                                    }
                                    item.flexClassClicked.value = false
                                    item.businessClassClicked.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00b4d8)
                            ),
                            contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                            modifier = Modifier.height(25.dp)
                        ) {
                            Log.d("VASILIS1", returnOrNot.toString())
                            Text(
                                text = if(listOfClassButtons[index].economyClassClicked.value) "Selected" else "Select",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp),
                        color = Color(0xFF023E8A),
                        thickness = 1.dp
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Flex",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0096c7)
                        )
                        Text(
                            text = "${flight.flexPrice} €",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0096c7),
                            modifier = Modifier.padding(start = 40.dp)
                        )
                        Button(
                            onClick = {
                                listOfClassButtons.forEach { item ->
                                    if (item.economyClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.flexClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.businessClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    }
                                }
                                listOfClassButtons[index].flexClassClicked.value =
                                    !listOfClassButtons[index].flexClassClicked.value
                                if(listOfClassButtons[index].flexClassClicked.value) {
                                    totalPrice.doubleValue = flight.flexPrice
                                }
                                listOfClassButtons.forEachIndexed { indexClass, item ->
                                    if(indexClass!=index) {
                                        item.flexClassClicked.value = false
                                    }
                                    item.economyClassClicked.value = false
                                    item.businessClassClicked.value = false
                                } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0096c7)
                            ),
                            contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                            modifier = Modifier.height(25.dp)
                        ) {
                            Text(
                                text = if(listOfClassButtons[index].flexClassClicked.value) "Selected" else "Select",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp),
                        color = Color(0xFF023E8A),
                        thickness = 1.dp
                    )
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Business",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0077b6)
                        )
                        Text(
                            text = "${flight.businessPrice} €",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF0077b6),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        Button(
                            onClick = {
                                listOfClassButtons.forEach { item ->
                                    if (item.economyClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.flexClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    } else if (item.businessClassClicked.value) {
                                        totalPrice.doubleValue = 0.0
                                    }
                                }
                                listOfClassButtons[index].businessClassClicked.value =
                                    !listOfClassButtons[index].businessClassClicked.value
                                if(listOfClassButtons[index].businessClassClicked.value) {
                                    totalPrice.doubleValue = flight.businessPrice
                                }
                                listOfClassButtons.forEachIndexed { indexClass, item ->
                                    if(indexClass!=index) {
                                        item.businessClassClicked.value = false
                                    }
                                    item.economyClassClicked.value = false
                                    item.flexClassClicked.value = false
                                }},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0077b6)
                            ),
                            contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                            modifier = Modifier.height(25.dp)
                        ) {
                            Text(
                                text = if(listOfClassButtons[index].businessClassClicked.value) "Selected" else "Select",
                                fontSize = 14.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}