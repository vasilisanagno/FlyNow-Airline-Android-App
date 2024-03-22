package com.example.flynow.ui.screens.seats

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.BaggageAndPets
import com.example.flynow.navigation.Passengers
import com.example.flynow.navigation.Seats
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.screens.seats.components.InitializationOfSeatLists
import com.example.flynow.ui.screens.seats.components.SeatListsForInboundFlights
import com.example.flynow.ui.screens.seats.components.SeatListsForOutboundFlights
import com.example.flynow.utils.Constants

//In this screen there is the choice of the seats in the flights
//that the user selected
//navController helps to navigate to previous page or next page,
//seats view model keeps the state of the variable of this screen and
//has some function that helps inside the screen and the communication with the server
//shared view model has the shared variables across the app
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SeatsScreen(
    navController: NavController,
    seatsViewModel: SeatsViewModel,
    sharedViewModel: SharedViewModel
) {
    InitializationOfSeatLists(
        sharedViewModel = sharedViewModel,
        seatsViewModel = seatsViewModel
    )
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { seatsViewModel.prepareForTheNextScreen() },
            previousRoute = Seats.route,
            nextRoute = BaggageAndPets.route,
            totalPrice = sharedViewModel.totalPrice,
            enabled = true
        )
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
                    seatsViewModel.goToPreviousScreen()
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
                    .background(Constants.gradient)
            ) {
                LazyColumn {
                    items(sharedViewModel.passengersCounter) { index ->
                        SeatListsForOutboundFlights(
                            sharedViewModel = sharedViewModel,
                            seatsViewModel = seatsViewModel,
                            index = index
                        )
                    }
                    if(sharedViewModel.page == 1) {
                        items(sharedViewModel.passengersCounter) { index ->
                            SeatListsForInboundFlights(
                                sharedViewModel = sharedViewModel,
                                seatsViewModel = seatsViewModel,
                                index = index
                            )
                        }
                    }
                }
            }
        }
    }
}