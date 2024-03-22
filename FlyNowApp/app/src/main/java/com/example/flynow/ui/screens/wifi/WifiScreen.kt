package com.example.flynow.ui.screens.wifi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.navigation.Wifi
import com.example.flynow.navigation.WifiDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowCredentials
import kotlinx.coroutines.delay

//screen that shows the credentials fields to check the if the booking exists
// and retrieves data for the wifi in the booking
@Composable
fun WifiScreen(
    navController: NavController,
    wifiViewModel: WifiViewModel,
    sharedViewModel: SharedViewModel
) {
    //api that stores the return value from query in variable wifiOnBoardInfo and shows
    //if the user has wifi or not
    LaunchedEffect(sharedViewModel.wifiMore) {
        if(sharedViewModel.wifiMore) {
            wifiViewModel.getWifiFromApi()
            sharedViewModel.showProgressBar = true
            delay(2000)
            navController.navigate(WifiDetails.route) {
                popUpTo(Wifi.route)
                launchSingleTop = true
            }
        }
    }
    FlyNowCredentials(
        state = "Wifi",
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}