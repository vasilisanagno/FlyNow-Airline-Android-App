package com.example.flynow.ui.screens.passengers

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.material3.Scaffold
import androidx.navigation.NavController
import com.example.flynow.R
import com.example.flynow.navigation.Flights
import com.example.flynow.navigation.Passengers
import com.example.flynow.navigation.Seats
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.screens.passengers.components.PassengersListOfInputFields

//In this screen the passengers of the reservation, that is being processed,
//fill in their personal information
//navController helps to navigate to previous page or next page,
//passengers view model is for one variable that is used across the components of this screen
//and shared view model is for shared variables
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PassengersScreen(
    navController: NavController,
    passengersViewModel: PassengersViewModel,
    sharedViewModel: SharedViewModel
) {
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { passengersViewModel.prepareForTheNextScreen() },
            previousRoute = Passengers.route,
            nextRoute = Seats.route,
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
                    passengersViewModel.goToPreviousScreen()
                    navController.navigate(Flights.route) {
                        popUpTo(Passengers.route)
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        Icons.Outlined.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF023E8A)
                    )
                }
                //"Passengers" text title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Passengers",
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
                        Icons.Filled.People,
                        contentDescription = "passengers",
                        modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 5.dp),
                        tint = Color(0xFF023E8A)
                    )
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
            PassengersListOfInputFields(
                numOfPassengers = sharedViewModel.passengersCounter,
                passengersViewModel = passengersViewModel,
                sharedViewModel = sharedViewModel
            )
        }
    }
}