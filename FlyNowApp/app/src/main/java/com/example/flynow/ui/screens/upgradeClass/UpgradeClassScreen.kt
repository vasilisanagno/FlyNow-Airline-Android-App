package com.example.flynow.ui.screens.upgradeClass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.navigation.UpgradeClass
import com.example.flynow.navigation.UpgradeClassDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowCredentials
import kotlinx.coroutines.delay

//screen that shows the credentials fields to check the if the booking exists
// and retrieves data for the class of the flights in the booking
@Composable
fun UpgradeClassScreen(
    navController: NavController,
    upgradeClassViewModel: UpgradeClassViewModel,
    sharedViewModel: SharedViewModel
) {
    ///api that stores the return value from query in variable upgradeToBusinessInfo and shows
    //the user state about the outbound and inbound flights, what class it is
    LaunchedEffect(sharedViewModel.upgradeToBusinessMore) {
        if(sharedViewModel.upgradeToBusinessMore) {
            upgradeClassViewModel.getClassFromApi()
            sharedViewModel.showProgressBar = true
            delay(2000)
            navController.navigate(UpgradeClassDetails.route) {
                popUpTo(UpgradeClass.route)
                launchSingleTop = true
            }
        }
    }
    FlyNowCredentials(
        state = "UpgradeClass",
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}