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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

//navController helps to navigate to previous page or next page,
//selectedIndex helps if there is the bottom bar in previous page,
//oneWay, outboundDirect and inboundDirect is to know what type is the flight,
//with return or not and if it is direct or with one stop flight,
//flightsMyBooking, passengersMyBooking are lists that save the info about flights and passengers,
//wifiOnBoard, petSizeMyBooking are variables that save info about pets and wifi,
//numOfPassengers is the number of passengers in the booking,
//baggageAndSeatMyBooking, carsMyBooking are lists that save the info about baggage, seats and cars,
//totalPriceMyBooking, rentingTotalPrice are variables that save the price for the booking and the renting of car
//booking id is the booking reference that the searching is happened
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyBookingDetailsScreen(navController: NavController,
                           selectedIndex: MutableIntState,
                           oneWay: MutableState<Boolean>,
                           outboundDirect: MutableState<Boolean>,
                           inboundDirect: MutableState<Boolean>,
                           flightsMyBooking: MutableState<SnapshotStateList<BasicFlight?>>,
                           passengersMyBooking: MutableState<SnapshotStateList<PassengerInfo?>>,
                           numOfPassengers: MutableIntState,
                           petSizeMyBooking: MutableState<String>,
                           wifiOnBoard: MutableIntState,
                           bookingId: MutableState<String>,
                           baggageAndSeatMyBooking: MutableState<SnapshotStateList<BaggageAndSeatPerPassenger?>>,
                           carsMyBooking: MutableState<SnapshotStateList<CarDetailsMyBooking?>>,
                           totalPriceMyBooking: MutableDoubleState,
                           rentingTotalPrice: MutableDoubleState
) {
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    //variables for showing dialog for more details for flights and passengers
    val showDialogPassenger= remember {
        mutableStateOf(false)
    }
    val showDialogFlights= remember {
        mutableStateOf(false)
    }
    val outboundFlight = remember {
        mutableStateOf(false)
    }
    val selectedIndexDetails = remember {
        mutableIntStateOf(0)
    }
    val flightIndex = remember {
        mutableIntStateOf(0)
    }
    val dateInNums = remember {
        mutableStateOf("")
    }
    val dateInWords= remember {
        mutableStateOf("")
    }
    val outboundDuration = remember {
        mutableStateOf("")
    }
    val inboundDuration = remember {
        mutableStateOf("")
    }
    val backButton = remember {
        mutableStateOf(false)
    }
    val deleteBooking = remember {
        mutableStateOf(false)
    }
    val ctx = LocalContext.current
    val showDialog = remember {
        mutableStateOf(false)
    }
    val showDialogConfirm = remember {
        mutableStateOf(false)
    }

    //api that deletes the booking and all the references about this booking
    LaunchedEffect(deleteBooking.value) {
        if(deleteBooking.value) {
            val url = "http://100.106.205.30:5000/flynow/delete-booking"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingId", bookingId.value)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { _ ->
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(3000)
            deleteBooking.value = false
        }
    }

    //alert dialog to complete the deletion of the booking
    if (showDialog.value || showDialogConfirm.value) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(
                        text = if (showDialog.value) "Confirm Deleting This Booking" else if(!deleteBooking.value) "The Cancellation Of The Reservation Was Done Successfully!" else "",
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
                    if(!deleteBooking.value) {
                        Icon(
                            if (showDialog.value) Icons.Filled.QuestionMark
                            else Icons.Filled.Verified,
                            contentDescription = "question",
                            modifier =
                            if (showDialog.value) Modifier.padding(start = 72.dp, top = 33.dp)
                            else Modifier.padding(top = 66.dp, start = 125.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                },
                text = {
                    if (showDialog.value && !deleteBooking.value) {
                        Text(
                            text = "Are you sure you want to delete this booking?",
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
                    if (deleteBooking.value) {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                },
                confirmButton = {
                    if (showDialog.value && !deleteBooking.value) {
                        Button(
                            onClick = {
                                deleteBooking.value = true
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
                        if(!deleteBooking.value) {
                            Button(
                                onClick = {
                                    showDialogConfirm.value = false
                                    showDialog.value = false
                                    selectedIndex.intValue = 0
                                    backButton.value = true
                                    oneWay.value = false
                                    outboundDirect.value = false
                                    inboundDirect.value = false
                                    flightsMyBooking.value.clear()
                                    passengersMyBooking.value.clear()
                                    numOfPassengers.intValue = 0
                                    totalPriceMyBooking.doubleValue = 0.0
                                    rentingTotalPrice.doubleValue = 0.0
                                    petSizeMyBooking.value = ""
                                    wifiOnBoard.intValue = -1
                                    bookingId.value = ""
                                    baggageAndSeatMyBooking.value.clear()
                                    carsMyBooking.value.clear()
                                    navController.navigate(Home.route) {
                                        popUpTo(MyBookingDetails.route)
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
                    if (showDialog.value && !deleteBooking.value) {
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
            //"Back" button to find your booking page
            IconButton(onClick = {
                backButton.value = true
                oneWay.value = false
                outboundDirect.value = false
                inboundDirect.value = false
                flightsMyBooking.value.clear()
                passengersMyBooking.value.clear()
                carsMyBooking.value.clear()
                numOfPassengers.intValue = 0
                totalPriceMyBooking.doubleValue = 0.0
                rentingTotalPrice.doubleValue = 0.0
                petSizeMyBooking.value = ""
                wifiOnBoard.intValue = -1
                bookingId.value = ""
                baggageAndSeatMyBooking.value.clear()
                navController.navigate(MyBooking.route) {
                    popUpTo(MyBookingDetails.route)
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
                    text = "My Booking",
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
                    Icons.Filled.AirplaneTicket,
                    contentDescription = "bookingId",
                    tint = Color(0xFF023E8A),
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 5.dp)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

        if(!backButton.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mybook),
                    contentDescription = "baggage",
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
                        text = bookingId.value.uppercase(Locale.getDefault()),
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
                            Icons.Filled.FlightTakeoff,
                            contentDescription = "takeoff",
                            tint = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 10.dp, top = 12.dp)
                        )
                        Text(
                            text = "Outbound",
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
                            outboundFlight.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(5.dp),
                        modifier = Modifier.padding(end = 10.dp)
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
                dateInNums.value = flightsMyBooking.value[0]?.flightDate?.value.toString()
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
                if (!outboundDirect.value) {
                    flightIndex.intValue = 1
                }
                Text(
                    text = "${flightsMyBooking.value[0]?.departureTime?.value} ${flightsMyBooking.value[0]?.departureCity?.value} - " +
                            if (outboundDirect.value)
                                "${flightsMyBooking.value[0]?.arrivalTime?.value} ${flightsMyBooking.value[0]?.arrivalCity?.value}"
                            else
                                "${flightsMyBooking.value[1]?.arrivalTime?.value} ${flightsMyBooking.value[1]?.arrivalCity?.value}",
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
                        text = if (outboundDirect.value) "Nonstop" else "One-stop",
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
                    if (outboundDirect.value) {
                        outboundDuration.value =
                            flightsMyBooking.value[0]?.flightDuration?.value.toString()
                    } else {
                        val (totalHours, totalMinutes) = findTotalHoursMinutesMyBooking(
                            flightsMyBooking.value[0],
                            flightsMyBooking.value[1]
                        )
                        outboundDuration.value = "${totalHours}h ${totalMinutes}min"
                    }
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = "duration",
                        tint = Color(0xFF0077FF),
                        modifier = Modifier.padding(top = 9.5.dp, start = 25.dp)
                    )
                    Text(
                        text = outboundDuration.value,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            Icons.Filled.FlightLand,
                            contentDescription = "land",
                            tint = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 10.dp, top = 12.dp)
                        )
                        Text(
                            text = "Inbound",
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
                    if (!oneWay.value) {
                        Button(
                            onClick = {
                                showDialogFlights.value = true
                                outboundFlight.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(5.dp),
                            modifier = Modifier.padding(end = 10.dp)
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
                }
                if (oneWay.value) {
                    Text(
                        text = "No Inbound flights",
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
                } else {
                    dateInNums.value =
                        flightsMyBooking.value[flightIndex.intValue + 1]?.flightDate?.value.toString()
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
                    Text(
                        text = "${flightsMyBooking.value[flightIndex.intValue + 1]?.departureTime?.value} ${flightsMyBooking.value[flightIndex.intValue + 1]?.departureCity?.value} - " +
                                if (inboundDirect.value)
                                    "${flightsMyBooking.value[flightIndex.intValue + 1]?.arrivalTime?.value} ${flightsMyBooking.value[flightIndex.intValue + 1]?.arrivalCity?.value}"
                                else
                                    "${flightsMyBooking.value[flightIndex.intValue + 2]?.arrivalTime?.value}  ${flightsMyBooking.value[flightIndex.intValue + 2]?.arrivalCity?.value}",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (inboundDirect.value) "Nonstop" else "One-stop",
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
                        if (inboundDirect.value) {
                            inboundDuration.value =
                                flightsMyBooking.value[flightIndex.intValue + 1]?.flightDuration?.value.toString()
                        } else {
                            val (totalHours, totalMinutes) = findTotalHoursMinutesMyBooking(
                                flightsMyBooking.value[flightIndex.intValue + 1],
                                flightsMyBooking.value[flightIndex.intValue + 2]
                            )
                            inboundDuration.value = "${totalHours}h ${totalMinutes}min"
                        }
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = "duration",
                            tint = Color(0xFF0077FF),
                            modifier = Modifier.padding(top = 9.5.dp, start = 25.dp)
                        )
                        Text(
                            text = inboundDuration.value,
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

                Column {
                    for (index in 0 until numOfPassengers.intValue) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text =
                                if (passengersMyBooking.value[index]?.gender?.value == "Female")
                                    "Mrs ${passengersMyBooking.value[index]?.firstname?.value} ${passengersMyBooking.value[index]?.lastname?.value}"
                                else
                                    "Mr ${passengersMyBooking.value[index]?.firstname?.value} ${passengersMyBooking.value[index]?.lastname?.value}",

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
                            Box(
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        showDialogPassenger.value = true
                                        selectedIndexDetails.intValue = index
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = "showDetails",
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp,
                                                end = 10.dp,
                                                top = 7.dp
                                            ),
                                        tint = Color(0xFF023FCC)
                                    )

                                }
                            }
                        }
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

                Text(
                    text = "Outbound",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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

                Column {
                    for (index in 0 until numOfPassengers.intValue) {
                        Row {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "passenger",
                                tint = Color(0xFF0077FF),
                                modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                            )
                            Text(
                                text =
                                if (baggageAndSeatMyBooking.value[index]?.gender?.value == "Female")
                                    "Mrs ${baggageAndSeatMyBooking.value[index]?.firstname?.value} ${baggageAndSeatMyBooking.value[index]?.lastname?.value}"
                                else
                                    "Mr ${baggageAndSeatMyBooking.value[index]?.firstname?.value} ${baggageAndSeatMyBooking.value[index]?.lastname?.value}",
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
                            text = if (outboundDirect.value)
                                "${baggageAndSeatMyBooking.value[index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[index]?.arrivalcity?.value}\n" +
                                        "Seat Number: ${baggageAndSeatMyBooking.value[index]?.seatnumber?.value}"
                            else
                                "${baggageAndSeatMyBooking.value[index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[index]?.arrivalcity?.value}\n" +
                                        "Seat Number: ${baggageAndSeatMyBooking.value[index]?.seatnumber?.value}\n\n" +
                                        "${baggageAndSeatMyBooking.value[index + numOfPassengers.intValue]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[index + numOfPassengers.intValue]?.arrivalcity?.value}\n" +
                                        "Seat Number: ${baggageAndSeatMyBooking.value[index + numOfPassengers.intValue]?.seatnumber?.value}",
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
                            text =  if(baggageAndSeatMyBooking.value[index]?.checkin?.value == true) "Checked-in\n" else "Not checked-in\n",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                            color = if(baggageAndSeatMyBooking.value[index]?.checkin?.value == true) Color(0xFF023E8A) else Color.Red,
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
                if (!oneWay.value) {

                    Text(
                        text = "Inbound",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
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

                    Column {
                        for (index in 0 until numOfPassengers.intValue) {
                            Row {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "passenger",
                                    tint = Color(0xFF0077FF),
                                    modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                                )
                                Text(
                                    text =
                                    if (baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.gender?.value == "Female")
                                        "Mrs ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.firstname?.value} ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.lastname?.value}"
                                    else
                                        "Mr ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.firstname?.value} ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.lastname?.value}",
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
                                text = if (outboundDirect.value && inboundDirect.value)//1->1
                                    "${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.arrivalcity?.value}\n" +
                                            "Seat Number: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.seatnumber?.value}"
                                else if (outboundDirect.value && !inboundDirect.value)//1->2
                                    "${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.arrivalcity?.value}\n" +
                                            "Seat Number: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.seatnumber?.value}\n\n" +
                                            "${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.arrivalcity?.value}\n" +
                                            "Seat Number: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.seatnumber?.value}"
                                else if (!outboundDirect.value && inboundDirect.value)//2->1
                                    "${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.arrivalcity?.value}\n" +
                                            "Seat Number: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.seatnumber?.value}"
                                else//2->2
                                    "${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.arrivalcity?.value}\n" +
                                            "Seat Number: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.seatnumber?.value}\n\n" +
                                            "${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index + numOfPassengers.intValue]?.departurecity?.value} to ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index + numOfPassengers.intValue]?.arrivalcity?.value}\n" +
                                            "Seat Number: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index + numOfPassengers.intValue]?.seatnumber?.value}",
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
                                text = if((outboundDirect.value && inboundDirect.value) || (outboundDirect.value && !inboundDirect.value))
                                    if(baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.checkin?.value == true)
                                        "Checked-in\n"
                                    else
                                        "Not checked-in\n"
                                else
                                    if(baggageAndSeatMyBooking.value[numOfPassengers.intValue*2 + index]?.checkin?.value == true)
                                        "Checked-in\n"
                                    else
                                        "Not checked-in\n",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                color =  if((outboundDirect.value && inboundDirect.value) || (outboundDirect.value && !inboundDirect.value))
                                    if(baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.checkin?.value == true)
                                        Color(0xFF023E8A)
                                    else
                                        Color.Red
                                else
                                    if(baggageAndSeatMyBooking.value[numOfPassengers.intValue*2 + index]?.checkin?.value == true)
                                        Color(0xFF023E8A)
                                    else
                                        Color.Red,
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
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(),
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

                Text(
                    text = "Outbound",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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

                Column {
                    for (index in 0 until numOfPassengers.intValue) {
                        Row {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "passenger",
                                tint = Color(0xFF0077FF),
                                modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                            )
                            Text(
                                text =
                                if (baggageAndSeatMyBooking.value[index]?.gender?.value == "Female")
                                    "Mrs ${baggageAndSeatMyBooking.value[index]?.firstname?.value} ${baggageAndSeatMyBooking.value[index]?.lastname?.value}"
                                else
                                    "Mr ${baggageAndSeatMyBooking.value[index]?.firstname?.value} ${baggageAndSeatMyBooking.value[index]?.lastname?.value}",
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
                            text = "Total baggage pieces 23kg: ${baggageAndSeatMyBooking.value[index]?.baggage23kg?.value}\n" +
                                    "Total baggage pieces 32kg: ${baggageAndSeatMyBooking.value[index]?.baggage32kg?.value}\n",
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
                if (!oneWay.value) {
                    Text(
                        text = "Inbound",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
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
                    Column {
                        for (index in 0 until numOfPassengers.intValue) {
                            Row {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "passenger",
                                    tint = Color(0xFF0077FF),
                                    modifier = Modifier.padding(top = 11.dp, start = 10.dp)
                                )
                                Text(
                                    text =
                                    if (baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.gender?.value == "Female")
                                        "Mrs ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.firstname?.value} ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.lastname?.value}"
                                    else
                                        "Mr ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.firstname?.value} ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.lastname?.value}",
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
                                text =
                                if (outboundDirect.value && inboundDirect.value)//1->1
                                    "Total baggage pieces 23kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.baggage23kg?.value}\n" +
                                            "Total baggage pieces 32kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.baggage32kg?.value}\n"
                                else if (outboundDirect.value && !inboundDirect.value)//1->2
                                    "Total baggage pieces 23kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.baggage23kg?.value}\n" +
                                            "Total baggage pieces 32kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue + index]?.baggage32kg?.value}\n"
                                else if (!outboundDirect.value && inboundDirect.value)//2->1
                                    "Total baggage pieces 23kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.baggage23kg?.value}\n" +
                                            "Total baggage pieces 32kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.baggage32kg?.value}\n"
                                else//2->2
                                    "Total baggage pieces 23kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.baggage23kg?.value}\n" +
                                            "Total baggage pieces 32kg: ${baggageAndSeatMyBooking.value[numOfPassengers.intValue * 2 + index]?.baggage32kg?.value}\n",
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
                    text = when (wifiOnBoard.intValue) {
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
                    if (petSizeMyBooking.value == "null")
                        "No Pets Selected"
                    else {
                        when (petSizeMyBooking.value) {
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
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color(0xFF023E8A)
                )
                Row {
                    Icon(
                        Icons.Filled.DirectionsCar,
                        contentDescription = "car",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 12.dp)
                    )
                    Text(
                        text = "Car",
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
                    if(carsMyBooking.value.size == 0) {
                        Text(
                            text = "No Cars Selected",
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
                    else {
                        for (index in 0 until carsMyBooking.value.size) {
                            Card(
                                modifier = Modifier
                                    .padding(
                                        top = if (index == 0)
                                            20.dp else 30.dp,
                                        start = 5.dp,
                                        end = 5.dp,
                                        bottom = 10.dp
                                    )
                                    .width(350.dp)
                                    .height(250.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = ShapeDefaults.Small,
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 10.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxHeight(),
                                        horizontalAlignment = Alignment.Start) {
                                        Image(
                                            bitmap = carsMyBooking.value[index]!!.carImage.value.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(160.dp, 160.dp)
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = carsMyBooking.value[index]!!.model.value,
                                                fontSize = 20.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp),
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF023E8A)
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Outlined.Person,
                                                    contentDescription = "person",
                                                    tint = Color(0xFF023E8A),
                                                    modifier = Modifier.padding(start = 10.dp)
                                                )
                                                Text(
                                                    text = "x5",
                                                    fontSize = 16.sp,
                                                    fontFamily = FontFamily(
                                                        fonts = listOf(
                                                            Font(
                                                                resId = R.font.opensans
                                                            )
                                                        )
                                                    ),
                                                    color = Color(0xFF023E8A),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Icon(
                                                    Icons.Outlined.Luggage,
                                                    contentDescription = "baggage",
                                                    tint = Color(0xFF023E8A),
                                                    modifier = Modifier.padding(start = 8.dp)
                                                )
                                                Text(
                                                    text = "x2",
                                                    fontSize = 16.sp,
                                                    fontFamily = FontFamily(
                                                        fonts = listOf(
                                                            Font(
                                                                resId = R.font.opensans
                                                            )
                                                        )
                                                    ),
                                                    color = Color(0xFF023E8A),
                                                    modifier = Modifier.padding(start = 2.dp),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = carsMyBooking.value[index]!!.company.value,
                                            fontSize = 22.sp,
                                            fontFamily = FontFamily(
                                                fonts = listOf(
                                                    Font(
                                                        resId = R.font.roboto
                                                    )
                                                )
                                            ),
                                            modifier = Modifier.padding(top = 10.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF023E8A),
                                            textDecoration = TextDecoration.Underline
                                        )
                                        Box(modifier = Modifier
                                            .fillMaxSize()
                                            .padding(end = 10.dp),
                                            contentAlignment = Alignment.CenterEnd) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Row {
                                                    Text(
                                                        text = "Location",
                                                        fontSize = 18.sp,
                                                        fontFamily = FontFamily(
                                                            fonts = listOf(
                                                                Font(
                                                                    resId = R.font.gilroy
                                                                )
                                                            )
                                                        ),
                                                        modifier = Modifier.padding(top = 15.dp),
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF023E8A)
                                                    )
                                                    Text(
                                                        text = carsMyBooking.value[index]!!.location.value,
                                                        fontSize = 18.sp,
                                                        fontFamily = FontFamily(
                                                            fonts = listOf(
                                                                Font(
                                                                    resId = R.font.lato
                                                                )
                                                            )
                                                        ),
                                                        modifier = Modifier.padding(start = 10.dp, top = 15.dp),
                                                        color = Color(0xFF023E8A)
                                                    )
                                                }
                                                Row {
                                                    Text(
                                                        text = "Pick Up",
                                                        fontSize = 18.sp,
                                                        fontFamily = FontFamily(
                                                            fonts = listOf(
                                                                Font(
                                                                    resId = R.font.gilroy
                                                                )
                                                            )
                                                        ),
                                                        modifier = Modifier.padding(top = 30.dp, end = 9.dp),
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF023E8A)
                                                    )
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text(
                                                            text = carsMyBooking.value[index]!!.pickUpDateTime.value.substring(0,10),
                                                            fontSize = 18.sp,
                                                            fontFamily = FontFamily(
                                                                fonts = listOf(
                                                                    Font(
                                                                        resId = R.font.lato
                                                                    )
                                                                )
                                                            ),
                                                            modifier = Modifier.padding(top = 20.dp),
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF023E8A)
                                                        )
                                                        Text(
                                                            text = carsMyBooking.value[index]!!.pickUpDateTime.value.substring(11,16),
                                                            fontSize = 18.sp,
                                                            fontFamily = FontFamily(
                                                                fonts = listOf(
                                                                    Font(
                                                                        resId = R.font.lato
                                                                    )
                                                                )
                                                            ),
                                                            modifier = Modifier.padding(top = 3.dp, bottom = 5.dp),
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF023E8A)
                                                        )
                                                    }
                                                }
                                                Row {
                                                    Text(
                                                        text = "Return",
                                                        fontSize = 18.sp,
                                                        fontFamily = FontFamily(
                                                            fonts = listOf(
                                                                Font(
                                                                    resId = R.font.gilroy
                                                                )
                                                            )
                                                        ),
                                                        modifier = Modifier.padding(top = 20.dp, end = 9.dp),
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF023E8A)
                                                    )
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text(
                                                            text = carsMyBooking.value[index]!!.returnDateTime.value.substring(0,10),
                                                            fontSize = 18.sp,
                                                            fontFamily = FontFamily(
                                                                fonts = listOf(
                                                                    Font(
                                                                        resId = R.font.lato
                                                                    )
                                                                )
                                                            ),
                                                            modifier = Modifier.padding(top = 10.dp),
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF023E8A)
                                                        )
                                                        Text(
                                                            text = carsMyBooking.value[index]!!.returnDateTime.value.substring(11,16),
                                                            fontSize = 18.sp,
                                                            fontFamily = FontFamily(
                                                                fonts = listOf(
                                                                    Font(
                                                                        resId = R.font.lato
                                                                    )
                                                                )
                                                            ),
                                                            modifier = Modifier.padding(top = 3.dp, bottom = 5.dp),
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF023E8A)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
                        Icons.Outlined.Euro,
                        contentDescription = "price",
                        tint = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 10.dp, top = 13.dp)
                    )
                    Text(
                        text = "Price",
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Booking Price: ",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
                            text = "${totalPriceMyBooking.doubleValue} €",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 10.dp),
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Renting Cars Price: ",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
                            text = "${rentingTotalPrice.doubleValue} €",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 10.dp),
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Total Price: ",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
                            text = "${totalPriceMyBooking.doubleValue+rentingTotalPrice.doubleValue} €",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 10.dp),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        onClick = {
                            backButton.value = true
                            oneWay.value = false
                            outboundDirect.value = false
                            inboundDirect.value = false
                            flightsMyBooking.value.clear()
                            passengersMyBooking.value.clear()
                            numOfPassengers.intValue = 0
                            totalPriceMyBooking.doubleValue = 0.0
                            rentingTotalPrice.doubleValue = 0.0
                            petSizeMyBooking.value = ""
                            wifiOnBoard.intValue = -1
                            bookingId.value = ""
                            baggageAndSeatMyBooking.value.clear()
                            carsMyBooking.value.clear()
                            selectedIndex.intValue = 0
                            navController.navigate(Home.route) {
                                popUpTo(MyBookingDetails.route)
                                launchSingleTop = true
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(top = 30.dp, bottom = 30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Text(text = "Home",
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
                    ElevatedButton(
                        onClick = {
                            showDialog.value = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(top = 30.dp, bottom = 30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFa4161a)),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Row {
                            Text(text = "Delete Your Booking",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                            Icon(
                                Icons.Outlined.DeleteForever,
                                contentDescription = "deletion",
                                tint = Color.White
                            )
                        }
                    }
                }


                if (showDialogPassenger.value) {
                    ShowDialogPassengerInfo(
                        showDialogPassenger = showDialogPassenger,
                        passengersMyBooking = passengersMyBooking,
                        index = selectedIndexDetails
                    )
                }
                if (showDialogFlights.value) {
                    if (outboundFlight.value) {
                        ShowDialogFlightsInfo(
                            showDialogFlights = showDialogFlights,
                            flightsMyBooking = flightsMyBooking,
                            outboundDirect = outboundDirect,
                            inboundDirect = inboundDirect,
                            outboundFlight = outboundFlight,
                            duration = outboundDuration
                        )
                    } else {
                        ShowDialogFlightsInfo(
                            showDialogFlights = showDialogFlights,
                            flightsMyBooking = flightsMyBooking,
                            outboundDirect = outboundDirect,
                            inboundDirect = inboundDirect,
                            outboundFlight = outboundFlight,
                            duration = inboundDuration
                        )
                    }
                }
            }
        }
    }
}

//alert dialog for more details in passengers that shows the information that it is in
//the list of passengerMyBooking
@Composable
private fun ShowDialogPassengerInfo(showDialogPassenger: MutableState<Boolean>,
                            passengersMyBooking: MutableState<SnapshotStateList<PassengerInfo?>>,
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
                        passengersMyBooking.value[index.intValue]?.email?.value?.let {
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
                        passengersMyBooking.value[index.intValue]?.phonenumber?.value?.let {
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
                        passengersMyBooking.value[index.intValue]?.birthdate?.value?.let {
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
                        passengersMyBooking.value[index.intValue]?.gender?.value?.let {
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
//like duration and flightsMyBooking list that has all the information about flights
@Composable
fun ShowDialogFlightsInfo(showDialogFlights: MutableState<Boolean>,
                          flightsMyBooking: MutableState<SnapshotStateList<BasicFlight?>>,
                          outboundDirect: MutableState<Boolean>,
                          inboundDirect: MutableState<Boolean>,
                          outboundFlight: MutableState<Boolean>,
                          duration: MutableState<String>){
    val repeatTimes = remember {
        mutableIntStateOf(0)
    }

    if(outboundFlight.value){
        if(outboundDirect.value){
            repeatTimes.intValue = 1
        }
        else{
            repeatTimes.intValue = 2
        }
    }
    else{
        if(inboundDirect.value){
            repeatTimes.intValue = 1
        }
        else{
            repeatTimes.intValue = 2
        }
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
                                if (outboundFlight.value) "Outbound" else "Inbound",
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
                                    text = if (outboundFlight.value)
                                        "${duration.value}, " + if (outboundDirect.value) "nonstop" else "one-stop"
                                    else
                                        "${duration.value}, " + if (inboundDirect.value) "nonstop" else "one-stop",
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
                        if((outboundFlight.value && !outboundDirect.value) || (!outboundFlight.value && !inboundDirect.value)) {
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
                                text =  if(outboundFlight.value)
                                            "${flightsMyBooking.value[index]?.flightDate?.value}"
                                        else if(!outboundFlight.value && outboundDirect.value)
                                            "${flightsMyBooking.value[index+1]?.flightDate?.value}"
                                        else
                                            "${flightsMyBooking.value[index+2]?.flightDate?.value}",
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
                            text = if(outboundFlight.value)
                                    "${flightsMyBooking.value[index]?.departureTime?.value}     ${flightsMyBooking.value[index]?.departureCity?.value} (${flightsMyBooking.value[index]?.departureAirp?.value})"
                                  else if(!outboundFlight.value && outboundDirect.value)
                                    "${flightsMyBooking.value[index+1]?.departureTime?.value}     ${flightsMyBooking.value[index+1]?.departureCity?.value} (${flightsMyBooking.value[index+1]?.departureAirp?.value})"
                                  else
                                    "${flightsMyBooking.value[index+2]?.departureTime?.value}     ${flightsMyBooking.value[index+2]?.departureCity?.value} (${flightsMyBooking.value[index+2]?.departureAirp?.value})",
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
                                    text =  if(outboundFlight.value)
                                                "${flightsMyBooking.value[index]?.flightDuration?.value}"
                                            else if(!outboundFlight.value && outboundDirect.value)
                                                "${flightsMyBooking.value[index+1]?.flightDuration?.value}"
                                            else
                                                "${flightsMyBooking.value[index+2]?.flightDuration?.value}",
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
                            text =  if(outboundFlight.value)
                                        "${flightsMyBooking.value[index]?.arrivalTime?.value}     ${flightsMyBooking.value[index]?.arrivalCity?.value} (${flightsMyBooking.value[index]?.arrivalAirp?.value})"
                                    else if(!outboundFlight.value && outboundDirect.value)
                                        "${flightsMyBooking.value[index+1]?.arrivalTime?.value}     ${flightsMyBooking.value[index+1]?.arrivalCity?.value} (${flightsMyBooking.value[index+1]?.arrivalAirp?.value})"
                                    else
                                        "${flightsMyBooking.value[index+2]?.arrivalTime?.value}     ${flightsMyBooking.value[index+2]?.arrivalCity?.value} (${flightsMyBooking.value[index+2]?.arrivalAirp?.value})",
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
                                text = if (outboundFlight.value)
                                    "${flightsMyBooking.value[index]?.flightId?.value}"
                                else if (!outboundFlight.value && outboundDirect.value)
                                    "${flightsMyBooking.value[index + 1]?.flightId?.value}"
                                else
                                    "${flightsMyBooking.value[index + 2]?.flightId?.value}",
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
                                text = if (outboundFlight.value)
                                    "${flightsMyBooking.value[index]?.airplaneModel?.value}"
                                else if (!outboundFlight.value && outboundDirect.value)
                                    "${flightsMyBooking.value[index + 1]?.airplaneModel?.value}"
                                else
                                    "${flightsMyBooking.value[index + 2]?.airplaneModel?.value}",
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
                                text = if (outboundFlight.value)
                                    "${flightsMyBooking.value[index]?.classType?.value}"
                                else if (!outboundFlight.value && outboundDirect.value)
                                    "${flightsMyBooking.value[index + 1]?.classType?.value}"
                                else
                                    "${flightsMyBooking.value[index + 2]?.classType?.value}",
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

//converts the date to string format of EEEE d MMMM yyyy
@RequiresApi(Build.VERSION_CODES.O)
fun dateToString(dateInNums: MutableState<String>, dateInWords: MutableState<String>) {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val outputFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ENGLISH)

    try {
        // Parse the input date string to a LocalDate object
        val date = LocalDate.parse(dateInNums.value, inputFormatter)

        // Format the LocalDate object to the desired output string format
        dateInWords.value = date.format(outputFormatter)

        Log.d("dateInWords", dateInWords.value)
    } catch (e: Exception) {
        Log.d("Invalid date format", dateInNums.value)
    }
}

//this function finds the total hours and minutes for flights with one stop that is the adding
//of the flight duration of the first flight + flight duration of the second flight
//+ (time to depart the second flight - time to arrive the first flight
@RequiresApi(Build.VERSION_CODES.O)
fun findTotalHoursMinutesMyBooking(flight1: BasicFlight?, flight2: BasicFlight?): Pair<Int,Int> {
    val time1 = flight1!!.arrivalTime
    val time2 = flight2?.departureTime

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val localTime1 = LocalTime.parse(time1.value, formatter)
    val localTime2 = LocalTime.parse(time2!!.value, formatter)
    val (hour3, minutes3) = parseTime(flight1.flightDuration.value)
    val (hour4, minutes4) = parseTime(flight2.flightDuration.value)

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

