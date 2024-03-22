package com.example.flynow.ui.screens.book.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Airports
import com.example.flynow.navigation.Book
import com.example.flynow.navigation.Flights
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowButton
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.book.BookViewModel
import com.example.flynow.utils.Constants

//component that shows the fields in the One-Way trip or Round Trip page
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ShowInputFields(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    bookViewModel: BookViewModel,
    page: Int
) {
    var temp = ""
    val makeQuery = remember {
        mutableStateOf(false)
    }

    //api for taking flights from the database and store the data to the variables
    //post request to send data that select in the book screen the user and make
    //the queries accordingly to the variables
    LaunchedEffect(makeQuery.value) {
        if(makeQuery.value) {
            bookViewModel.fetchFlightsFromApi()
            sharedViewModel.showProgressBar = true
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
            .background(Constants.gradient)
            .verticalScroll(rememberScrollState())
    ) {
        //different fields that is shown on the screen
        //Text input "From"
        FlyNowTextField(
            text = sharedViewModel.airportFrom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                .clickable(enabled = false, onClickLabel = null, onClick = {}),
            label = "From",
            readOnly = true,
            onTextChange = { sharedViewModel.airportFrom = it
                bookViewModel.buttonClicked = false },
            leadingIcon = {
                //selecting airport about clicking the icon
                IconButton(onClick = {
                    sharedViewModel.whatAirport = 0
                    sharedViewModel.rentCar = false
                    sharedViewModel.page = page
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
            isError = (bookViewModel.buttonClicked && sharedViewModel.airportFrom == "")
        )
        //Text input "To"
        FlyNowTextField(
            text = sharedViewModel.airportTo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 5.dp, end = 10.dp)
                .clickable(enabled = false, onClickLabel = null, onClick = {}),
            label = "To",
            readOnly = true,
            onTextChange = {
                sharedViewModel.airportTo = it
                bookViewModel.buttonClicked = false
            },
            leadingIcon = {
                //selecting airport about clicking the icon
                IconButton(onClick = {
                    sharedViewModel.whatAirport = 1
                    sharedViewModel.rentCar = false
                    sharedViewModel.page = page
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
            isError = (bookViewModel.buttonClicked && sharedViewModel.airportTo == "")
        )
        DatePickerDialog(bookViewModel, page)
        //Text input "Passengers"
        FlyNowTextField(
            text = temp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 5.dp, end = 10.dp)
                .clickable(enabled = false, onClickLabel = null, onClick = {}),
            label = if(sharedViewModel.passengersCounter == 1) "1 Passenger" else "${sharedViewModel.passengersCounter} Passengers",
            readOnly = true,
            onTextChange = {temp = it},
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
                        if (sharedViewModel.passengersCounter > 1) {
                            sharedViewModel.passengersCounter--
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
                        if(sharedViewModel.passengersCounter < 10) {
                            sharedViewModel.passengersCounter++
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
            }
        )
        SwitchButtonAndCheckBox(bookViewModel)
        //Button "Search Flights" to continue to the next page
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            FlyNowButton(
                text = "Search Flights",
                onClick = {
                    //go to Flights Screen
                    bookViewModel.amChecked = false
                    bookViewModel.pmChecked = false
                    bookViewModel.checked = false
                    bookViewModel.buttonClicked = false
                    sharedViewModel.listOfClassButtonsOutbound.clear()
                    sharedViewModel.listOfClassButtonsInbound.clear()
                    if(sharedViewModel.airportFrom=="" ||
                        sharedViewModel.airportTo=="" ||
                        bookViewModel.departureDate=="") {
                        bookViewModel.buttonClicked = true
                    }
                    else {
                        makeQuery.value = true
                    }
                },
                modifier = Modifier.padding(top = 50.dp)
            )
        }
    }
}