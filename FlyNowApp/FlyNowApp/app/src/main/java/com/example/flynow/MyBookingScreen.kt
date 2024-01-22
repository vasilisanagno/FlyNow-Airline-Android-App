package com.example.flynow

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
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

//screen that shows the text fields for finding of one booking
//navController helps to navigate to previous page or next page,
//oneWay, outboundDirect and inboundDirect is to know what type is the flight,
//with return or not and if it is direct or with one stop flight,
//flightsMyBooking, passengersMyBooking are lists that save the info about flights and passengers,
//wifiOnBoard, petSizeMyBooking are variables that save info about pets and wifi,
//numOfPassengers is the number of passengers in the booking,
//baggageAndSeatMyBooking, carsMyBooking are lists that save the info about baggage, seats and cars,
//totalPriceMyBooking, rentingTotalPrice are variables that save the price for the booking and the renting of car
@Composable
fun MyBookingScreen(navController: NavController,
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
    val bookingExists = remember {
        mutableStateOf(false)
    }
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val checkBooking = remember {
        mutableStateOf(false)
    }
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
        if (checkBooking.value && textLastname.value != "" && textBookingId.value != "") {
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
            delay(500)
            checkBooking.value = false
        }
        else {
            checkBooking.value = false
        }
    }

    bookingId.value = textBookingId.value
    //api that takes all the information for the booking
    //flights, passengers, seats, baggage wifi, pets, cars and total price
    LaunchedEffect(bookingExists.value) {
        if(bookingExists.value) {
            val url = "http://100.106.205.30:5000/flynow/booking-details"

            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", bookingId.value)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    //flights
                    Log.d("response" , "getflights")
                    oneWay.value = response.getJSONObject(0).getBoolean("oneway")
                    outboundDirect.value = response.getJSONObject(1).getBoolean("outbounddirect")
                    inboundDirect.value = response.getJSONObject(2).getBoolean("inbounddirect")
                    val bookingFlights  = parseFlightsJson(response.getJSONObject(3).getJSONArray("flights").toString())
                    if(bookingFlights != null){
                        flightsMyBooking.value = bookingFlights
                    }
                    else{
                        Log.d("response" , "null")
                    }
                    Log.d("response" , flightsMyBooking.value[0]?.flightDate?.value.toString())
                    //baggage
                    val bookingBaggageSeat = parseBaggageAndSeatPerPassengerJson(response.getJSONObject(4).getJSONArray("baggagePerPassenger").toString())
                    if(bookingBaggageSeat != null){
                        baggageAndSeatMyBooking.value = bookingBaggageSeat
                    }

                    numOfPassengers.intValue = response.getJSONObject(5).getInt("numofpassengers")
                    //passengers
                    val bookingPassengers = parsePassengersJson(response.getJSONObject(7).getJSONArray("passengers").toString())
                    if(bookingPassengers != null){
                        passengersMyBooking.value = bookingPassengers
                    }

                    //pet
                    petSizeMyBooking.value = response.getJSONObject(8).getString("petsize")

                    //wifi
                    wifiOnBoard.intValue = response.getJSONObject(9).getInt("wifionboard")

                    //cars
                    val cars = parseCarMyBookingJson(response.getJSONObject(10).getJSONArray("cars").toString(), rentingTotalPrice)
                    if(cars != null){
                        carsMyBooking.value = cars
                    }

                    totalPriceMyBooking.doubleValue = response.getJSONObject(11).getDouble("bookingPrice")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            circularProgress.value = true
            delay(5000)
            bookingExists.value = false
            queryEnded.value = true
        }
    }

    if(!bookingExists.value && queryEnded.value){
        //navigates to the next page to show the booking details
        navController.navigate(MyBookingDetails.route) {
            popUpTo(MyBooking.route)
            launchSingleTop = true
        }
        queryEnded.value = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Find your Booking",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
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
                modifier = Modifier.padding(start = 5.dp,top = 10.dp)
            )
        }

        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mybook),
                contentDescription = "baggage",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f)
                    .padding(start = 10.dp, end = 10.dp)
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
                    Text(text = "Continue",
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

//function that parse the info for the passengers and save them in a list that is type of the data class PassengerInfo
fun parsePassengersJson(jsonString: String): SnapshotStateList<PassengerInfo?>? {
    if(jsonString != "null" && jsonString != "[]") {
        val jsonArray = JSONArray(jsonString)
        val passengers = mutableStateListOf<PassengerInfo?>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val gender = mutableStateOf(jsonObject.getString("gender"))
            val firstname= mutableStateOf(jsonObject.getString("firstname"))
            val lastname= mutableStateOf(jsonObject.getString("lastname"))
            val birthdate= mutableStateOf(jsonObject.getString("birthdate"))
            val email= mutableStateOf(jsonObject.getString("email"))
            val phonenumber= mutableStateOf(jsonObject.getString("phonenumber"))
            val passenger = PassengerInfo(gender, firstname, lastname, birthdate, email, phonenumber)
            passengers.add(passenger)
        }
        return passengers
    }
    return null
}

//function that parse the info for the flights and save them in a list that is type of the data class BasicFlight
fun parseFlightsJson(jsonString: String): SnapshotStateList<BasicFlight?>? {
    if(jsonString != "null" && jsonString != "[]") {
        val jsonArray = JSONArray(jsonString)
        val flights = mutableStateListOf<BasicFlight?>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val flightDate = mutableStateOf(jsonObject.getString("flightdate"))
            val departureTime= mutableStateOf(jsonObject.getString("departuretime"))
            val arrivalTime = mutableStateOf(jsonObject.getString("arrivaltime"))
            val departureCity= mutableStateOf(jsonObject.getString("departurecity"))
            val arrivalCity = mutableStateOf(jsonObject.getString("arrivalcity"))
            val flightDuration= mutableStateOf(jsonObject.getString("duration"))
            val departureAirp= mutableStateOf(jsonObject.getString("departureairport"))
            val arrivalAirp = mutableStateOf(jsonObject.getString("arrivalairport"))
            val flightid = mutableStateOf(jsonObject.getString("flightid"))
            val airplaneModel = mutableStateOf(jsonObject.getString("airplanemodel"))
            val classType = mutableStateOf(jsonObject.getString("classtype"))
            val flight = BasicFlight(flightDate, departureTime, arrivalTime, departureCity, arrivalCity, departureAirp, arrivalAirp, flightDuration, flightid, airplaneModel, classType)
            flights.add(flight)
        }
        return flights
    }
    return null
}

//function that parse the info for the seats and baggage
//and save them in a list that is type of the data class BaggageAndSeatPerPassenger
fun parseBaggageAndSeatPerPassengerJson(jsonString: String): SnapshotStateList<BaggageAndSeatPerPassenger?>? {
    if(jsonString != "null" && jsonString != "[]") {
        val jsonArray = JSONArray(jsonString)
        val baggage = mutableStateListOf<BaggageAndSeatPerPassenger?>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val firstname= mutableStateOf(jsonObject.getString("firstname"))
            val lastname= mutableStateOf(jsonObject.getString("lastname"))
            val gender = mutableStateOf(jsonObject.getString("gender"))
            val flightid = mutableStateOf(jsonObject.getString("flightid"))
            val reservationid = mutableStateOf(jsonObject.getString("reservationid"))
            val baggage23kg = mutableIntStateOf(jsonObject.getInt("baggage23kg"))
            val baggage32kg = mutableIntStateOf(jsonObject.getInt("baggage32kg"))
            val seatnumber = mutableStateOf(jsonObject.getString("seatnumber"))
            val departureCity= mutableStateOf(jsonObject.getString("departurecity"))
            val arrivalCity = mutableStateOf(jsonObject.getString("arrivalcity"))
            val checkin = mutableStateOf(jsonObject.getBoolean("checkin"))
            val baggagePerPass = BaggageAndSeatPerPassenger(gender, firstname, lastname, flightid, reservationid, baggage23kg, baggage32kg, seatnumber, departureCity, arrivalCity, checkin)
            baggage.add(baggagePerPass)
        }
        return baggage
    }
    return null
}

//function that parse the info for the cars
//and save them in a list that is type of the data class CarDetailsMyBooking
private fun parseCarMyBookingJson(jsonString: String, rentingTotalPrice: MutableDoubleState): SnapshotStateList<CarDetailsMyBooking?>? {
    if(jsonString != "null" && jsonString != "[]") {
        val jsonArray = JSONArray(jsonString)
        val cars = mutableStateListOf<CarDetailsMyBooking?>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            //Get the base64-encoded string from the response
            val base64ImageData: String = jsonObject.getString("carimage")
            //Decode the base64 string into a byte array
            val decodedBytes: ByteArray = Base64.decode(base64ImageData, Base64.DEFAULT)
            // Convert the byte array to a Bitmap
            val carImage = mutableStateOf(byteArrayToBitmap(decodedBytes))
            val company = mutableStateOf(jsonObject.getString("company"))
            val model = mutableStateOf(jsonObject.getString("model"))
            val price = mutableDoubleStateOf(jsonObject.getDouble("price"))
            val location = mutableStateOf(jsonObject.getString("location"))
            val pickUpDateTime = mutableStateOf(jsonObject.getString("pickup"))
            val returnDateTime = mutableStateOf(jsonObject.getString("return"))
            rentingTotalPrice.doubleValue += jsonObject.getDouble("price")
            val car =
                CarDetailsMyBooking(carImage,
                    company, model, price, location,
                    pickUpDateTime, returnDateTime)
            cars.add(car)
        }
        return cars
    }
    return null
}

//function that converts the byte array to bitmap
private fun byteArrayToBitmap(data: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(data, 0, data.size)
}