package com.example.flynow.ui.screens.carCredentials.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.carCredentials.CarCredentialsViewModel

//component that displays the car date text input with the calendar
@Composable
fun CarDatePickerDialog(
    sharedViewModel: SharedViewModel,
    carCredentialsViewModel: CarCredentialsViewModel,
    pickUpOrReturn: Int
) {
    val showDatePicker = remember {
        mutableStateOf(false)
    }
    if(sharedViewModel.pickUpDateCar == ""){
        sharedViewModel.returnDateCar = ""
    }
    //the return text field is shown when the pick up is filled
    FlyNowTextField(
        text =
        if((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
            ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool))
            sharedViewModel.pickUpDateCar
        else
            sharedViewModel.returnDateCar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, bottom = 5.dp, end = 10.dp)
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label =
        if((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
            ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool))
            "Pick Up Date"
        else
            "Return Date",
        readOnly = true,
        onTextChange = {
            sharedViewModel.pickUpDateCar = it
            sharedViewModel.buttonClickedCredentials = false
        },
        leadingIcon = {
            IconButton(
                onClick = { showDatePicker.value = true },
                enabled = (((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                        ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool)) && sharedViewModel.pickUpDateCar == "")
                        || (!((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                        ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool)) && sharedViewModel.pickUpDateCar != "")
                        || (((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                        ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool)) && sharedViewModel.pickUpDateCar != "")
            )
            {
                Icon(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "calendar",
                    tint = Color(0xFF00B4D8)
                )
            }
        },
        supportingText = {
            Text(
                text =
                if((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                    ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool))
                    "Click calendar to select pick up date"
                else
                    "Click calendar to select return date",
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        },
        enabled = (((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool)) && sharedViewModel.pickUpDateCar == "")
                || (!((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool)) && sharedViewModel.pickUpDateCar != "")
                || (((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool)) && sharedViewModel.pickUpDateCar != ""),
        isError = (
                if(pickUpOrReturn == 0)
                    sharedViewModel.pickUpDateCar == "" && sharedViewModel.buttonClickedCredentials
                else
                    sharedViewModel.returnDateCar == "" && sharedViewModel.buttonClickedCredentials)
    )
    if (showDatePicker.value) {
        CarDatePicker(
            carCredentialsViewModel = carCredentialsViewModel,
            sharedViewModel = sharedViewModel,
            pickUpOrReturn = pickUpOrReturn,
            onSelectedDate = {
                if((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpBool)
                    ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnBool))
                    sharedViewModel.pickUpDateCar = it
                else
                    sharedViewModel.returnDateCar = it
            },
            onDismiss = { showDatePicker.value = false }
        )
    }
}