package com.example.flynow.ui.screens.carCredentials.components

import androidx.compose.runtime.Composable
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.carCredentials.CarCredentialsViewModel

//component that creates the dropdown menu for the time selection
@Composable
fun TimeDropdownMenu(
    sharedViewModel: SharedViewModel,
    carCredentialsViewModel: CarCredentialsViewModel,
    hoursOrMins: Int,
    pickUpOrReturn: Int
) {
    //dropdown list for the hours that are from 00 - 23
    if((hoursOrMins == 0 && carCredentialsViewModel.hourBool)
        ||(hoursOrMins == 1 && carCredentialsViewModel.minBool)) {
        HoursDropdownMenu(
            sharedViewModel = sharedViewModel,
            carCredentialsViewModel = carCredentialsViewModel,
            pickUpOrReturn = pickUpOrReturn
        )
    }
    //dropdown list for the minutes that are 00, 15, 30 and 45
    else {
        MinutesDropdownMenu(
            sharedViewModel = sharedViewModel,
            carCredentialsViewModel = carCredentialsViewModel,
            pickUpOrReturn = pickUpOrReturn
        )
    }
}