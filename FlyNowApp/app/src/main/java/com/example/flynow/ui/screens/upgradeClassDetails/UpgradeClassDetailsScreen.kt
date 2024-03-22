package com.example.flynow.ui.screens.upgradeClassDetails

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.components.FlyNowShowDialog
import com.example.flynow.ui.screens.upgradeClassDetails.components.UpgradeToBusinessClass
import kotlinx.coroutines.delay

//screen that shows the options for the user to upgrade the class of his flights
// in his booking
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpgradeClassDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    upgradeClassDetailsViewModel: UpgradeClassDetailsViewModel
) {
    //api that complete the update query for the selecting value from the user for the upgrade class
    LaunchedEffect(sharedViewModel.updateBusiness) {
        if(sharedViewModel.updateBusiness) {
            upgradeClassDetailsViewModel.updateClass()
            delay(2000)
            sharedViewModel.updateBusiness = false
        }
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { upgradeClassDetailsViewModel.finishUpdate() },
            previousRoute = "",
            nextRoute = "",
            totalPrice = upgradeClassDetailsViewModel.upgradeBusinessPrice,
            enabled = upgradeClassDetailsViewModel.upgradeBusinessPrice != 0.0
        )
    }) {
        FlyNowShowDialog(
            state = "UpgradeClass",
            navController = navController,
            sharedViewModel = sharedViewModel,
            wifiDetailsViewModel = null,
            upgradeClassDetailsViewModel = upgradeClassDetailsViewModel,
            baggageDetailsViewModel = null,
            petsDetailsViewModel = null
        )
        UpgradeToBusinessClass(
            navController = navController,
            sharedViewModel = sharedViewModel,
            upgradeClassDetailsViewModel = upgradeClassDetailsViewModel
        )
    }
}