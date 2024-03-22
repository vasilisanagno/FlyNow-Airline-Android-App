package com.example.flynow.ui.screens.flights

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.flynow.navigation.Book
import com.example.flynow.navigation.Flights
import com.example.flynow.navigation.Passengers
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.screens.flights.components.FlightsList
import com.example.flynow.utils.Constants
import kotlinx.coroutines.delay

//screen that shows the flights that came forward after the choices of the user
//navController helps to navigate to previous page or next page,
//shared view model is more shared variables that are sharing many screens
//flights view model has functions and variables about the state of this screen
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType",
    "MutableCollectionMutableState"
)
@Composable
fun FlightsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    flightsViewModel: FlightsViewModel
) {
    //delay 3 seconds to be the progress bar and launch the flights
    LaunchedEffect(Unit) {
        if(sharedViewModel.showProgressBar) {
            delay(3000)
            sharedViewModel.showProgressBar = false
        }
    }

    LaunchedEffect(flightsViewModel.sortPrice, flightsViewModel.sortDepartureTime) {
        flightsViewModel.sortingOutbound()
    }
    LaunchedEffect(flightsViewModel.sortPriceReturn,flightsViewModel.sortDepartureTimeReturn) {
        flightsViewModel.sortingInbound()
    }

    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        if(sharedViewModel.seeBottomBar) {
            FlyNowBottomAppBar(
                navController = navController,
                prepareForTheNextScreen = { flightsViewModel.prepareForTheNextScreen() },
                previousRoute = Flights.route,
                nextRoute = Passengers.route,
                totalPrice = sharedViewModel.totalPriceOneWay + sharedViewModel.totalPriceReturn,
                enabled = (sharedViewModel.totalPriceOneWay != 0.0 && sharedViewModel.totalPriceReturn != 0.0 && sharedViewModel.page == 1)
                        || (sharedViewModel.page == 0 && sharedViewModel.totalPriceOneWay != 0.0)
            )
        }
    }) {
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
                    flightsViewModel.goToPreviousScreen()
                    //navigates back to the book page
                    navController.navigate(Book.route) {
                        popUpTo(Flights.route)
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
                        text = "Flights",
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
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Constants.gradient)) {
                if(!sharedViewModel.showProgressBar) {
                    FlightsList(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        flightsViewModel = flightsViewModel
                    )
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
}