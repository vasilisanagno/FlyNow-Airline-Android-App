package com.example.flynow

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

//navController helps to navigate to previous page or next page,
//passengersCount is the number of passengers in the booking,
//pagePrevious show if the search begins from one way flights or round trip flights,
//passengers, seats, selectedFlights are the list that
//is saved the information about passengers,seats and selected flights
//total price is from previous page that is flights screen so it is the price for the flight that is selected,
//prevTotalPrice is variable that is useful for the back button of the baggage and pets screen
//the two variables with class buttons contains all the buttons of the outbound and inbound flights,
//bookingFailed variable is if some seat is taken from someone else during the completion of the reservation
//this variable becomes true and send the user back to seats screen to select again seats for the passengers
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SeatsScreen(navController: NavController,
                totalPrice: MutableDoubleState,
                passengersCount: MutableIntState,
                passengers: MutableList<PassengerInfo>,
                seats: MutableList<MutableList<MutableState<String>>>,
                pagePrevious: MutableIntState,
                selectedFlights: MutableList<SelectedFlightDetails>,
                selectedFlightOutbound: MutableIntState,
                selectedFlightInbound: MutableIntState,
                prevTotalPrice: MutableDoubleState,
                bookingFailed: MutableState<Boolean>
) {
    val numOfPassengers = passengersCount.intValue
    if(seats.size==0) {
        if (pagePrevious.intValue == 0) {
            for (i in 0 until numOfPassengers) {
                val innerList: MutableList<MutableState<String>> = mutableListOf()
                for (j in 0 until 2) {
                    val seat: MutableState<String> = remember {
                        mutableStateOf("")
                    }
                    innerList.add(seat)
                }
                seats.add(innerList)
            }
        } else {
            for (i in 0 until 2 * numOfPassengers) {
                val innerList: MutableList<MutableState<String>> = mutableListOf()
                for (j in 0 until 2) {
                    val seat: MutableState<String> = remember {
                        mutableStateOf("")
                    }
                    innerList.add(seat)
                }
                seats.add(innerList)
            }
        }
    }
    val checkForEmptySeats = remember {
        mutableStateOf(false)
    }
    val buttonClicked = remember {
        mutableStateOf(false)
    }

    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )


    val isClickedSeat: MutableList<MutableList<MutableState<Boolean>>> = remember {
        MutableList(4) {mutableListOf()}
    }
    val seatListOutboundDirect: MutableList<String> = mutableListOf()
    val seatListInboundDirect: MutableList<String> = mutableListOf()
    val seatListOutboundOneStop: MutableList<MutableList<String>> = MutableList(2) {mutableListOf()}
    val seatListInboundOneStop: MutableList<MutableList<String>> = MutableList(2) {mutableListOf()}

    //creates the seat lists of the passenger for each different flight even the one stop flights
    if(selectedFlightOutbound.intValue == 0) {
        GenerateSeatList(
            flightId = selectedFlights[0].flightId, seatList = seatListOutboundDirect, airplaneModel = selectedFlights[0].airplaneModel, isClickedSeat = isClickedSeat[0])
    }
    else {
        GenerateSeatList(flightId = selectedFlights[0].flightId, seatList = seatListOutboundOneStop[0], airplaneModel = selectedFlights[0].airplaneModel, isClickedSeat = isClickedSeat[0])
        GenerateSeatList(flightId = selectedFlights[1].flightId, seatList = seatListOutboundOneStop[1], airplaneModel = selectedFlights[1].airplaneModel, isClickedSeat = isClickedSeat[1])
    }
    if (pagePrevious.intValue == 1) {
        if(selectedFlightOutbound.intValue == 0 && selectedFlightInbound.intValue == 0) {
            GenerateSeatList(flightId = selectedFlights[1].flightId, seatList = seatListInboundDirect, airplaneModel = selectedFlights[1].airplaneModel, isClickedSeat = isClickedSeat[1])
        }
        else if(selectedFlightOutbound.intValue == 1 && selectedFlightInbound.intValue == 0) {
            GenerateSeatList(flightId = selectedFlights[2].flightId, seatList = seatListInboundDirect, airplaneModel = selectedFlights[2].airplaneModel, isClickedSeat = isClickedSeat[2])
        }
        else if(selectedFlightOutbound.intValue == 0 && selectedFlightInbound.intValue == 1) {
            GenerateSeatList(flightId = selectedFlights[1].flightId, seatList = seatListInboundOneStop[0], airplaneModel = selectedFlights[1].airplaneModel, isClickedSeat = isClickedSeat[1])
            GenerateSeatList(flightId = selectedFlights[2].flightId, seatList = seatListInboundOneStop[1], airplaneModel = selectedFlights[2].airplaneModel, isClickedSeat = isClickedSeat[2])
        }
        else {
            GenerateSeatList(flightId = selectedFlights[2].flightId, seatList = seatListInboundOneStop[0], airplaneModel = selectedFlights[2].airplaneModel, isClickedSeat = isClickedSeat[2])
            GenerateSeatList(flightId = selectedFlights[3].flightId, seatList = seatListInboundOneStop[1], airplaneModel = selectedFlights[3].airplaneModel, isClickedSeat = isClickedSeat[3])
        }
    }

    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
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
                    "Total Price: ${totalPrice.doubleValue} €",
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
                        buttonClicked.value = true
                        prevTotalPrice.doubleValue = totalPrice.doubleValue
                        checkForEmptySeats.value = false
                        var passenger = 0
                        seats.forEach { seat->
                            var counter = 0
                            if(passenger < numOfPassengers) {
                                if(selectedFlightOutbound.intValue == 0) {
                                    seat.forEach {seatPerFlight ->
                                        if(seatPerFlight.value == "" && counter == 0) {
                                            checkForEmptySeats.value = true
                                        }
                                        counter++
                                    }
                                }
                                else {
                                    seat.forEach {seatPerFlight ->
                                        if(seatPerFlight.value == "") {
                                            checkForEmptySeats.value = true
                                        }
                                    }
                                }
                            }
                            counter = 0
                            if(passenger >= numOfPassengers) {
                                if(selectedFlightInbound.intValue == 0) {
                                    seat.forEach {seatPerFlight ->
                                        if(seatPerFlight.value == "" && counter == 0) {
                                            checkForEmptySeats.value = true
                                        }
                                        counter++
                                    }
                                }
                                else {
                                    seat.forEach {seatPerFlight ->
                                        if(seatPerFlight.value == "") {
                                            checkForEmptySeats.value = true
                                        }
                                    }
                                }
                            }
                            passenger++
                        }
                        if(!checkForEmptySeats.value) {
                            navController.navigate(BaggageAndPets.route) {
                                popUpTo(Seats.route)
                                launchSingleTop = true
                            }
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
                    )
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
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //"Back" button
                IconButton(onClick = {
                    seats.clear()
                    bookingFailed.value = false
                    navController.navigate(Passengers.route) {
                        popUpTo(Seats.route)
                        launchSingleTop = true
                    }
                }
                ) {
                    Icon(
                        Icons.Outlined.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF023E8A)
                    )
                }
                //"Passengers" text field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Seats",
                        fontSize = 22.sp,
                        modifier = Modifier,
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
                    Icon(
                        Icons.Filled.AirlineSeatReclineNormal,
                        contentDescription = "seat",
                        modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 5.dp),
                        tint = Color(0xFF023E8A)
                    )
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
            ) {
                LazyColumn {
                    items(numOfPassengers) { index ->
                        if (index == 0) {
                            Image(
                                painter = painterResource(id = R.drawable.seats),
                                contentDescription = "airplaneseat",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(3f)
                                    .padding(start = 10.dp, end = 10.dp)
                            )
                            Text(
                                "Select a seat from 1x to 30x where x: A,B,C,D,E,F. " +
                                        "There is no charge for seat selection.",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 1.dp),
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                color = Color(0xFF023E8A)
                            )
                            if(bookingFailed.value) {
                                Text(
                                    "Your reservation failed because some of the seats you selected are already taken!",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 10.dp, top = 1.dp),
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color.Red
                                )
                            }
                            Text(
                                "Outbound",
                                fontSize = 22.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
                        Text(
                            text =
                            if (passengers[index].gender.value == "Female")
                                "Mrs ${passengers[index].firstname.value} ${passengers[index].lastname.value}"
                            else
                                "Mr ${passengers[index].firstname.value} ${passengers[index].lastname.value}",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(
                                start = 10.dp,
                                top = if (index == 0) 10.dp else 5.dp
                            ),
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
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically) {
                            if(selectedFlightOutbound.intValue == 0) {
                                SeatsDropdownMenu(
                                    seats = seatListOutboundDirect,
                                    buttonClicked = buttonClicked,
                                    passengersSeats = seats,
                                    index = index,
                                    column = 0,
                                    isClickedSeat = isClickedSeat[0],
                                    bookingFailed = bookingFailed
                                )
                            }
                            else {
                                SeatsDropdownMenu(
                                    seats = seatListOutboundOneStop[0],
                                    buttonClicked = buttonClicked,
                                    passengersSeats = seats,
                                    index = index,
                                    column = 0,
                                    isClickedSeat = isClickedSeat[0],
                                    bookingFailed = bookingFailed
                                )
                            }
                            Text(
                                text = selectedFlights[0].departureCity.value,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                color = Color(0xFF023E8A),
                                modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF023E8A),
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                text = selectedFlights[0].arrivalCity.value,
                                fontSize = 16.sp,
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
                        if(selectedFlightOutbound.intValue == 1) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                SeatsDropdownMenu(
                                    seats = seatListOutboundOneStop[1],
                                    buttonClicked = buttonClicked,
                                    passengersSeats = seats,
                                    index = index,
                                    column = 1,
                                    isClickedSeat = isClickedSeat[1],
                                    bookingFailed = bookingFailed
                                )
                                Text(
                                    text = selectedFlights[1].departureCity.value,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color(0xFF023E8A),
                                    modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Outlined.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF023E8A),
                                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                )
                                Text(
                                    text = selectedFlights[1].arrivalCity.value,
                                    fontSize = 16.sp,
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
                        }
                        if (index < numOfPassengers - 1 || pagePrevious.intValue == 1) {
                            if(pagePrevious.intValue == 1 && index==numOfPassengers-1) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 30.dp, bottom = 10.dp),
                                    thickness = 2.dp,
                                    color = Color(0xFF023E8A)
                                )
                            }
                            else {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, bottom = 10.dp),
                                    thickness = 1.dp,
                                    color = Color(0xFF023E8A)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.padding(bottom = 80.dp))
                        }
                    }
                    if(pagePrevious.intValue == 1) {
                        items(numOfPassengers) { index ->
                            val indexNew: Int = index + numOfPassengers
                            if (index == 0) {
                                Text(
                                    "Inbound",
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
                            Text(
                                text =
                                if (passengers[index].gender.value == "Female")
                                    "Mrs ${passengers[index].firstname.value} ${passengers[index].lastname.value}"
                                else
                                    "Mr ${passengers[index].firstname.value} ${passengers[index].lastname.value}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    top = if (index == 0) 10.dp else 5.dp
                                ),
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
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                if(selectedFlightInbound.intValue == 0) {
                                    SeatsDropdownMenu(
                                        seats = seatListInboundDirect,
                                        buttonClicked = buttonClicked,
                                        passengersSeats = seats,
                                        index = indexNew,
                                        column = 0,
                                        isClickedSeat =
                                        if(selectedFlightOutbound.intValue == 0)
                                            isClickedSeat[1]
                                        else
                                            isClickedSeat[2],
                                        bookingFailed = bookingFailed
                                    )
                                }
                                else {
                                    SeatsDropdownMenu(
                                        seats = seatListInboundOneStop[0],
                                        buttonClicked = buttonClicked,
                                        passengersSeats = seats,
                                        index = indexNew,
                                        column = 0,
                                        isClickedSeat =
                                        if(selectedFlightOutbound.intValue == 0)
                                            isClickedSeat[1]
                                        else
                                            isClickedSeat[2],
                                        bookingFailed = bookingFailed
                                    )
                                }
                                Text(
                                    text =
                                    if(selectedFlightOutbound.intValue == 0)
                                        selectedFlights[1].departureCity.value
                                    else
                                        selectedFlights[2].departureCity.value,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color(0xFF023E8A),
                                    modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Outlined.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF023E8A),
                                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                                )
                                Text(
                                    text =
                                    if(selectedFlightOutbound.intValue == 0)
                                        selectedFlights[1].arrivalCity.value
                                    else
                                        selectedFlights[2].arrivalCity.value,
                                    fontSize = 16.sp,
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
                            if(selectedFlightInbound.intValue == 1) {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    SeatsDropdownMenu(
                                        seats = seatListInboundOneStop[1],
                                        buttonClicked = buttonClicked,
                                        passengersSeats = seats,
                                        index = indexNew,
                                        column = 1,
                                        isClickedSeat =
                                        if (selectedFlightOutbound.intValue == 0)
                                            isClickedSeat[2]
                                        else
                                            isClickedSeat[3],
                                        bookingFailed = bookingFailed
                                    )
                                    Text(
                                        text =
                                        if (selectedFlightOutbound.intValue == 0)
                                            selectedFlights[2].departureCity.value
                                        else
                                            selectedFlights[3].departureCity.value,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(
                                            start = 5.dp,
                                            top = 10.dp
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF023E8A),
                                        modifier = Modifier.padding(
                                            start = 10.dp,
                                            top = 10.dp
                                        )
                                    )
                                    Text(
                                        text =
                                        if (selectedFlightOutbound.intValue == 0)
                                            selectedFlights[2].arrivalCity.value
                                        else
                                            selectedFlights[3].arrivalCity.value,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(
                                            start = 10.dp,
                                            top = 10.dp
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (index < numOfPassengers - 1) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, bottom = 10.dp),
                                    thickness = 1.dp,
                                    color = Color(0xFF023E8A)
                                )
                            } else {
                                Spacer(modifier = Modifier.padding(bottom = 80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

//dropdown menu for seats
//passengersSeats is the list that stores the selected seats of the passengers
//isClickedSeat is list that that shows in the same flight what seats are selected from the passenger
//and make them not enabled adn booking failed is for the completion of the reservation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatsDropdownMenu(seats: MutableList<String>,
                      buttonClicked: MutableState<Boolean>,
                      passengersSeats: MutableList<MutableList<MutableState<String>>>,
                      index: Int,
                      column: Int,
                      isClickedSeat: MutableList<MutableState<Boolean>>,
                      bookingFailed: MutableState<Boolean>
) {
    val isExpanded = remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = { newValue ->
            isExpanded.value = newValue
        },
        modifier = Modifier
            .width(140.dp)
            .padding(top = 5.dp, start = 10.dp, end = 2.dp)
    ) {
        OutlinedTextField(
            value = if(passengersSeats.size == 0) "" else passengersSeats[index][column].value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            label = {
                Text(
                    fontSize = 16.sp,
                    text = "Seat"
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color(0xFF023E8A),
                focusedBorderColor = Color(0xFF023E8A),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFF00B4D8),
                errorContainerColor = Color.White
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            isError = (passengersSeats.size!=0 && passengersSeats[index][column].value == "" && buttonClicked.value)
        )
        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = {
                isExpanded.value = false
            },
            modifier = Modifier
                .width(120.dp)
                .background(color = Color.White)
        ) {
            for (i in 0 until seats.size) {
                DropdownMenuItem(
                    text = {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = seats[i],
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
                    onClick = {
                        for(j in 0 until seats.size) {
                            if(seats[j]==passengersSeats[index][column].value) {
                                isClickedSeat[j].value = false
                            }
                        }
                        isExpanded.value = false
                        isClickedSeat[i].value = true
                        bookingFailed.value = false
                        passengersSeats.forEachIndexed { index1,seat ->
                            if(index1==index) {
                                seat.forEachIndexed { index2,seat1 ->
                                    if(index2==column) {
                                        seat1.value = seats[i]
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.background(color = Color.White),
                    enabled = !isClickedSeat[i].value
                )
            }
        }
    }
}

//function that generates the seat list accordingly the capacity of the airplane of a flight
//removing the seats that is already taken for other passengers
@Composable
fun GenerateSeatList(flightId: MutableState<String>,
                     seatList: MutableList<String>,
                     airplaneModel: MutableState<String>,
                     isClickedSeat: MutableList<MutableState<Boolean>>) {
    val ctx = LocalContext.current
    val bookedSeats = remember {
        mutableListOf<String>()
    }
    val capacity = remember {
        mutableIntStateOf(0)
    }

    //api that takes the capacity of the airplane and creates the list of seats
    LaunchedEffect(Unit) {
        val url = "http://100.106.205.30:5000/flynow/airplane-capacity"
        // on below line we are creating a variable for
        // our request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(ctx)
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("airplaneModel", airplaneModel.value)
        jsonArray.put(jsonObject)

        val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
            try {
                capacity.intValue = response.getJSONObject(0).getInt("capacity")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Log.d("Error", error.toString())
            Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                .show()
        })
        queue.add(request)
        delay(500)
    }
    var index = 0
    for (row in 1..capacity.intValue/6) {
        for (seatChar in 'A'..'F') {
            val seat = "$row$seatChar"
            seatList.add("")
            seatList[index] = seat
            index++
        }
    }
    repeat(capacity.intValue) {
        isClickedSeat.add(mutableStateOf(false))
    }

    //api that takes the not available seats and store them in a list
    LaunchedEffect(Unit) {
        val url = "http://100.106.205.30:5000/flynow/seats"
        // on below line we are creating a variable for
        // our request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(ctx)
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("flightId", flightId.value)
        jsonArray.put(jsonObject)

        val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
            try {
                if (response != null) {
                    for (i in 0 until response.length()) {
                        bookedSeats.add(response.getJSONObject(i).getString("seatnumber"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }, { error ->
            Log.d("Error", error.toString())
            Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                .show()
        })
        queue.add(request)
        delay(1000)
    }
    //removes the not available seats from the capacity of the airplane to remain finally
    //only the available seats inside the airplane of a flight
    seatList.removeAll { seat ->
        seat in bookedSeats
    }
}
