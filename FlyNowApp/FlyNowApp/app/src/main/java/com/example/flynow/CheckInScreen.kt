package com.example.flynow

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
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

//screen that shows the check in and the parameters are:
//navController to navigate backward or forward to other pages,
//bookingIdCheckIn is the booking reference of the reservation,
//directFlight if true the flight is direct else the flight has one stop,
//flightsCheckIn is list with the flights available to check in,
//passengersCheckIn is list with the passengers of the flights,
//numOfPassengersCheckIn is the number of passengers in each flight,
//petSizeCheckIn is the size of the pet,
//wifiOnBoardCheckIn is the type of wifi selected if exists,
//baggageAndSeatCheckIn is list with the baggage and seats of each passenger in each flight
@Composable
fun CheckInScreen(navController: NavController,
                  bookingIdCheckIn: MutableState<String>,
                  directFlight: MutableState<Boolean>,
                  flightsCheckIn: MutableState<SnapshotStateList<BasicFlight?>>,
                  passengersCheckIn: MutableState<SnapshotStateList<PassengerInfo?>>,
                  numOfPassengersCheckIn: MutableIntState,
                  petSizeCheckIn: MutableState<String>,
                  wifiOnBoardCheckIn: MutableIntState,
                  baggageAndSeatCheckIn: MutableState<SnapshotStateList<BaggageAndSeatPerPassenger?>>){
    //variables for two text inputs "Booking reference" and "Last name"
    val textBookingId = remember {
        mutableStateOf("")
    }
    val textLastname = remember {
        mutableStateOf("")
    }
    //button clicked to see if some input are blank or not
    val buttonClicked = remember {
        mutableStateOf(false)
    }
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val bookingExists = remember {
        mutableStateOf(false)
    }
    val checkInOpen = remember {
        mutableStateOf(true)
    }
    val checkBooking = remember {
        mutableStateOf(false)
    }
    //error from the query that searches for the booking with the booking reference and lastname
    val hasError = remember {
        mutableStateOf(false)
    }
    val queryEnded = remember {
        mutableStateOf(false)
    }
    val ctx = LocalContext.current
    val circularProgress = remember {
        mutableStateOf(false)
    }

    //api for checking the booking if exists and continue to other routes
    LaunchedEffect(checkBooking.value) {
        if (checkBooking.value) {
            val url = "http://100.106.205.30:5000/flynow/check-booking"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("lastname", textLastname.value)
            jsonObject.put("bookingid", textBookingId.value)
            jsonArray.put(jsonObject)
            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    bookingExists.value = response.getJSONObject(0).getBoolean("success")
                    if (!bookingExists.value){
                        hasError.value = true
                    }
                    Log.d("response", response.getJSONObject(0).getBoolean("success").toString())
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
            checkBooking.value = false
        }
    }

    //api that takes all the information for the booking
    //flights, passengers, seats, baggage wifi, and pets
    LaunchedEffect(bookingExists.value) {
        if(bookingExists.value) {
            val url = "http://100.106.205.30:5000/flynow/checkin-details"

            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", textBookingId.value)
            jsonArray.put(jsonObject)
            Log.d("response", textBookingId.value)
            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    if(response.getJSONObject(0).getInt("numofflightstocheckin") == 0){
                        Log.d("response", "check in later")
                        checkInOpen.value = false
                    }
                    else{
                        Log.d("response", "flights for checkin")
                        checkInOpen.value = true
                        circularProgress.value = true
                    }

                    //flights
                    Log.d("response" , "getflights")
                    directFlight.value = response.getJSONObject(1).getBoolean("direct")
                    Log.d("response" , directFlight.value.toString())
                    val checkInFlights  = parseFlightsJson(response.getJSONObject(2).getJSONArray("flights").toString())
                    Log.d("response" , checkInFlights.toString())
                    if(checkInFlights != null){
                        flightsCheckIn.value = checkInFlights
                    }
                    else{
                        Log.d("response" , "null")
                    }
                    Log.d("response" , flightsCheckIn.value[0]?.flightDate?.value.toString())
                    //baggage
                    val bookingBaggageSeat = parseBaggageAndSeatPerPassengerJson(response.getJSONObject(3).getJSONArray("baggagePerPassenger").toString())
                    if(bookingBaggageSeat != null){
                        baggageAndSeatCheckIn.value = bookingBaggageSeat
                    }
                    Log.d("response", bookingBaggageSeat?.get(0)?.lastname.toString())

                    numOfPassengersCheckIn.intValue = response.getJSONObject(4).getInt("numofpassengers")
                    Log.d("response", numOfPassengersCheckIn.intValue.toString())
                    //passengers
                    val checkInPassengers = parsePassengersJson(response.getJSONObject(6).getJSONArray("passengers").toString())
                    if(checkInPassengers != null){
                        passengersCheckIn.value = checkInPassengers
                    }
                    //pet
                    petSizeCheckIn.value = response.getJSONObject(7).getString("petsize")
                    Log.d("response", petSizeCheckIn.value)
                    //wifi
                    wifiOnBoardCheckIn.intValue = response.getJSONObject(8).getInt("wifionboard")
                    Log.d("response", wifiOnBoardCheckIn.intValue.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(5000)
            bookingExists.value = false
            queryEnded.value = true
        }
    }

    if(!bookingExists.value && checkInOpen.value && queryEnded.value){
        bookingIdCheckIn.value = textBookingId.value
        //navigates to the next page to show the checkin details
        navController.navigate(CheckInDetails.route) {
                popUpTo(CheckIn.route)
            launchSingleTop = true
        }
        queryEnded.value = false
    }


    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                //clicking the back icon to go back in the previous page
                navController.navigate(More.route) {
                    popUpTo(CheckIn.route)
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
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 2.5.dp)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.checkin),
                contentDescription = "pet",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 3f)
                    .padding(start = 10.dp, top = 5.dp, end = 10.dp)
            )
            if(!circularProgress.value) {
                //Text input "Booking reference"
                OutlinedTextField(
                    value = textBookingId.value,
                    onValueChange = {
                        textBookingId.value = it
                        buttonClicked.value = false
                        hasError.value = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, start = 10.dp, end = 10.dp),
                    label = { Text("Booking reference", fontSize = 16.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color(0xFF023E8A),
                        focusedBorderColor = Color(0xFF023E8A),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFF00B4D8),
                        errorContainerColor = Color.White,
                        cursorColor = Color(0xFF023E8A)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AirplaneTicket,
                            contentDescription = "bookingId",
                            tint = Color(0xFF00B4D8)
                        )
                    },
                    isError = (buttonClicked.value && textBookingId.value == "" || hasError.value)
                )
                //Text input "Last name"
                OutlinedTextField(
                    value = textLastname.value,
                    onValueChange = {
                        textLastname.value = it
                        buttonClicked.value = false
                        hasError.value = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                    label = { Text("Last name", fontSize = 16.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color(0xFF023E8A),
                        focusedBorderColor = Color(0xFF023E8A),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFF00B4D8),
                        errorContainerColor = Color.White,
                        cursorColor = Color(0xFF023E8A)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.passenger),
                            contentDescription = "lastname",
                            tint = Color(0xFF00B4D8)
                        )
                    },
                    isError = (buttonClicked.value && textLastname.value == "" || hasError.value)
                )
                //if there is an error with the searching of the booking throws an error and put it in a text
                if (hasError.value) {
                    Text(
                        text = "Incorrect booking reference or lastname. Please check your details and try again.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp),
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
                //if the check-in is not available yet throws an error message
                if (!checkInOpen.value) {
                    Text(
                        text = "Check-in is available 24 hours to 30 minutes before your departure.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp),
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

                //button to go to the next page
                ElevatedButton(
                    onClick = {
                        buttonClicked.value = true
                        checkBooking.value = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(top = 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp
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
            else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 100.dp),
                        color = Color(0xFF023E8A)
                    )
                }
            }
        }
    }
}
