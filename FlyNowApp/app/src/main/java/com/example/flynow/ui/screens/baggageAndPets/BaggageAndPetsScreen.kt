package com.example.flynow.ui.screens.baggageAndPets

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
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
import com.example.flynow.navigation.Seats
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.screens.baggageAndPets.components.BaggageFields
import com.example.flynow.ui.screens.baggageAndPets.components.ShowDialogToCommitReservation
import com.example.flynow.ui.screens.book.BookViewModel
import com.example.flynow.ui.screens.flights.FlightsViewModel
import com.example.flynow.ui.screens.passengers.PassengersViewModel
import com.example.flynow.ui.screens.seats.SeatsViewModel
import com.example.flynow.utils.Constants

//this screen shows the baggage and pets fields to select the user
//navController helps to navigate to previous page or next page,
//baggage and pets view model stores some variables and has some functions
//for different use in the screen
//and shared view model for the shared variables that this screen affects them
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BaggageAndPetsScreen(
    navController: NavController,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel,
    seatsViewModel: SeatsViewModel,
    passengersViewModel: PassengersViewModel,
    flightsViewModel: FlightsViewModel,
    bookViewModel: BookViewModel
){
    val state = "Baggage&Pets"

    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { baggageAndPetsViewModel.finishReservation() },
            previousRoute = "",
            nextRoute = "",
            totalPrice = sharedViewModel.totalPrice,
            enabled = true
        )
    }) {
        ShowDialogToCommitReservation(
            navController = navController,
            baggageAndPetsViewModel = baggageAndPetsViewModel,
            sharedViewModel = sharedViewModel,
            seatsViewModel = seatsViewModel,
            passengersViewModel = passengersViewModel,
            flightsViewModel = flightsViewModel,
            bookViewModel = bookViewModel
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //"Back" button
                IconButton(onClick = {
                    baggageAndPetsViewModel.goToPreviousScreen()
                    navController.navigate(Seats.route) {
                        popUpTo(BaggageAndPets.route)
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
                        text = "Baggage&Pets",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(end = 45.dp),
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
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color(0xFF00B4D8))

            if(sharedViewModel.finishReservation == 0) {
                Column(
                    Modifier
                        .background(Constants.gradient)
                        .fillMaxSize()){
                    BaggageFields(
                        state = state,
                        sharedViewModel = sharedViewModel,
                        baggageAndPetsViewModel = baggageAndPetsViewModel
                    )
                    baggageAndPetsViewModel.addingPrices()
                }
            }
        }
    }
}