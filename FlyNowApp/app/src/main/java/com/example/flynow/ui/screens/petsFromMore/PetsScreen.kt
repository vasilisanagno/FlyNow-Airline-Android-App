package com.example.flynow.ui.screens.petsFromMore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.flynow.navigation.PetsFromMore
import com.example.flynow.navigation.PetsFromMoreDetails
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowCredentials
import kotlinx.coroutines.delay

//screen that checks the credentials to retrieve the pet information
@Composable
fun PetsScreen(
    navController: NavController,
    petsViewModel: PetsViewModel,
    sharedViewModel: SharedViewModel
) {
    //api that stores the return value from query in variable petSize and shows
    //if the user has pet or not and what size
    LaunchedEffect(sharedViewModel.petsFromMore) {
        if(sharedViewModel.petsFromMore) {
            petsViewModel.getPets()
            sharedViewModel.showProgressBar = true
            delay(2000)
            navController.navigate(PetsFromMoreDetails.route) {
                popUpTo(PetsFromMore.route)
                launchSingleTop = true
            }
        }
    }
    FlyNowCredentials(
        state = "PetsFromMore",
        navController = navController,
        sharedViewModel = sharedViewModel
    )
}
