//In this screen the user of the app can search for a flight with more details
package com.example.flynow

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

//navController helps to navigate to previous page or next page,
//airportFrom and airportTo is in what variable will be stored the airport that is selected,
//whatAirport shows if the click from previous page has come from
//airportTo or airportFrom, rentCar shows if the previous page was "rent a car"
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BookScreen(
    navController: NavController,
    pageNow: MutableIntState,
    airportFrom: MutableState<String>,
    airportTo: MutableState<String>,
    whatAirport: MutableIntState,
    rentCar: MutableState<Boolean>,
    oneWayDirectFlights: MutableState<SnapshotStateList<DirectFlight?>>,
    returnDirectFlights: MutableState<SnapshotStateList<DirectFlight?>>,
    oneWayOneStopFlights: MutableState<SnapshotStateList<OneStopFlight?>>,
    returnOneStopFlights: MutableState<SnapshotStateList<OneStopFlight?>>,
    departureDate: MutableState<String>,
    returnDate: MutableState<String>,
    passengersCounter: MutableIntState,
    listOfClassButtonsOutbound: MutableList<ReservationType>,
    listOfClassButtonsInbound: MutableList<ReservationType>
) {
    //helps for horizontal scrolling between two pages "One-way Trip" and "Round Trip"
    val pagerState = rememberPagerState(pageCount = 2,
        initialPage = pageNow.intValue)
    val coroutineScope = rememberCoroutineScope()
    val list = listOf(
        "One-Way Trip",
        "Round Trip"
    )
    //for switch button
    val checked = remember {
        mutableStateOf(false)
    }
    //if the button clicked to see if some textfields are empty
    val buttonClicked = remember {
        mutableStateOf(false)
    }
    //check boxes about am or pm flights
    val amChecked = remember {
        mutableStateOf(false)
    }
    val pmChecked = remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        //title for the screen
        Text(
            text = "Book a flight",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 5.dp),
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
        //tabs for two pages one way or round trip
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color = Color(0xFF023E8A)
                )
            }
        ) {
            list.forEachIndexed { index, listItem ->
                pageNow.intValue = pagerState.currentPage
                Tab(
                    text = { Text(
                        listItem,
                        color = if (pagerState.currentPage == index) Color(0xFF023E8A) else Color(0x99023E8A),
                        fontSize = 16.sp
                    )},
                    selected = pagerState.currentPage == index,
                    onClick = {
                        if(pagerState.currentPage!=index) {
                            departureDate.value=""
                            returnDate.value=""
                        }
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        //horizontal scrolling between two pages and showing some different fields each time
        HorizontalPager(state = pagerState) { page ->
            departureDate.value=""
            returnDate.value=""
            when (page) {
                0 -> ShowInputFields(
                    navController = navController,
                    pageNow = pageNow,
                    airportFrom = airportFrom,
                    airportTo = airportTo, whatAirport = whatAirport, page = 0,
                    departureDate = departureDate, returnDate = returnDate, checked = checked,
                    passengersCounter = passengersCounter, buttonClicked = buttonClicked,
                    amChecked = amChecked, pmChecked = pmChecked, rentCar = rentCar,
                    oneWayDirectFlights = oneWayDirectFlights,
                    returnDirectFlights = returnDirectFlights,
                    oneWayOneStopFlights = oneWayOneStopFlights,
                    returnOneStopFlights = returnOneStopFlights,
                    listOfClassButtonsOutbound = listOfClassButtonsOutbound,
                    listOfClassButtonsInbound = listOfClassButtonsInbound)
                1 -> ShowInputFields(
                    navController = navController,
                    pageNow = pageNow,
                    airportFrom = airportFrom,
                    airportTo = airportTo, whatAirport = whatAirport, page = 1,
                    departureDate = departureDate, returnDate = returnDate, checked = checked,
                    passengersCounter = passengersCounter, buttonClicked = buttonClicked,
                    amChecked = amChecked, pmChecked = pmChecked, rentCar = rentCar,
                    oneWayDirectFlights = oneWayDirectFlights,
                    returnDirectFlights = returnDirectFlights,
                    oneWayOneStopFlights = oneWayOneStopFlights,
                    returnOneStopFlights = returnOneStopFlights,
                    listOfClassButtonsOutbound = listOfClassButtonsOutbound,
                    listOfClassButtonsInbound = listOfClassButtonsInbound)
            }
        }
    }
}

//pass the arguments for main function above and different variables and addition the page 0 or 1
//One-Way trip or Round Trip page
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ShowInputFields(
    navController: NavController,
    pageNow: MutableIntState,
    airportFrom: MutableState<String>,
    airportTo: MutableState<String>,
    whatAirport: MutableIntState,
    page: Int,
    departureDate: MutableState<String>,
    returnDate: MutableState<String>,
    checked: MutableState<Boolean>,
    passengersCounter: MutableIntState,
    buttonClicked: MutableState<Boolean>,
    amChecked: MutableState<Boolean>,
    pmChecked: MutableState<Boolean>,
    rentCar: MutableState<Boolean>,
    oneWayDirectFlights: MutableState<SnapshotStateList<DirectFlight?>>,
    returnDirectFlights: MutableState<SnapshotStateList<DirectFlight?>>,
    oneWayOneStopFlights: MutableState<SnapshotStateList<OneStopFlight?>>,
    returnOneStopFlights: MutableState<SnapshotStateList<OneStopFlight?>>,
    listOfClassButtonsOutbound: MutableList<ReservationType>,
    listOfClassButtonsInbound: MutableList<ReservationType>
) {

    var temp = ""
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val ctx = LocalContext.current
    val makeQuery = remember {
        mutableStateOf(false)
    }

    //api for taking flights from the database and store the data to the variables
    //post request to send data that select in the book screen the user and make
    //the queries accordingly to the variables
    LaunchedEffect(makeQuery.value) {
        if(makeQuery.value) {
            val url = "http://100.106.205.30:5000/flynow/flights"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val details = JSONObject()

            details.put("from", airportFrom.value)
            details.put("to", airportTo.value)
            details.put("departureDate",departureDate.value)
            details.put("returnDate",returnDate.value)
            details.put("directFlights",checked.value)
            details.put("amFlights",amChecked.value)
            details.put("pmFlights",pmChecked.value)
            details.put("passengersCount",passengersCounter.intValue)
            jsonArray.put(details)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    //store the data to lists
                    val oneWayDirect = parseDirectJson(response.getJSONObject(0).get("oneWayDirectResult").toString())
                    if (oneWayDirect != null) {
                        oneWayDirectFlights.value = oneWayDirect
                    }
                    else {
                        oneWayDirectFlights.value = mutableStateListOf()
                    }
                    val oneWayOneStop = parseOneStopJson(response.getJSONObject(1).get("oneWayOneStopResult").toString())
                    if (oneWayOneStop != null) {
                        oneWayOneStopFlights.value = oneWayOneStop
                    }
                    else {
                        oneWayOneStopFlights.value = mutableStateListOf()
                    }
                    val returnDirect = parseDirectJson(response.getJSONObject(2).get("returnDirectResult").toString())
                    if (returnDirect != null) {
                        returnDirectFlights.value = returnDirect
                    }
                    else {
                        returnDirectFlights.value = mutableStateListOf()
                    }
                    val returnOneStop = parseOneStopJson(response.getJSONObject(3).get("returnOneStopResult").toString())
                    if (returnOneStop != null) {
                        returnOneStopFlights.value = returnOneStop
                    }
                    else {
                        returnOneStopFlights.value = mutableStateListOf()
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
            //after the query navigate to the next page
            navController.navigate(Flights.route) {
                popUpTo(Book.route)
                launchSingleTop = true
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(rememberScrollState())
    ) {
        //different fields that is shown on the screen
        //Text input "From"
        OutlinedTextField(
            value = airportFrom.value,
            onValueChange = { airportFrom.value = it
                buttonClicked.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                .clickable(enabled = false, onClickLabel = null, onClick = {}),
            label = { Text("From", fontSize = 16.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color(0xFF023E8A),
                focusedBorderColor = Color(0xFF023E8A),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFF00B4D8),
                errorContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            leadingIcon = {
                //selecting airport about clicking the icon
                IconButton(onClick = {
                    whatAirport.intValue = 0
                    rentCar.value = false
                    pageNow.intValue = page
                    navController.navigate(Airports.route) {
                        popUpTo(Book.route)
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        painterResource(id = R.drawable.takeoff),
                        contentDescription = "takeOff",
                        tint = Color(0xFF00B4D8)
                    )
                }
            },
            textStyle = TextStyle.Default.copy(fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            ),
            readOnly = true,
            isError = (buttonClicked.value && airportFrom.value == "")
        )
        //Text input "To"
        OutlinedTextField(
            value = airportTo.value,
            onValueChange = { airportTo.value = it
                buttonClicked.value = false},
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 5.dp, end = 10.dp)
                .clickable(enabled = false, onClickLabel = null, onClick = {}),
            label = { Text("To", fontSize = 16.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color(0xFF023E8A),
                focusedBorderColor = Color(0xFF023E8A),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFF00B4D8),
                errorContainerColor = Color.White
            ),
            leadingIcon = {
                //selecting airport about clicking the icon
                IconButton(onClick = {
                    whatAirport.intValue = 1
                    rentCar.value = false
                    pageNow.intValue = page
                    navController.navigate(Airports.route) {
                        popUpTo(Book.route)
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        painterResource(id = R.drawable.landon),
                        contentDescription = "landon",
                        tint = Color(0xFF00B4D8)
                    )
                }
            },
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
            readOnly = true,
            isError = (buttonClicked.value && airportTo.value == "")
        )
        DatePickerDialog(page, departureDate, returnDate, buttonClicked)
        //Text input "Passengers"
        OutlinedTextField(
            value = temp,
            onValueChange = {temp = it},
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 5.dp, end = 10.dp)
                .clickable(enabled = false, onClickLabel = null, onClick = {}),
            label = { Text(if(passengersCounter.intValue == 1) "1 Passenger" else "${passengersCounter.intValue} Passengers", fontSize = 16.sp) },
            singleLine = true,
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color(0xFF023E8A),
                focusedBorderColor = Color(0xFF023E8A),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFF00B4D8),
                errorContainerColor = Color.White
            ),
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.passenger),
                    contentDescription = "passengers",
                    tint = Color(0xFF00B4D8)
                )
            },
            trailingIcon = {
                //two buttons that increase or decrease the passengers
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    IconButton(onClick = {
                        if (passengersCounter.intValue > 1) {
                            passengersCounter.intValue--
                        }
                    }) {
                        Icon(
                            Icons.Outlined.RemoveCircleOutline,
                            contentDescription = "remove",
                            tint = Color(0xFF00B4D8),
                            modifier = Modifier.padding(start = 30.dp)
                        )
                    }
                    IconButton(onClick = {
                        if(passengersCounter.intValue < 10) {
                            passengersCounter.intValue++
                        }
                    }) {
                        Icon(
                            painterResource(id = R.drawable.add),
                            contentDescription = "add",
                            tint = Color(0xFF00B4D8),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            },
            textStyle = TextStyle.Default.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        )
        //Switch button "Direct Flights"
        Row {
            Switch(
                checked = checked.value,
                onCheckedChange = {
                    checked.value = it
                    if(!checked.value) {
                        amChecked.value = false
                        pmChecked.value = false
                    }
                },
                modifier = Modifier
                    .padding(top = 20.dp, start = 10.dp),
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = Color(0xFF00B4D8),
                    uncheckedThumbColor = Color(0xFF00B4D8),
                    checkedTrackColor = Color(0xFF00B4D8)
                )
            )
            Text(
                "Direct flights",
                fontSize = 16.sp,
                modifier = Modifier.padding(top= 32.dp,start = 5.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
            //Checkboxes "AM Flights" and "PM flights"
            Row(
                horizontalArrangement = Arrangement.End) {
                Column {
                    Row {
                        Checkbox(checked = amChecked.value,
                            onCheckedChange = { isChecked -> amChecked.value = isChecked },
                            modifier = Modifier
                                .height(36.dp)
                                .width(50.dp)
                                .padding(top = 30.dp, start = 30.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00B4D8),
                                uncheckedColor = Color(0xFF00B4D8)
                            ),
                            enabled = checked.value
                        )
                        Text(
                            "AM flights (00:00-11:59)",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp,top = 23.dp),
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    }
                    Row {
                        Checkbox(checked = pmChecked.value,
                            onCheckedChange = { isChecked -> pmChecked.value = isChecked },
                            modifier = Modifier
                                .height(30.dp)
                                .width(50.dp)
                                .padding(top = 10.dp, start = 30.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00B4D8),
                                uncheckedColor = Color(0xFF00B4D8)
                            ),
                            enabled = checked.value
                        )
                        Text(
                            "PM flights (12:00-23:59)",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp,top = 9.dp),
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
        //Button "Search Flights" to continue to the next page
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            ElevatedButton(onClick = {
                //go to Flights Screen
                listOfClassButtonsOutbound.clear()
                listOfClassButtonsInbound.clear()
                if(airportFrom.value=="" || airportTo.value=="" || departureDate.value=="") {
                    buttonClicked.value = true
                }
                else {
                    makeQuery.value = true
                } },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(top = 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Text(text = "Search Flights",
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

//function that parse the data from the database and storing them to a list
//that is a type of DirectFlight data class
private fun parseDirectJson(jsonString: String): SnapshotStateList<DirectFlight?>? {
    if(jsonString != "null" && jsonString != "[]") {
        val jsonArray = JSONArray(jsonString)
        val flights = mutableStateListOf<DirectFlight?>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val flightId = jsonObject.getString("flightid")
            val flightDate = jsonObject.getString("flightdate")
            val departureTime = jsonObject.getString("departuretime")
            val arrivalTime = jsonObject.getString("arrivaltime")
            val economyPrice = jsonObject.getDouble("economyprice")
            val flexPrice = jsonObject.getDouble("flexprice")
            val businessPrice = jsonObject.getDouble("businessprice")
            val departureAirport = jsonObject.getString("departureairport")
            val arrivalAirport = jsonObject.getString("arrivalairport")
            val departureCity = jsonObject.getString("departurecity")
            val arrivalCity = jsonObject.getString("arrivalcity")
            val airplaneModel = jsonObject.getString("airplanemodel")
            val flightDuration = jsonObject.getString("flightduration")

            val flight = DirectFlight(
                flightId, flightDate, departureTime,
                arrivalTime, economyPrice, flexPrice, businessPrice, departureAirport,
                arrivalAirport, departureCity, arrivalCity, airplaneModel, flightDuration
            )
            flights.add(flight)
        }
        return flights
    }
    return null
}

//function that parse the data from the database and storing them to a list
//that is a type of OneStopFlight data class
private fun parseOneStopJson(jsonString: String): SnapshotStateList<OneStopFlight?>? {
    if(jsonString != "null" && jsonString != "[]") {
        val jsonArray = JSONArray(jsonString)
        val flights = mutableStateListOf<OneStopFlight?>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val firstFlightId = jsonObject.getString("first_flightid")
            val firstFlightDate = jsonObject.getString("first_flightdate")
            val firstDepartureTime = jsonObject.getString("first_flight_departuretime")
            val firstArrivalTime = jsonObject.getString("first_flight_arrivaltime")
            val firstEconomyPrice = jsonObject.getDouble("first_flight_economyprice")
            val firstFlexPrice = jsonObject.getDouble("first_flight_flexprice")
            val firstBusinessPrice = jsonObject.getDouble("first_flight_businessprice")
            val firstDepartureAirport = jsonObject.getString("first_flight_departureairport")
            val firstArrivalAirport = jsonObject.getString("first_flight_arrivalairport")
            val firstDepartureCity = jsonObject.getString("first_flight_departurecity")
            val firstArrivalCity = jsonObject.getString("first_flight_arrivalcity")
            val firstAirplaneModel = jsonObject.getString("first_flight_airplanemodel")
            val firstFlightDuration = jsonObject.getString("first_flightduration")

            val secondFlightId = jsonObject.getString("second_flightid")
            val secondFlightDate = jsonObject.getString("second_flightdate")
            val secondDepartureTime = jsonObject.getString("second_flight_departuretime")
            val secondArrivalTime = jsonObject.getString("second_flight_arrivaltime")
            val secondEconomyPrice = jsonObject.getDouble("second_flight_economyprice")
            val secondFlexPrice = jsonObject.getDouble("second_flight_flexprice")
            val secondBusinessPrice = jsonObject.getDouble("second_flight_businessprice")
            val secondDepartureAirport = jsonObject.getString("second_flight_departureairport")
            val secondArrivalAirport = jsonObject.getString("second_flight_arrivalairport")
            val secondDepartureCity = jsonObject.getString("second_flight_departurecity")
            val secondArrivalCity = jsonObject.getString("second_flight_arrivalcity")
            val secondAirplaneModel = jsonObject.getString("second_flight_airplanemodel")
            val secondFlightDuration = jsonObject.getString("second_flightduration")

            val flight = OneStopFlight(
                firstFlightId,
                firstFlightDate,
                firstDepartureTime,
                firstArrivalTime,
                firstEconomyPrice,
                firstFlexPrice,
                firstBusinessPrice,
                firstDepartureAirport,
                firstArrivalAirport,
                firstDepartureCity,
                firstArrivalCity,
                firstAirplaneModel,
                firstFlightDuration,
                secondFlightId,
                secondFlightDate,
                secondDepartureTime,
                secondArrivalTime,
                secondEconomyPrice,
                secondFlexPrice,
                secondBusinessPrice,
                secondDepartureAirport,
                secondArrivalAirport,
                secondDepartureCity,
                secondArrivalCity,
                secondAirplaneModel,
                secondFlightDuration
            )
            flights.add(flight)
        }
        return flights
    }
    return null
}

//converts the milliseconds to date format that is shown below
@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

//function that shows the date picker and select a date the user,
//range of dates with return or one date for one-way
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    page: Int,
    onSelectedDate: (String) -> Unit,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val utcDate = convertMillisToDate(utcTimeMillis)
            val currentDate = convertMillisToDate(System.currentTimeMillis())

            return (((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                    &&
                    utcDate.substring(0,2).toInt() >= currentDate.substring(0,2).toInt()
                    ||
                    utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt()
                    )
                    &&
                    utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt()
                    )
                    ||
                    utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt())
        }
    })

    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val utcDate = convertMillisToDate(utcTimeMillis)
            val currentDate = convertMillisToDate(System.currentTimeMillis())

            return (((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                    &&
                    utcDate.substring(0,2).toInt() >= currentDate.substring(0,2).toInt()
                    ||
                    utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt()
                    )
                    &&
                    utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt()
                    )
                    ||
                    utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt())
        }
    })

    val selectedStartDate = dateRangePickerState.selectedStartDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""
    val selectedEndDate = dateRangePickerState.selectedEndDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    val context = LocalContext.current

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                if(page == 1 && selectedStartDate != "" && selectedEndDate != "") {
                    onStartDateSelected(selectedStartDate)
                    onEndDateSelected(selectedEndDate)
                    onDismiss()
                }
                else if(page == 0){
                    onSelectedDate(selectedDate)
                    onDismiss()
                }
                else {
                    Toast.makeText(context,"You must select a departure and a return date!",Toast.LENGTH_SHORT).show()
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
                Text(text = "OK",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ))
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )) {
                Text(text = "Cancel",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor =  Color(0xFFEBF2FA)
        ),
        modifier = Modifier.scale(scaleX = 0.9f, scaleY = 0.9f)
    ) {
        if(page == 1) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .background(color = Color(0xFFEBF2FA))
                    .height(450.dp)
                    .padding(bottom = 10.dp),
                title = {
                    Text(text = "Select dates to travel",
                        fontSize = 24.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                },
                headline = {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(text = if(dateRangePickerState.selectedStartDateMillis!=null) "$selectedStartDate - " else "Start Date - ",
                            fontSize = 20.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )

                        )
                        Text(text = if(dateRangePickerState.selectedEndDateMillis!=null) selectedEndDate else "End Date",
                            fontSize = 20.sp,
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
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    titleContentColor = Color(0xFF4361EE),
                    headlineContentColor = Color(0xFF4361EE),
                    weekdayContentColor = Color(0xFF023E8A),
                    navigationContentColor = Color(0xFF023E8A),
                    yearContentColor = Color(0xFF023E8A),
                    dayContentColor = Color(0xFF023E8A),
                    todayDateBorderColor = Color(0xFF4361EE),
                    todayContentColor = Color(0xFF023E8A),
                    subheadContentColor = Color(0xFF023E8A),
                    dividerColor = Color(0xFF4361EE),
                    selectedDayContainerColor = Color(0xFF023E8A),
                    selectedYearContainerColor = Color(0xFF023E8A),
                    containerColor = Color(0xFFEBF2FA),
                    currentYearContentColor = Color(0xFF023E8A),
                    dayInSelectionRangeContainerColor = Color(0x55023E8A),
                    dayInSelectionRangeContentColor = Color(0xFF023E8A)
                )
            )
        }
        else {
            DatePicker(
                state = datePickerState,
                modifier = Modifier
                    .background(color = Color(0xFFEBF2FA))
                    .height(500.dp),
                title = {
                    Text(text = "Select date to travel",
                        fontSize = 24.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                },
                headline = {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(text = if(datePickerState.selectedDateMillis!=null) selectedDate else "No Date",
                            fontSize = 20.sp,
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
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    titleContentColor = Color(0xFF4361EE),
                    headlineContentColor = Color(0xFF4361EE),
                    weekdayContentColor = Color(0xFF023E8A),
                    navigationContentColor = Color(0xFF023E8A),
                    yearContentColor = Color(0xFF023E8A),
                    dayContentColor = Color(0xFF023E8A),
                    todayDateBorderColor = Color(0xFF4361EE),
                    todayContentColor = Color(0xFF023E8A),
                    subheadContentColor = Color(0xFF4361EE),
                    dividerColor = Color(0xFF4361EE),
                    selectedDayContainerColor = Color(0xFF023E8A),
                    selectedYearContainerColor = Color(0xFF023E8A),
                    containerColor = Color(0xFFEBF2FA),
                    currentYearContentColor = Color(0xFF023E8A)
                )
            )
        }
    }
}

//function that shows the outlined text field where the user can click the leading icon to
//select date or dates(for return)
@Composable
fun DatePickerDialog(page: Int,
                     departureDate: MutableState<String>,
                     returnDate: MutableState<String>,
                     buttonClicked: MutableState<Boolean>) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = if(page == 1 && departureDate.value!="" && returnDate.value!="") "${departureDate.value} - ${returnDate.value}" else departureDate.value,
        onValueChange = {
            if (page == 0) {
                departureDate.value = it
                buttonClicked.value = false
            } else {
                returnDate.value = it
                buttonClicked.value = false
            }},
        modifier = Modifier
            .width(500.dp)
            .padding(
                start = 10.dp,
                bottom = 5.dp,
                end = 10.dp
            )
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label = { Text(if(page == 0) "Departure" else "Departure - Return", fontSize = 16.sp) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = Color(0xFF023E8A),
            focusedBorderColor = Color(0xFF023E8A),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFF00B4D8),
            errorContainerColor = Color.White
        ),
        leadingIcon = {
            //clicking the icon to see the dates to select
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "calendar",
                    tint = Color(0xFF00B4D8)
                )
            }
        },
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
        readOnly = true,
        supportingText = {
            Text(
                if(page == 0) "Click calendar to select date" else "Click calendar to select departure and return dates",
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        },
        isError = (buttonClicked.value &&
                ((departureDate.value == "" && page == 0) ||
                        ((returnDate.value == "" || departureDate.value == "") && page == 1)))
    )

    //stores in the correct variables the values in according the page that the user is
    if (showDatePicker) {
        DatePicker(
            page,
            onSelectedDate = {if(page == 0) departureDate.value = it},
            onStartDateSelected = { if(page == 1) departureDate.value = it},
            onEndDateSelected = { if(page == 1) returnDate.value = it},
            onDismiss = { showDatePicker = false }
        )
    }
}