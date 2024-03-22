package com.example.flynow.ui.screens.wifiDetails

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.components.FlyNowShowDialog
import com.example.flynow.ui.screens.wifiDetails.components.AddWifiOnBoard
import kotlinx.coroutines.delay

//screen that shows the choices that may the user wants about wifi on board
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WifiDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    wifiDetailsViewModel: WifiDetailsViewModel
) {
    //api that complete the update query for the selecting value from the user for the wifi
    LaunchedEffect(sharedViewModel.updateWifi) {
        if(sharedViewModel.updateWifi) {
            wifiDetailsViewModel.updateWifi()
            delay(2000)
            sharedViewModel.updateWifi = false
        }
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { wifiDetailsViewModel.finishUpdate() },
            previousRoute = "",
            nextRoute = "",
            totalPrice = wifiDetailsViewModel.wifiPrice,
            enabled = wifiDetailsViewModel.selectedWifi != 0
        )
    }) {
        FlyNowShowDialog(
            state = "Wifi",
            navController = navController,
            sharedViewModel = sharedViewModel,
            wifiDetailsViewModel = wifiDetailsViewModel,
            upgradeClassDetailsViewModel = null,
            baggageDetailsViewModel = null,
            petsDetailsViewModel = null
        )
        AddWifiOnBoard(
            navController = navController,
            sharedViewModel = sharedViewModel,
            wifiDetailsViewModel = wifiDetailsViewModel
        )
    }
}