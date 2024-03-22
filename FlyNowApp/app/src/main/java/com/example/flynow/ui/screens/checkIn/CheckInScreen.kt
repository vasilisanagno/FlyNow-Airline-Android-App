package com.example.flynow.ui.screens.checkIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.navigation.CheckIn
import com.example.flynow.navigation.CheckInDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowCredentials
import kotlinx.coroutines.delay

//screen that shows the credentials fields to find the check-in of a booking
//navController to navigate backward or forward to other pages
@Composable
fun CheckInScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    checkInViewModel: CheckInViewModel
) {
    //api that takes all the information for the checkin
    //flights, passengers, seats, baggage, wifi and pets
    LaunchedEffect(sharedViewModel.checkIn) {
        if (sharedViewModel.checkIn) {
            checkInViewModel.getCheckInDetails()
            delay(2000)
            if(sharedViewModel.checkInOpen) {
                sharedViewModel.showProgressBar = true
                delay(3000)
                navController.navigate(CheckInDetails.route) {
                    popUpTo(CheckIn.route)
                    launchSingleTop = true
                }
            }
        }
    }
    FlyNowCredentials(
        state = "Check-In",
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}