package com.example.flynow

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

//screen that shows the check in and the parameters are:
//navController to navigate backward or forward to other pages,
//selectedIndex helps if there is the bottom bar in previous page,
//bookingIdCheckIn is the booking reference of the reservation,
//directFlight if true the flight is direct else the flight has one stop,
//flightsCheckIn is list with the flights available to check in,
//passengersCheckIn is list with the passengers of the flights,
//numOfPassengersCheckIn is the number of passengers in each flight,
//petSizeCheckIn is the size of the pet,
//wifiOnBoardCheckIn is the type of wifi selected if exists,
//baggageAndSeatCheckIn is list with the baggage and seats of each passenger in each flight
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckInDetailsScreen(navController: NavController,
                         selectedIndex: MutableIntState,
                         bookingIdCheckIn: MutableState<String>,
                         directFlight: MutableState<Boolean>,
                         flightsCheckIn: MutableState<SnapshotStateList<BasicFlight?>>,
                         passengersCheckIn: MutableState<SnapshotStateList<PassengerInfo?>>,
                         numOfPassengersCheckIn: MutableIntState,
                         petSizeCheckIn: MutableState<String>,
                         wifiOnBoardCheckIn: MutableIntState,
                         baggageAndSeatCheckIn: MutableState<SnapshotStateList<BaggageAndSeatPerPassenger?>>
) {
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val backButton = remember {
        mutableStateOf(false)
    }
    val dateInNums = remember {
        mutableStateOf("")
    }
    val dateInWords = remember {
        mutableStateOf("")
    }
    val flightIndex = remember {
        mutableIntStateOf(0)
    }
    val flightDuration = remember {
        mutableStateOf("")
    }
    //variables for showing dialog for more details for flights and passengers
    val showDialogFlights = remember {
        mutableStateOf(false)
    }
    val showDialogPassenger = remember {
        mutableStateOf(false)
    }
    //variable for showing dialog to confirm check-in
    val showDialogCheckIn = remember {
        mutableStateOf(false)
    }
    //list that keeps the checked passengers so as to the check-in button be enabled
    //when all passengers are checked
    val checkedState = remember {
        mutableStateListOf<MutableState<Boolean>>().apply {
            repeat(numOfPassengersCheckIn.intValue) {
                add(mutableStateOf(false))
            }
        }
    }
    //variable to start updating the database with the check-in
    val updateCheckIn = remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //"Back" button
            IconButton(onClick = {
                backButton.value = true
                bookingIdCheckIn.value = ""
                directFlight.value = false
                flightsCheckIn.value.clear()
                passengersCheckIn.value.clear()
                numOfPassengersCheckIn.intValue = 0
                petSizeCheckIn.value = ""
                wifiOnBoardCheckIn.intValue = -1
                baggageAndSeatCheckIn.value.clear()
                navController.navigate(CheckIn.route) {
                    popUpTo(CheckInDetails.route)
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
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Check-In",
                    fontSize = 22.sp,
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
                    Icons.Filled.FactCheck,
                    contentDescription = "checkin",
                    tint = Color(0xFF023E8A),
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

        if (!backButton.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkin),
                    contentDescription = "checkin",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f)
                        .padding(start = 10.dp, end = 10.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Filled.AirplaneTicket,
                        contentDescription = "bookingId",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 13.dp)
                    )
                    Text(
                        text = "Booking Reference: ",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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
                    Text(
                        text = bookingIdCheckIn.value.uppercase(Locale.getDefault()),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 13.5.dp),
                        color = Color(0xFF0077FF),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            Icons.Filled.AirplanemodeActive,
                            contentDescription = "airplane",
                            tint = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 10.dp, top = 12.dp)
                        )
                        Text(
                            text = "Flight",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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
                    Button(
                        onClick = {
                            showDialogFlights.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(5.dp),
                        modifier = Modifier.padding(start = 130.dp, end = 20.dp)
                    ) {
                        Text(
                            text = "More details",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023FCC),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                }

                //converts the flight date to day name and all the others like Saturday 19 March 2024
                dateInNums.value = flightsCheckIn.value[0]?.flightDate?.value.toString()
                dateToString(dateInNums, dateInWords)
                Text(
                    text = dateInWords.value,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    fontWeight = FontWeight.Bold
                )
                if (!directFlight.value) {
                    flightIndex.intValue = 1
                }
                Text(
                    text = "${flightsCheckIn.value[0]?.departureTime?.value} ${flightsCheckIn.value[0]?.departureCity?.value} - " +
                            if (directFlight.value)
                                "${flightsCheckIn.value[0]?.arrivalTime?.value} ${flightsCheckIn.value[0]?.arrivalCity?.value}"
                            else
                                "${flightsCheckIn.value[1]?.arrivalTime?.value} ${flightsCheckIn.value[1]?.arrivalCity?.value}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (directFlight.value) "Nonstop" else "One-stop",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                        color = Color(0xFF0077FF),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                    if (directFlight.value) {
                        flightDuration.value =
                            flightsCheckIn.value[0]?.flightDuration?.value.toString()
                    } else {
                        val (totalHours, totalMinutes) = findTotalHoursMinutesMyBooking(
                            flightsCheckIn.value[0],
                            flightsCheckIn.value[1]
                        )
                        flightDuration.value = "${totalHours}h ${totalMinutes}min"
                    }
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = "duration",
                        tint = Color(0xFF0077FF),
                        modifier = Modifier.padding(top = 9.5.dp, start = 25.dp)
                    )
                    Text(
                        text = flightDuration.value,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                        color = Color(0xFF0077FF),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "passenger",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(top = 13.dp, start = 10.dp)
                    )
                    Text(
                        text = "Passengers",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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
                CheckboxPassengers(
                    numOfPassengersCheckIn = numOfPassengersCheckIn,
                    passengersCheckIn = passengersCheckIn,
                    showDialogPassenger = showDialogPassenger,
                    selectedIndex = selectedIndex,
                    checkedState = checkedState
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row {
                    Icon(
                        Icons.Filled.AirlineSeatReclineNormal,
                        contentDescription = "seats",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 12.5.dp)
                    )
                    Text(
                        text = "Seats",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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

                Column {
                    for (index in 0 until numOfPassengersCheckIn.intValue) {
                        Row {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "passenger",
                                tint = Color(0xFF0077FF),
                                modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                            )
                            Text(
                                text =
                                if (baggageAndSeatCheckIn.value[index]?.gender?.value == "Female")
                                    "Mrs ${baggageAndSeatCheckIn.value[index]?.firstname?.value} ${baggageAndSeatCheckIn.value[index]?.lastname?.value}"
                                else
                                    "Mr ${baggageAndSeatCheckIn.value[index]?.firstname?.value} ${baggageAndSeatCheckIn.value[index]?.lastname?.value}",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                                color = Color(0xFF0077FF),
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
                        Text(
                            text = if (directFlight.value)
                                    "${baggageAndSeatCheckIn.value[index]?.departurecity?.value} to ${baggageAndSeatCheckIn.value[index]?.arrivalcity?.value}\n"+
                                            "Seat Number: ${baggageAndSeatCheckIn.value[index]?.seatnumber?.value}"
                                else
                                    "${baggageAndSeatCheckIn.value[index]?.departurecity?.value} to ${baggageAndSeatCheckIn.value[index]?.arrivalcity?.value}\n"+
                                            "Seat Number: ${baggageAndSeatCheckIn.value[index]?.seatnumber?.value}\n\n"+
                                            "${baggageAndSeatCheckIn.value[index+numOfPassengersCheckIn.intValue]?.departurecity?.value} to ${baggageAndSeatCheckIn.value[index+numOfPassengersCheckIn.intValue]?.arrivalcity?.value}\n"+
                                            "Seat Number: ${baggageAndSeatCheckIn.value[index+numOfPassengersCheckIn.intValue]?.seatnumber?.value}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                            color = Color(0xFF0077FF),
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
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row {
                    Icon(
                        Icons.Filled.Luggage,
                        contentDescription = "baggage",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 12.5.dp)
                    )
                    Text(
                        text = "Baggage",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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

                Column {
                    for (index in 0 until numOfPassengersCheckIn.intValue) {
                        Row {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "passenger",
                                tint = Color(0xFF0077FF),
                                modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                            )
                            Text(
                                text =
                                if (baggageAndSeatCheckIn.value[index]?.gender?.value == "Female")
                                    "Mrs ${baggageAndSeatCheckIn.value[index]?.firstname?.value} ${baggageAndSeatCheckIn.value[index]?.lastname?.value}"
                                else
                                    "Mr ${baggageAndSeatCheckIn.value[index]?.firstname?.value} ${baggageAndSeatCheckIn.value[index]?.lastname?.value}",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                                color = Color(0xFF0077FF),
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
                        Text(
                            text = "Total baggage pieces 23kg: ${baggageAndSeatCheckIn.value[index]?.baggage23kg?.value}\n" +
                                    "Total baggage pieces 32kg: ${baggageAndSeatCheckIn.value[index]?.baggage32kg?.value}\n",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                            color = Color(0xFF0077FF),
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
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row {
                    Icon(
                        Icons.Filled.Wifi,
                        contentDescription = "wifi",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 12.dp)
                    )
                    Text(
                        text = "Wifi on Board",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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
                Text(
                    text = when (wifiOnBoardCheckIn.intValue) {
                        0 -> "No Wifi"
                        1 -> "Web browsing & Social Media, \nup to 1.5Mbps"
                        2 -> "Audio/Video streaming, \nHigh speed web browsing & Social Media, \nup to 15Mbps"
                        else -> "Error with database, wifi didn't initialized"
                    },
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row {
                    Icon(
                        Icons.Filled.Pets,
                        contentDescription = "pets",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 12.dp)
                    )
                    Text(
                        text = "Pets",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp),
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
                Text(
                    text =
                    if (petSizeCheckIn.value == "null")
                        "No Pets Selected"
                    else {
                        when (petSizeCheckIn.value) {
                            "Small" -> {
                                "Pet Size: Small (<8kg)"
                            }
                            "Medium" -> {
                                "Pet Size: Medium (<25kg)"
                            }
                            else -> {
                                "Pet Size: Large (>25kg)"
                            }
                        }
                    },
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
                Text(
                    text = "Select all passengers to continue with check-in",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        onClick = {
                            showDialogCheckIn.value = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(top = 10.dp, bottom = 30.dp),
                        enabled = checkedState.all { it.value },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Text(
                            text = "Check-in",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                        ShowDialogToCommitCheckIn(
                            navController =  navController,
                            selectedIndex = selectedIndex,
                            showDialog = showDialogCheckIn,
                            bookingIdCheckIn = bookingIdCheckIn,
                            flightsCheckIn = flightsCheckIn,
                            directFlight = directFlight,
                            passengersCheckIn = passengersCheckIn,
                            numOfPassengersCheckIn = numOfPassengersCheckIn,
                            petSizeCheckIn = petSizeCheckIn,
                            wifiOnBoardCheckIn = wifiOnBoardCheckIn,
                            baggageAndSeatCheckIn = baggageAndSeatCheckIn,
                            updateCheckIn = updateCheckIn,
                            backButton = backButton
                        )
                    }
                }
                if(showDialogPassenger.value) {
                    ShowDialogPassengerInfo(
                        showDialogPassenger = showDialogPassenger,
                        passengersCheckIn = passengersCheckIn,
                        index = selectedIndex
                    )
                }
                if(showDialogFlights.value) {
                    ShowDialogFlightsInfo(
                        showDialogFlights = showDialogFlights,
                        flightsCheckIn = flightsCheckIn,
                        directFlight = directFlight,
                        duration = flightDuration
                    )
                }
            }
        }
    }
}

//alert dialog for more details in passengers that shows the information that it is in
//the list of passengersCheckIn
@Composable
private fun ShowDialogPassengerInfo(showDialogPassenger: MutableState<Boolean>,
                                    passengersCheckIn: MutableState<SnapshotStateList<PassengerInfo?>>,
                                    index: MutableIntState
){

    Box(
        contentAlignment = Alignment.Center
    ){
        AlertDialog(
            modifier = Modifier
                .width(400.dp)
                .height(300.dp),
            onDismissRequest = { showDialogPassenger.value = false },
            title = {
                Text(
                    text = "Passenger's Information",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp),
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
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())) {

                    Row {
                        Text(
                            "Email: ",
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
                        passengersCheckIn.value[index.intValue]?.email?.value?.let {
                            Text(
                                text = it,
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
                    Row {
                        Text(
                            "Phone Number: ",
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
                        passengersCheckIn.value[index.intValue]?.phonenumber?.value?.let {
                            Text(
                                text = it,
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
                    Row {
                        Text(
                            "Birthdate: ",
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
                        passengersCheckIn.value[index.intValue]?.birthdate?.value?.let {
                            Text(
                                text = it,
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
                    Row {
                        Text(
                            "Gender: ",
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
                        passengersCheckIn.value[index.intValue]?.gender?.value?.let {
                            Text(
                                text = it,
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogPassenger.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF023E8A)
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        "OK",
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
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }
}

//alert dialog for the more details in the flights
//and takes as parameters the info that must show in the dialog
//like duration and flightsCheckIn list that has all the information about flights
@Composable
private fun ShowDialogFlightsInfo(showDialogFlights: MutableState<Boolean>,
                          flightsCheckIn: MutableState<SnapshotStateList<BasicFlight?>>,
                          directFlight: MutableState<Boolean>,
                          duration: MutableState<String>){
    val repeatTimes = remember {
        mutableIntStateOf(0)
    }

    if(directFlight.value){
        repeatTimes.intValue = 1
    }
    else{
        repeatTimes.intValue = 2
    }


    Box(
        contentAlignment = Alignment.Center
    ){
        AlertDialog(
            onDismissRequest = { showDialogFlights.value = false },
            title = {
                Text(
                    text = "More Details",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp),
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
            },
            text = {
                LazyColumn {
                    items(repeatTimes.intValue) {index->
                        Log.d("index", index.toString())

                        if(index==0) {
                            Text(
                                if (repeatTimes.intValue == 1) "Flight" else "Flights",
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
                                Text(
                                    "Total time: ",
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
                                    text = "${duration.value}, " + if (directFlight.value) "nonstop" else "one-stop",
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
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            thickness = 1.dp,
                            color = Color(0xFF023E8A)
                        )
                        if(!directFlight.value) {
                            Text(
                                text = if (index == 0) "1st Flight" else "2nd Flight",
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
                                "Flight date: ",
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
                                text = "${flightsCheckIn.value[index]?.flightDate?.value}",
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
                        Text(
                            text = "${flightsCheckIn.value[index]?.departureTime?.value}     ${flightsCheckIn.value[index]?.departureCity?.value} (${flightsCheckIn.value[index]?.departureAirp?.value})",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 10.dp),
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
                                    text = "${flightsCheckIn.value[index]?.flightDuration?.value}",
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
                        Text(
                            text = "${flightsCheckIn.value[index]?.arrivalTime?.value}     ${flightsCheckIn.value[index]?.arrivalCity?.value} (${flightsCheckIn.value[index]?.arrivalAirp?.value})",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 10.dp),
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
                        Row{
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
                                text = "${flightsCheckIn.value[index]?.flightId?.value}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 10.dp),
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
                        Row{
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
                                text = "${flightsCheckIn.value[index]?.airplaneModel?.value}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 10.dp),
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
                        Row{
                            Text(
                                text = "Booking class: ",
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
                                text = "${flightsCheckIn.value[index]?.classType?.value}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 10.dp),
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogFlights.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF023E8A)
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        "OK",
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

//function that shows the passengers with checkboxes in front of their names
//to be checked for the check-in button to be enabled
@Composable
private fun CheckboxPassengers(
    numOfPassengersCheckIn: MutableIntState,
    passengersCheckIn: MutableState<SnapshotStateList<PassengerInfo?>>,
    showDialogPassenger: MutableState<Boolean>,
    selectedIndex: MutableIntState,
    checkedState: SnapshotStateList<MutableState<Boolean>>
) {

    Column {
        for (index in 0 until numOfPassengersCheckIn.intValue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox for each passenger
                Checkbox(
                    checked = checkedState[index].value,
                    onCheckedChange = { isChecked ->
                        checkedState[index].value = isChecked

                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF00B4D8),
                        uncheckedColor = Color(0xFF00B4D8)
                    ),
                    modifier = Modifier.padding(start = 10.dp, top = 12.5.dp )
                )

                Text(
                    text = if (passengersCheckIn.value[index]?.gender?.value == "Female")
                        "Mrs ${passengersCheckIn.value[index]?.firstname?.value} ${passengersCheckIn.value[index]?.lastname?.value}"
                    else
                        "Mr ${passengersCheckIn.value[index]?.firstname?.value} ${passengersCheckIn.value[index]?.lastname?.value}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )

                Box(
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(
                        onClick = {
                            showDialogPassenger.value = true
                            selectedIndex.intValue = index
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "showDetails",
                            modifier = Modifier
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 9.dp
                                ),
                            tint = Color(0xFF023FCC)
                        )
                    }
                }
            }
        }
    }
}

//alert dialog for committing the check-in process
@Composable
fun ShowDialogToCommitCheckIn(
    navController: NavController,
    selectedIndex: MutableIntState,
    showDialog: MutableState<Boolean>,
    bookingIdCheckIn: MutableState<String>,
    flightsCheckIn: MutableState<SnapshotStateList<BasicFlight?>>,
    directFlight: MutableState<Boolean>,
    passengersCheckIn: MutableState<SnapshotStateList<PassengerInfo?>>,
    numOfPassengersCheckIn: MutableIntState,
    petSizeCheckIn: MutableState<String>,
    wifiOnBoardCheckIn: MutableIntState,
    baggageAndSeatCheckIn: MutableState<SnapshotStateList<BaggageAndSeatPerPassenger?>>,
    updateCheckIn: MutableState<Boolean>,
    backButton: MutableState<Boolean>
) {
    val ctx = LocalContext.current
    val showDialogConfirm = remember {
        mutableStateOf(false)
    }

    //api that updates the check-in for the passengers in specific flight/s
    LaunchedEffect(updateCheckIn.value) {
        if (updateCheckIn.value) {
            val url = "http://100.106.205.30:5000/flynow/update-checkin"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", bookingIdCheckIn.value)
            jsonObject.put("flightid1", flightsCheckIn.value[0]?.flightId?.value)
            if(flightsCheckIn.value.size > 1){
                jsonObject.put("flightid2", flightsCheckIn.value[1]?.flightId?.value)
                jsonObject.put("numofflights", 2)
            }
            else{
                jsonObject.put("flightid2", "")
                jsonObject.put("numofflights", 1)
            }
            jsonArray.put(jsonObject)
            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    if(response.getJSONObject(0).getBoolean("success")) {
                        Log.d("response", "success")
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
            updateCheckIn.value = false
        }
    }

    //alert dialog to complete the update of the check in
    if (showDialog.value || showDialogConfirm.value) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(
                        text = if (showDialog.value) "Confirm Check-In" else if(!updateCheckIn.value) "The Check-In Was Done Successfully!" else "",
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
                    if(!updateCheckIn.value) {
                        Icon(
                            if (showDialog.value) Icons.Filled.QuestionMark
                            else Icons.Filled.Verified,
                            contentDescription = "question",
                            modifier =
                            if (showDialog.value) Modifier.padding(start = 160.dp, top = 0.dp)
                            else Modifier.padding(top = 33.dp, start = 125.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                },
                text = {
                    if (showDialog.value && !updateCheckIn.value) {
                        Text(
                            text = "Are you sure you want to check-in?",
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
                    if (updateCheckIn.value) {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                },
                confirmButton = {
                    if (showDialog.value && !updateCheckIn.value) {
                        Button(
                            onClick = {
                                updateCheckIn.value = true
                                showDialog.value = false
                                showDialogConfirm.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF023E8A)
                            )
                        ) {
                            Text(
                                "Yes",
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
                    } else {
                        if(!updateCheckIn.value) {
                            Button(
                                onClick = {
                                    showDialogConfirm.value = false
                                    backButton.value = true
                                    showDialog.value = false
                                    bookingIdCheckIn.value = ""
                                    directFlight.value = false
                                    selectedIndex.intValue = 0
                                    flightsCheckIn.value.clear()
                                    passengersCheckIn.value.clear()
                                    numOfPassengersCheckIn.intValue = 0
                                    petSizeCheckIn.value = ""
                                    wifiOnBoardCheckIn.intValue = -1
                                    baggageAndSeatCheckIn.value.clear()
                                    navController.navigate(Home.route) {
                                        popUpTo(CheckInDetails.route)
                                        launchSingleTop = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF023E8A)
                                )
                            ) {
                                Text(
                                    "OK",
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
                        }
                    }
                },
                dismissButton = {
                    if (showDialog.value && !updateCheckIn.value) {
                        Button(
                            onClick = {
                                showDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF023E8A)
                            )
                        ) {
                            Text(
                                "No",
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
                    }
                },
                containerColor = Color(0xFFEBF2FA),
                textContentColor = Color(0xFF023E8A),
                titleContentColor = Color(0xFF023E8A),
                tonalElevation = 30.dp,
                properties = DialogProperties(dismissOnClickOutside = false)
            )
        }
    }
}


