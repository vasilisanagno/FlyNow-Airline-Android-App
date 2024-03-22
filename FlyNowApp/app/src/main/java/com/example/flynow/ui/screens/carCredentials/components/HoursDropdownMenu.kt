package com.example.flynow.ui.screens.carCredentials.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.carCredentials.CarCredentialsViewModel

//component for the dropdown menu of the hours
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoursDropdownMenu(
    sharedViewModel: SharedViewModel,
    carCredentialsViewModel: CarCredentialsViewModel,
    pickUpOrReturn: Int
) {
    val isExpanded = remember {
        mutableStateOf(false)
    }
    val hours = remember {
        mutableStateOf(
            when (pickUpOrReturn) {
                0 -> {
                    sharedViewModel.pickUpHour
                }
                1 -> {
                    sharedViewModel.returnHour
                }
                else -> ""
            }
        )
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = { newValue ->
            isExpanded.value = newValue
        },
        modifier = Modifier
            .width(110.dp)
            .padding(top = 17.dp, start = 10.dp, end = 2.dp)
    ) {
        FlyNowTextField(
            text = hours.value,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = "",
            readOnly = true,
            onTextChange = {
                if(pickUpOrReturn == 0) {
                    sharedViewModel.pickUpHour = hours.value
                } else if(pickUpOrReturn == 1) {
                    sharedViewModel.returnHour = hours.value
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            enabled  = ((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpHourBool)||
                    (pickUpOrReturn == 1 && carCredentialsViewModel.returnHourBool)
                    ||
                    (sharedViewModel.returnDateCar != ""
                    && !((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpHourBool)
                            ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnHourBool)))),
        )
        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = {
                isExpanded.value = false
            },
            modifier = Modifier
                .width(90.dp)
                .background(color = Color.White)
        ) {
            for (i in 0..23) {
                DropdownMenuItem(
                    text = {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (i < 10) "0${i}" else "$i",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    },
                    onClick = {
                        hours.value = if (i < 10) "0${i}" else "$i"
                        isExpanded.value = false
                        if(pickUpOrReturn == 0) {
                            sharedViewModel.pickUpHour = hours.value
                        } else if(pickUpOrReturn == 1) {
                            sharedViewModel.returnHour = hours.value
                        }
                        carCredentialsViewModel.rentingTimeError = false
                    },
                    modifier = Modifier.background(color = Color.White)
                )
            }
        }
    }
}