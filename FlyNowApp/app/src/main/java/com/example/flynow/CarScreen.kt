package com.example.flynow

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AirplaneTicket
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalAirport
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

//navController helps to navigate to previous page or next page,
//locationToRentCar is in what variable will be stored the airport that is selected,
//whatAirport is initialized to 0,
//rentCar shows if the previous page was "rent a car",
//pickUp and return info is for the renting of car what exactly datetime is booked the car,
//bookingId is in what variable will be stored the booking reference for the next page,
//listOfCars is the list that the cars will be stored from the query in database,
//daysDifference is variable to help calculate the days between the pick up and return of the car,
//to calculate the price of the car per day and finding the total price
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarScreen(navController: NavController,
              rentCar: MutableState<Boolean>,
              locationToRentCar: MutableState<String>,
              whatAirport: MutableIntState,
              pickUpDateCar: MutableState<String>,
              pickUpHour: MutableState<String>,
              pickUpMins: MutableState<String>,
              returnDateCar: MutableState<String>,
              returnHour: MutableState<String>,
              returnMins: MutableState<String>,
              bookingId: MutableState<String>,
              listOfCars: MutableList<CarDetails>,
              daysDifference: MutableIntState) {
    //variables that help to know in function for what cause the function is called
    val pickUpHourBool = remember {
        mutableStateOf(true)
    }
    val returnHourBool = remember {
        mutableStateOf(false)
    }
    val buttonClicked = remember { //helps from see if the button is clicked and there is some error
        mutableStateOf(false)
    }
    val pickUpbool = remember {
        mutableStateOf(true)
    }
    val returnbool = remember {
        mutableStateOf(false)
    }
    val hourbool = remember {
        mutableStateOf(true)
    }
    val minbool = remember {
        mutableStateOf(false)
    }
    //to see if the dates are the same,to check the pick up
    //and return datetime to be after the return from the pick up
    val timeError = remember {
        mutableStateOf(false)
    }
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    //is the variable that checks if the booking exists
    val bookingExists = remember {
        mutableStateOf(false)
    }
    //variables that shows if an error has come from the booking id or arrival airport
    val bookingError = remember {
        mutableStateOf(false)
    }
    val airportError = remember {
        mutableStateOf(false)
    }
    val rentingTimeError = remember {
        mutableStateOf(false)
    }
    val ctx = LocalContext.current
    //variable to start the query
    val searchCarsQuery = remember {
        mutableStateOf(false)
    }

    //api for checking if the booking exists and does not have any error
    //about airport, booking id and pick-up/return time of the car
    LaunchedEffect(bookingExists.value) {
        if(bookingExists.value) {
            val url = "http://100.106.205.30:5000/flynow/car-booking-exists"
            val queue: RequestQueue = Volley.newRequestQueue(ctx)

            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingId", bookingId.value)
            jsonObject.put("location", locationToRentCar.value)
            jsonObject.put("pickUpDate", pickUpDateCar.value)
            val pickUpHours = pickUpHour.value.toInt()
            jsonObject.put("pickUpHours", pickUpHours)
            val pickUpMinutes = pickUpMins.value.toInt()
            jsonObject.put("pickUpMinutes", pickUpMinutes)

            jsonObject.put("returnDate", returnDateCar.value)
            val returnHours = returnHour.value.toInt()
            jsonObject.put("returnHours", returnHours)
            val returnMinutes = returnMins.value.toInt()
            jsonObject.put("returnMinutes", returnMinutes)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    if(response!=null) {
                        bookingError.value = !response.getJSONObject(0).getBoolean("success")
                        airportError.value = !response.getJSONObject(0).getBoolean("successAirport")
                        rentingTimeError.value = !response.getJSONObject(0).getBoolean("successTime")
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
            delay(500)
            //if is alright continue to searching cars query
            if(!bookingError.value && !airportError.value && !rentingTimeError.value) {
                searchCarsQuery.value = true
            }
            else {
                bookingExists.value = false
            }
        }
    }

    //api that searching cars according to the info type the user
    //and storing them to the listOfCars list and checks the cars in
    //the specific date times that the user select not to be in some reservation
    LaunchedEffect(searchCarsQuery.value) {
        if(searchCarsQuery.value) {
            val url = "http://100.106.205.30:5000/flynow/cars"
            val queue: RequestQueue = Volley.newRequestQueue(ctx)

            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("location", locationToRentCar.value)

            //converts the date, hours and minutes strings to datetime object of pattern "dd/MM/yyyy HH:mm"
            //for the pick up and return
            val pickUpDateComponents = pickUpDateCar.value.split("/")
            val pickUpDay = pickUpDateComponents[0].toInt()
            val pickUpMonth = pickUpDateComponents[1].toInt()
            val pickUpYear = pickUpDateComponents[2].toInt()

            val pickUpHours = pickUpHour.value.toInt()
            val pickUpMinutes = pickUpMins.value.toInt()
            // Create a LocalDateTime object
            val pickUpDateTime = LocalDateTime.of(pickUpYear, pickUpMonth, pickUpDay, pickUpHours, pickUpMinutes)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val pickUpFormattedDateTime = pickUpDateTime.format(formatter)
            jsonObject.put("pickUp", pickUpFormattedDateTime)

            val returnDateComponents = returnDateCar.value.split("/")
            val returnDay = returnDateComponents[0].toInt()
            val returnMonth = returnDateComponents[1].toInt()
            val returnYear = returnDateComponents[2].toInt()

            val returnHours = returnHour.value.toInt()
            val returnMinutes = returnMins.value.toInt()
            // Create a LocalDateTime object
            val returnDateTime = LocalDateTime.of(returnYear, returnMonth, returnDay, returnHours, returnMinutes)
            val returnFormattedDateTime = returnDateTime.format(formatter)
            jsonObject.put("return", returnFormattedDateTime)
            jsonArray.put(jsonObject)
            listOfCars.clear()

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    if(response!=null) {
                        for (i in 0 until response.length()) {
                            listOfCars.add(
                                CarDetails(mutableStateOf(
                                    Bitmap.createBitmap(
                                        400,
                                        400,
                                        Bitmap.Config.ARGB_8888
                                    )
                                ),
                                    mutableStateOf(""),
                                    mutableStateOf(""),
                                    mutableDoubleStateOf(0.00),
                                    mutableIntStateOf(0)

                                )
                            )
                            // Get the base64-encoded string from the response
                            val base64ImageData: String = response.getJSONObject(i).getString("carimage")

                            // Decode the base64 string into a byte array
                            val decodedBytes: ByteArray = Base64.decode(base64ImageData, Base64.DEFAULT)

                            // Convert the byte array to a Bitmap
                            listOfCars[i].carImage.value = byteArrayToBitmap(decodedBytes)
                            //stores the rest of the other variables
                            listOfCars[i].company.value = response.getJSONObject(i).getString("company")
                            listOfCars[i].model.value = response.getJSONObject(i).getString("model")
                            listOfCars[i].price.doubleValue = response.getJSONObject(i).getDouble("price")
                            listOfCars[i].carId.intValue = response.getJSONObject(i).getInt("carid")
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
            searchCarsQuery.value = false
            //navigates to the next page
            navController.navigate(SearchingCars.route) {
                popUpTo(Car.route)
                launchSingleTop = true
            }
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                //back button that returns to more screen and initialization of the variables
                rentCar.value = false
                locationToRentCar.value = ""
                whatAirport.intValue = 0
                pickUpDateCar.value = ""
                pickUpHour.value = "10"
                pickUpMins.value = "30"
                returnDateCar.value = ""
                returnHour.value = "10"
                returnMins.value = "30"
                listOfCars.clear()
                navController.navigate(More.route) {
                    popUpTo(Car.route)
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
                    text = "Rent A Car",
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
                    Icons.Outlined.DirectionsCar,
                    contentDescription = "cars",
                    modifier = Modifier.padding(start = 5.dp, end = 45.dp),
                    tint = Color(0xFF023E8A)
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(rememberScrollState())) {
            //text field for the airport location for the renting of car
            OutlinedTextField(
                value = locationToRentCar.value,
                onValueChange = { locationToRentCar.value = it
                    buttonClicked.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                    .clickable(enabled = false, onClickLabel = null, onClick = {}),
                label = { Text("Location", fontSize = 16.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF023E8A),
                    focusedBorderColor = Color(0xFF023E8A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFF00B4D8),
                    errorContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                leadingIcon = {
                    IconButton(onClick = {
                        //goes to the airports screen to select an airport
                        rentCar.value = true
                        whatAirport.intValue = 0
                        navController.navigate(Airports.route) {
                            popUpTo(Car.route)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            Icons.Outlined.LocalAirport,
                            contentDescription = "location",
                            tint = Color(0xFF00B4D8)
                        )
                    }
                },
                supportingText = {
                    Text("The location must be the arrival airport",
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                },
                textStyle = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )),
                readOnly = true,
                isError = (locationToRentCar.value == "" && buttonClicked.value)
            )
            //if there is an error with the arrival airport of the booking makes a text with the error
            if(airportError.value) {
                Text(
                    text = "Incorrect airport in this booking reference. Please check your details and try again.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp),
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
            //text field for the date and time for pick up and return
            CarDatePickerDialog(pickUp = pickUpbool, pickUpDateCar = pickUpDateCar,  returnDateCar = returnDateCar, date = pickUpDateCar, buttonClicked = buttonClicked, rentingTimeError = rentingTimeError)
            Row {
                Text("Pick Up Time:",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 35.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
                TimeDropdownMenu(time = pickUpHour, pickUpHourBool = pickUpHourBool, returnDateCar = returnDateCar, hourbool, rentingTimeError = rentingTimeError)
                TimeDropdownMenu(time = pickUpMins, pickUpHourBool = pickUpHourBool, returnDateCar = returnDateCar, minbool, rentingTimeError = rentingTimeError)
            }
            CarDatePickerDialog(pickUp = returnbool, pickUpDateCar = pickUpDateCar, returnDateCar = returnDateCar, date = returnDateCar, buttonClicked = buttonClicked, rentingTimeError = rentingTimeError)
            Row {
                Text("Return Time:",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 35.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )
                TimeDropdownMenu(time = returnHour, pickUpHourBool = returnHourBool, returnDateCar = returnDateCar, hourbool, rentingTimeError = rentingTimeError)
                TimeDropdownMenu(time = returnMins, pickUpHourBool = returnHourBool, returnDateCar = returnDateCar, minbool, rentingTimeError = rentingTimeError)
            }
            //error if the return is same or before the pick up if the dates are the same
            if(returnDateCar.value != "" && pickUpDateCar.value == returnDateCar.value && pickUpHour.value >= returnHour.value ) {
                Text(
                    text = "Return time must be after pick up time if the pick up and return day is the same!",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 15.dp, end = 10.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color.Red
                )
                timeError.value = true
            }
            else{
                timeError.value = false
            }
            //error if the rental datetime is not within the departure and return(if there is) flight in an arrival destination
            if(rentingTimeError.value) {
                Text(
                    text = "Τhe rental date and time must be within the limits of your flight!",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 15.dp, end = 10.dp),
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
            //text field for the booking reference
            OutlinedTextField(
                value = bookingId.value,
                onValueChange = { bookingId.value = it
                    buttonClicked.value = false
                    bookingError.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                label = { Text("Booking Reference", fontSize = 16.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF023E8A),
                    focusedBorderColor = Color(0xFF023E8A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFF00B4D8),
                    errorContainerColor = Color.White,
                    cursorColor = Color(0xFF023E8A)
                ),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Outlined.AirplaneTicket,
                        contentDescription = "ticket",
                        tint = Color(0xFF00B4D8)
                    )
                },
                textStyle = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )),
                isError = ((bookingId.value == "" && buttonClicked.value) || bookingError.value)
            )
            //the text for the error of the booking reference
            if (bookingError.value) {
                Text(
                    text = "Incorrect booking reference. Please check your details and try again.",
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
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                //button for continuing to the next page for the cars
                ElevatedButton(onClick = {
                    buttonClicked.value = true
                    if(locationToRentCar.value != "" &&
                        pickUpDateCar.value != "" &&
                        returnDateCar.value != "" &&
                        bookingId.value != "" &&
                        !timeError.value)
                    {
                        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                        val date1 = LocalDate.parse(pickUpDateCar.value, dateFormat)
                        val date2 = LocalDate.parse(returnDateCar.value, dateFormat)

                        daysDifference.intValue = ChronoUnit.DAYS.between(date1, date2).toInt()
                        if(daysDifference.intValue == 0) {
                            daysDifference.intValue = 1
                        }
                        bookingExists.value = true
                    }
                },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(top = 30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Text(text = "Search Cars",
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
}

//function tha converts milliseconds to the date format below
@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

//function tha converts date to the milliseconds format below
private fun convertDateToMillis(dateString: String): Long {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.time ?: 0L
}

//function that creates the calendar for the car date selection for pick up and return
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDatePicker(
    pickUp: MutableState<Boolean>,
    pickUpDateCar: MutableState<String>,
    returnDateCar: MutableState<String>,
    onSelectedDate: (String) -> Unit,
    onDismiss: () -> Unit,
    rentingTimeError: MutableState<Boolean>
) {

    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val utcDate = convertMillisToDate(utcTimeMillis)
                val currentDate = convertMillisToDate(System.currentTimeMillis())

                //pick up and return must have tje maximum difference of ten days
                return (
                        if(pickUpDateCar.value == "" || (pickUp.value && returnDateCar.value=="")){
                            (((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                                    &&
                                    utcDate.substring(0,2).toInt() > currentDate.substring(0,2).toInt()
                                    ||
                                    utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt())
                                    &&
                                    utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt())
                                    ||
                                    utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt())
                        }
                        else{
                            if(!pickUp.value && pickUpDateCar.value != ""){
                                val pickUpDateCarInMillis = convertDateToMillis(pickUpDateCar.value)
                                ((((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                                        &&
                                        utcDate.substring(0,2).toInt() > currentDate.substring(0,2).toInt()
                                        ||
                                        utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt())
                                        &&
                                        utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt())
                                        ||
                                        utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt())
                                        &&
                                        ((utcDate.substring(6,10).toInt() == pickUpDateCar.value.substring(6,10).toInt()
                                        && utcDate.substring(3,5).toInt() == pickUpDateCar.value.substring(3,5).toInt()
                                        && utcDate.substring(0,2).toInt() <= pickUpDateCar.value.substring(0,2).toInt() + 10
                                        && utcDate.substring(0,2).toInt() >= pickUpDateCar.value.substring(0,2).toInt())
                                        || (
                                        utcDate.substring(6,10).toInt() >= pickUpDateCar.value.substring(6,10).toInt()
                                        &&
                                        pickUpDateCar.value.substring(0,2).toInt() >= 19
                                        && utcTimeMillis <= pickUpDateCarInMillis + 10 * 24 * 60 * 60 * 1000
                                        && utcTimeMillis >= pickUpDateCarInMillis))
                                )
                            }
                            else{//if the return date is filled and the pick up date changes
                                var returnDateCarInMillis: Long = 0
                                if(returnDateCar.value!="") {
                                    returnDateCarInMillis = convertDateToMillis(returnDateCar.value)
                                }
                                (((
                                        utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                                        &&
                                        utcDate.substring(0,2).toInt() > currentDate.substring(0,2).toInt()
                                        ||
                                        utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt())
                                        &&
                                        utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt())
                                        ||
                                        utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt()
                                )
                                &&
                                ((
                                        returnDateCar.value !=""
                                        &&
                                        utcDate.substring(3,5).toInt() == returnDateCar.value.substring(3,5).toInt()
                                        &&
                                        utcDate.substring(6,10).toInt() == returnDateCar.value.substring(6,10).toInt()
                                        &&
                                        utcDate.substring(0,2).toInt() <= returnDateCar.value.substring(0,2).toInt()
                                        &&
                                        utcDate.substring(0,2).toInt() >= returnDateCar.value.substring(0,2).toInt() - 10
                                )
                                        ||(
                                        returnDateCar.value!="" &&
                                        utcDate.substring(6,10).toInt() <= returnDateCar.value.substring(6,10).toInt()
                                        &&
                                        returnDateCar.value.substring(0,2).toInt() <= 10
                                        &&
                                        utcTimeMillis >= returnDateCarInMillis - 10 * 24 * 60 * 60 * 1000
                                        &&
                                        utcTimeMillis <= returnDateCarInMillis
                                        ))
                            }

                        }
                )

            }
        }
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    rentingTimeError.value = false
                    onSelectedDate(selectedDate)
                    onDismiss()
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(end = 90.dp),
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
            Button(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
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
            containerColor = Color(0xFFEBF2FA)
        )
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .background(color = Color(0xFFEBF2FA))
                .size(520.dp, 250.dp),
            title = {
                Text(
                    if(pickUp.value) "Select pick up date" else "Select return date",
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            },
            headline = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (datePickerState.selectedDateMillis != null) selectedDate else "No Date",
                        fontSize = 20.sp
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
                currentYearContentColor = Color(0xFF023E8A),
                dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF023E8A),
                    focusedBorderColor = Color(0xFF023E8A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFF4361EE),
                    cursorColor = Color(0xFF023E8A)
                )
            )
        )
    }
}


//function that displays the car date text input with the calendar
@Composable
fun CarDatePickerDialog(
    pickUp: MutableState<Boolean>,
    pickUpDateCar: MutableState<String>,
    returnDateCar: MutableState<String>,
    date: MutableState<String>,
    buttonClicked: MutableState<Boolean>,
    rentingTimeError: MutableState<Boolean>
) {
    val showDatePicker = remember {
        mutableStateOf(false)
    }
    if(pickUpDateCar.value == ""){
        returnDateCar.value = ""
    }
    //the return text field is shown when the pick up is filled
    OutlinedTextField(
        enabled = (pickUp.value && pickUpDateCar.value == "")
                || (!pickUp.value && pickUpDateCar.value != "")
                || (pickUp.value && pickUpDateCar.value != ""),
        value = date.value,
        onValueChange = {
            pickUpDateCar.value = it
            buttonClicked.value = false
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 10.dp,
                top = 10.dp,
                bottom = 5.dp,
                end = 10.dp
            )
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label = {
            Text(
                if(pickUp.value)"Pick Up Date" else "Return Date",
                fontSize = 16.sp)},
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
            IconButton(
                onClick = { showDatePicker.value = true },
                enabled = (pickUp.value && pickUpDateCar.value == "")
                        || (!pickUp.value && pickUpDateCar.value != "")
                        || (pickUp.value && pickUpDateCar.value != "")
            )
            {
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
                text =
                    if(pickUp.value)"Click calendar to select pick up date"
                    else "Click calendar to select return date",
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        },
        isError = (date.value == "" && buttonClicked.value)
    )
    if (showDatePicker.value) {
        CarDatePicker(
            pickUp = pickUp,
            pickUpDateCar = pickUpDateCar,
            returnDateCar = returnDateCar,
            onSelectedDate = {date.value = it},
            onDismiss = { showDatePicker.value = false},
            rentingTimeError = rentingTimeError
        )
    }
}

//function that creates the dropdown menu for the time selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdownMenu(
    time: MutableState<String>,
    pickUpHourBool: MutableState<Boolean>,
    returnDateCar: MutableState<String>,
    hour:  MutableState<Boolean>,
    rentingTimeError: MutableState<Boolean>
) {
    val isExpanded = remember {
        mutableStateOf(false)
    }

    //dropdown list for the hours that are from 00 - 23
    if(hour.value) {
        val hours = remember {
            mutableStateOf(time.value)
        }
        ExposedDropdownMenuBox(
            expanded = isExpanded.value,
            onExpandedChange = { newValue ->
                isExpanded.value = newValue
            },
            modifier = Modifier
                .width(110.dp)
                .padding(top = 17.dp, start = 10.dp, end = 2.dp)
        ) {
            OutlinedTextField(
                enabled  = pickUpHourBool.value || (returnDateCar.value != "" && !pickUpHourBool.value),
                value = hours.value,
                onValueChange = {
                    time.value = hours.value
                },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
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
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = {
                    isExpanded.value = false
                },
                modifier = Modifier
                    .width(90.dp)
                    .background(color = Color.White)
            ) {
                for (i in 0..23) {
                    DropdownMenuItem(
                        text = {
                            Column(modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (i < 10) "0${i}" else "$i",
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
                            hours.value = if (i < 10) "0${i}" else "$i"
                            isExpanded.value = false
                            time.value = hours.value
                            rentingTimeError.value = false
                        },
                        modifier = Modifier.background(color = Color.White)
                    )
                }
            }
        }
    }
    //dropdown list for the minutes that are 00, 15, 30 and 45
    else {
        val minutes = remember {
            mutableStateOf(time.value)
        }
        Text(
            text = ":",
            modifier = Modifier.padding(top = 35.dp, start = 1.dp, end = 1.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        ExposedDropdownMenuBox(
            expanded = isExpanded.value,
            onExpandedChange = { newValue ->
                isExpanded.value = newValue
            },
            modifier = Modifier
                .width(95.dp)
                .padding(top = 17.dp, start = 2.dp, end = 2.dp)
        ) {
            OutlinedTextField(
                enabled  = pickUpHourBool.value || (returnDateCar.value != "" && !pickUpHourBool.value),
                value = minutes.value,
                onValueChange = {
                    time.value = minutes.value
                    rentingTimeError.value = false
                },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
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
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = {
                    isExpanded.value = false
                },
                modifier = Modifier
                    .width(83.dp)
                    .background(color = Color.White)
            ) {
                for (i in 0..45 step 15) {
                    DropdownMenuItem(
                        text = {
                            Column(modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (i < 15) "0${i}" else "$i",
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
                            minutes.value = if (i < 15) "0${i}" else "$i"
                            isExpanded.value = false
                            time.value = minutes.value
                            rentingTimeError.value = false
                        },
                        modifier = Modifier.background(color = Color.White)
                    )
                }
            }
        }
    }
}

//function that converts the byte array to bitmap
private fun byteArrayToBitmap(data: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(data, 0, data.size)
}