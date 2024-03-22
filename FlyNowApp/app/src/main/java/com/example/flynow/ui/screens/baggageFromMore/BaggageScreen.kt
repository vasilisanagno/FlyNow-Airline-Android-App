package com.example.flynow.ui.screens.baggageFromMore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.navigation.BaggageFromMore
import com.example.flynow.navigation.BaggageFromMoreDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowCredentials
import kotlinx.coroutines.delay

//screen that has the credentials to come into the baggage screen
//to update/select new baggage in a reservation
@Composable
fun BaggageScreen(
    navController: NavController,
    baggageViewModel: BaggageViewModel,
    sharedViewModel: SharedViewModel
) {
    //api that stores the passengers info and the currently number of baggage pieces per passenger
    //to check how many pieces of baggage remain to select the passenger
    LaunchedEffect(sharedViewModel.baggageFromMore) {
        if(sharedViewModel.baggageFromMore) {
            baggageViewModel.getBaggagePerPassenger()
            sharedViewModel.showProgressBar = true
            delay(2000)
            navController.navigate(BaggageFromMoreDetails.route) {
                popUpTo(BaggageFromMore.route)
                launchSingleTop = true
            }
        }
    }
    FlyNowCredentials(
        state = "BaggageFromMore",
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}