package com.example.flynow

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//navController helps to navigate to previous page or next page,
//selectedIndex for the finishing of the renting of car to go back in the home and shows the bottom navigation correctly,
//listOfCars is the list that are stored the cars with their info
//locationToRentCar is the arrival airport that is selected,
//pickUp and return info is for the insert query in the database,
//bookingId is for the insert query in the database,
//daysDifference is variable to help calculate the days between the pick up and return of the car,
//to calculate the price of the car per day and finding the total price
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchingCarsScreen(navController: NavController,
                        selectedIndex: MutableIntState,
                        listOfCars: MutableList<CarDetails>,
                        locationToRentCar: MutableState<String>,
                        pickUpDateCar: MutableState<String>,
                        pickUpHour: MutableState<String>,
                        pickUpMins: MutableState<String>,
                        returnDateCar: MutableState<String>,
                        returnHour: MutableState<String>,
                        returnMins: MutableState<String>,
                        bookingId: MutableState<String>,
                        daysDifference: MutableIntState
) {

    //these two variables are for the failure of the searching, if there are not flights
    val noResults = remember {
        mutableIntStateOf(1)
    }
    val seeBottomBar = remember {
        mutableStateOf(true)
    }
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val totalPriceForCar = remember {
        mutableDoubleStateOf(0.0)
    }
    //buttons boolean values for each car to see what car is selected
    val listOfButtonsCars: MutableList<MutableState<Boolean>> = remember {
        mutableListOf()
    }
    //variables that shows the dialog for the completion of the renting of car
    val showDialog = remember {
        mutableStateOf(false)
    }
    val showDialogConfirm = remember {
        mutableStateOf(false)
    }
    val insertNewBookingOfCar = remember {
        mutableStateOf(false)
    }
    val ctx = LocalContext.current
    if(listOfButtonsCars.size == 0) {
        repeat(listOfCars.size) {
            listOfButtonsCars.add(mutableStateOf(false))
        }
    }

    //api that insert the renting of car
    LaunchedEffect(insertNewBookingOfCar.value) {
        if(insertNewBookingOfCar.value) {
            val url = "http://100.78.116.14:5000/flynow/renting-car"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            for(i in 0 until listOfButtonsCars.size) {
                if(listOfButtonsCars[i].value) {
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

                    jsonObject.put("bookingId", bookingId.value)
                    jsonObject.put("carId", listOfCars[i].carId.intValue)
                    jsonObject.put("price", totalPriceForCar.doubleValue*daysDifference.intValue)
                    break
                }
            }
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
            insertNewBookingOfCar.value = false
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
                        "Total Price: ${totalPriceForCar.doubleValue*daysDifference.intValue} €",
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
                            showDialog.value = true
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
                        enabled = totalPriceForCar.doubleValue != 0.0
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
        //alert dialog for the completion of the renting and go back to the home page
        if (showDialog.value || showDialogConfirm.value) {
            Box(contentAlignment = Alignment.Center) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = {
                        Text(
                            text = if (showDialog.value) "Confirm Renting Of This      Car" else if(!insertNewBookingOfCar.value) "Renting Of Car Added To Your Reservation Successfully!" else "",
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
                        if(!insertNewBookingOfCar.value) {
                            Icon(
                                if (showDialog.value) Icons.Filled.QuestionMark
                                else Icons.Filled.Verified,
                                contentDescription = "question",
                                modifier =
                                if (showDialog.value) Modifier.padding(start = 27.dp, top = 31.dp)
                                else Modifier.padding(top = 66.dp, start = 125.dp),
                                tint = Color(0xFF023E8A)
                            )
                        }
                    },
                    text = {
                        if (showDialog.value && !insertNewBookingOfCar.value) {
                            Text(
                                text = "Are you sure you want to rent this car?",
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
                        if (insertNewBookingOfCar.value) {
                            Column(modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFF023E8A)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        if (showDialog.value && !insertNewBookingOfCar.value) {
                            Button(
                                onClick = {
                                    insertNewBookingOfCar.value = true
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
                            if(!insertNewBookingOfCar.value) {
                                Button(
                                    onClick = {
                                        showDialogConfirm.value = false
                                        showDialog.value = false
                                        selectedIndex.intValue = 0
                                        pickUpDateCar.value = ""
                                        pickUpHour.value = "10"
                                        pickUpMins.value = "30"
                                        returnDateCar.value = ""
                                        returnHour.value = "10"
                                        returnMins.value = "30"
                                        locationToRentCar.value = ""
                                        bookingId.value = ""
                                        daysDifference.intValue = 1
                                        navController.navigate(Home.route) {
                                            popUpTo(SearchingCars.route)
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
                        if (showDialog.value && !insertNewBookingOfCar.value) {
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
                    pickUpDateCar.value = ""
                    pickUpHour.value = "10"
                    pickUpMins.value = "30"
                    returnDateCar.value = ""
                    returnHour.value = "10"
                    returnMins.value = "30"
                    locationToRentCar.value = ""
                    bookingId.value = ""
                    daysDifference.intValue = 1
                    //navigates back to the book page
                    navController.navigate(Car.route) {
                        popUpTo(SearchingCars.route)
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
                        text = "Cars",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //if there is no car show a button that says go back, that the user returns to the rent a car page
                    items(noResults.intValue) {
                        if (listOfCars.size == 0) {
                            seeBottomBar.value = false
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(top = 250.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No Cars Found!",
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
                                        listOfCars.clear()
                                        pickUpDateCar.value = ""
                                        pickUpHour.value = "10"
                                        pickUpMins.value = "30"
                                        returnDateCar.value = ""
                                        returnHour.value = "10"
                                        returnMins.value = "30"
                                        locationToRentCar.value = ""
                                        bookingId.value = ""
                                        daysDifference.intValue = 1
                                        //navigates back to the book page
                                        navController.navigate(Car.route) {
                                            popUpTo(SearchingCars.route)
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
                                    Text(
                                        "Go Back",
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
                    //iterates the returning cars and showing them to cards
                    items(listOfCars.size) { index ->
                        Card(
                            modifier = Modifier
                                .padding(
                                    top = if (index == 0)
                                        20.dp else 30.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom = if (index == listOfCars.size - 1) 80.dp else 0.dp
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
                                        bitmap = listOfCars[index].carImage.value.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(160.dp, 160.dp)
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = listOfCars[index].model.value,
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
                                        text = listOfCars[index].company.value,
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
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                    )
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 25.dp),
                                        contentAlignment = Alignment.CenterEnd) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = locationToRentCar.value,
                                                fontSize = 20.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.gilroy
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp, end = 15.dp),
                                                color = Color(0xFF023E8A)
                                            )
                                            Text(
                                                text = "Price Per Day",
                                                fontSize = 18.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.opensans
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 10.dp, end = 15.dp),
                                                color = Color(0xFF023E8A)
                                            )
                                            Text(
                                                text = "${listOfCars[index].price.doubleValue} €",
                                                fontSize = 18.sp,
                                                fontFamily = FontFamily(
                                                    fonts = listOf(
                                                        Font(
                                                            resId = R.font.lato
                                                        )
                                                    )
                                                ),
                                                modifier = Modifier.padding(top = 3.dp, bottom = 20.dp, end = 15.dp),
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF023E8A)
                                            )
                                            Button(
                                                onClick = {
                                                    if(listOfCars[index].price.doubleValue != 0.0) {
                                                        if(!listOfButtonsCars[index].value) {
                                                            listOfButtonsCars.forEach { carButton ->
                                                                carButton.value = false
                                                            }
                                                            listOfButtonsCars[index].value = true
                                                            totalPriceForCar.doubleValue = listOfCars[index].price.doubleValue
                                                        }
                                                        else {
                                                            listOfButtonsCars[index].value = false
                                                            totalPriceForCar.doubleValue = 0.0
                                                        }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF0096c7)
                                                ),
                                                contentPadding = PaddingValues(
                                                    start = 30.dp,
                                                    end = 30.dp
                                                ),
                                                modifier = Modifier
                                                    .padding(bottom = 25.dp, end = 15.dp)
                                                    .height(25.dp)
                                            ) {
                                                Text(
                                                    text = if(listOfButtonsCars[index].value) "Selected" else "Select",
                                                    fontSize = 17.sp,
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
                }
            }
        }
    }
}