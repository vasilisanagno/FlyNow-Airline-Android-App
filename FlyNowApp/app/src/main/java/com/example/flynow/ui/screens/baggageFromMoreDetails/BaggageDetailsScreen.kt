package com.example.flynow.ui.screens.baggageFromMoreDetails

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.components.FlyNowShowDialog
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import com.example.flynow.ui.screens.baggageFromMoreDetails.components.UpgradeBaggage
import kotlinx.coroutines.delay

//screen that uses components from the baggageAndPets screen
//to show only the baggage fields in the more screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BaggageDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    baggageDetailsViewModel: BaggageDetailsViewModel,
    baggageAndPetsViewModel: BaggageAndPetsViewModel
) {
    //api that complete the update query for the selecting values for the baggage from the user
    LaunchedEffect(sharedViewModel.updateBaggage) {
        if(sharedViewModel.updateBaggage) {
            baggageDetailsViewModel.updateBaggage()
            delay(2000)
            sharedViewModel.updateBaggage = false
        }
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { baggageDetailsViewModel.finishUpdate() },
            previousRoute = "",
            nextRoute = "",
            totalPrice = baggageDetailsViewModel.baggagePrice,
            enabled = baggageDetailsViewModel.baggagePrice != 0.0
        )
    }) {
        FlyNowShowDialog(
            state = "BaggageFromMore",
            navController = navController,
            sharedViewModel = sharedViewModel,
            wifiDetailsViewModel = null,
            upgradeClassDetailsViewModel = null,
            baggageDetailsViewModel = baggageDetailsViewModel,
            petsDetailsViewModel = null
        )
        UpgradeBaggage(
            navController = navController,
            baggageDetailsViewModel = baggageDetailsViewModel,
            baggageAndPetsViewModel = baggageAndPetsViewModel,
            sharedViewModel = sharedViewModel
        )
    }
}