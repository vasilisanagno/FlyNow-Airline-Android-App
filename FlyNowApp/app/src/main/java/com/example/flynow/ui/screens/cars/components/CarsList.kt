package com.example.flynow.ui.screens.cars.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.cars.CarsViewModel

//component that shows the list of cars in car card components
@Composable
fun CarsList(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    carsViewModel: CarsViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //if there is no car show a button that says go back, that the user returns to the rent a car page
        items(carsViewModel.noResults) {
            if(!sharedViewModel.seeBottomBar) {
                NoCarResults(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    carsViewModel = carsViewModel
                )
            }
        }
        //iterates the returning cars and showing them to cards
        items(sharedViewModel.listOfCars.size) { index ->
            CarCard(
                sharedViewModel = sharedViewModel,
                carsViewModel = carsViewModel,
                index = index
            )
        }
    }
}