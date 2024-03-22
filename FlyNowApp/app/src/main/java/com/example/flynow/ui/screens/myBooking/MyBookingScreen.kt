package com.example.flynow.ui.screens.myBooking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.navigation.MyBooking
import com.example.flynow.navigation.MyBookingDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowCredentials
import kotlinx.coroutines.delay

//screen that shows the text fields for finding of one booking
//navController helps to navigate to previous page or next page
//share view model is for the shared variables in the screen and
//my booking view model is for the variables and functionalities
// that are referred to the screen
@Composable
fun MyBookingScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    myBookingViewModel: MyBookingViewModel
) {
    //api that takes all the information for the booking
    //flights, passengers, seats, baggage, wifi, pets, cars and total price
    LaunchedEffect(sharedViewModel.myBooking) {
        if(sharedViewModel.myBooking) {
            myBookingViewModel.getBookingDetails()
            sharedViewModel.showProgressBar = true
            delay(5000)
            navController.navigate(MyBookingDetails.route) {
                popUpTo(MyBooking.route)
                launchSingleTop = true
            }
        }
    }
    FlyNowCredentials(
        state = "MyBooking",
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}