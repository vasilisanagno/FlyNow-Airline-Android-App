package com.example.flynow.ui.screens.petsFromMoreDetails

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomAppBar
import com.example.flynow.ui.components.FlyNowShowDialog
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import com.example.flynow.ui.screens.petsFromMoreDetails.components.UpgradePetInReservation
import kotlinx.coroutines.delay

//screen that shows the details of the pets that a user can select
//to upgrade in his booking
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PetsDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    petsDetailsViewModel: PetsDetailsViewModel,
    baggageAndPetsViewModel: BaggageAndPetsViewModel
) {
    //api that complete the update query for the selecting value from the user for the pets
    LaunchedEffect(sharedViewModel.updatePets) {
        if(sharedViewModel.updatePets) {
            petsDetailsViewModel.updatePets()
            delay(2000)
            sharedViewModel.updatePets = false
        }
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        FlyNowBottomAppBar(
            navController = navController,
            prepareForTheNextScreen = { petsDetailsViewModel.finishUpdate() },
            previousRoute = "",
            nextRoute = "",
            totalPrice = petsDetailsViewModel.petsPrice,
            enabled = petsDetailsViewModel.petsPrice != 0.0
        )
    }) {
        FlyNowShowDialog(
            state = "PetsFromMore",
            navController = navController,
            sharedViewModel = sharedViewModel,
            wifiDetailsViewModel = null,
            upgradeClassDetailsViewModel = null,
            baggageDetailsViewModel = null,
            petsDetailsViewModel = petsDetailsViewModel
        )
        UpgradePetInReservation(
            navController = navController,
            petsDetailsViewModel = petsDetailsViewModel,
            baggageAndPetsViewModel = baggageAndPetsViewModel,
            sharedViewModel = sharedViewModel
        )
    }
}