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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.carCredentials.CarCredentialsViewModel

//component for the dropdown menu of the minutes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinutesDropdownMenu(
    sharedViewModel: SharedViewModel,
    carCredentialsViewModel: CarCredentialsViewModel,
    pickUpOrReturn: Int
) {
    val isExpanded = remember {
        mutableStateOf(false)
    }
    val minutes = remember {
        mutableStateOf(
            when (pickUpOrReturn) {
                0 -> {
                    sharedViewModel.pickUpMins
                }
                1 -> {
                    sharedViewModel.returnMins
                }
                else -> ""
            }
        )
    }
    Text(
        text = ":",
        modifier = Modifier.padding(top = 35.dp, start = 1.dp, end = 1.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = { newValue ->
            isExpanded.value = newValue
        },
        modifier = Modifier
            .width(95.dp)
            .padding(top = 17.dp, start = 2.dp, end = 2.dp)
    ) {
        FlyNowTextField(
            text = minutes.value,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = "",
            readOnly = true,
            onTextChange = {
                if(pickUpOrReturn == 0) {
                    sharedViewModel.pickUpMins = minutes.value
                } else if(pickUpOrReturn == 1) {
                    sharedViewModel.returnMins = minutes.value
                }
                carCredentialsViewModel.rentingTimeError = false
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            enabled  = ((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpHourBool)||
                    (pickUpOrReturn == 1 && carCredentialsViewModel.returnHourBool)
                    ||
                    (sharedViewModel.returnDateCar != ""
                            && !((pickUpOrReturn == 0 && carCredentialsViewModel.pickUpHourBool)
                            ||(pickUpOrReturn == 1 && carCredentialsViewModel.returnHourBool)))),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = {
                isExpanded.value = false
            },
            modifier = Modifier
                .width(83.dp)
                .background(color = Color.White)
        ) {
            for (i in 0..45 step 15) {
                DropdownMenuItem(
                    text = {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (i < 15) "0${i}" else "$i",
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
                        minutes.value = if (i < 15) "0${i}" else "$i"
                        isExpanded.value = false
                        if(pickUpOrReturn == 0) {
                            sharedViewModel.pickUpMins = minutes.value
                        } else if(pickUpOrReturn == 1) {
                            sharedViewModel.returnMins = minutes.value
                        }
                        carCredentialsViewModel.rentingTimeError = false
                    },
                    modifier = Modifier.background(color = Color.White)
                )
            }
        }
    }
}