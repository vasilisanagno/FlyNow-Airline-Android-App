package com.example.flynow.ui.screens.seats.components

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.seats.SeatsViewModel

//component that shows the dropdown menu for seats
//isClickedSeat is list that that shows in the same flight what seats are selected from the passenger
//and make them not enabled adn booking failed is for the completion of the reservation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatsDropdownMenu(
    sharedViewModel: SharedViewModel,
    seatsViewModel: SeatsViewModel,
    seats: MutableList<String>,
    index: Int,
    column: Int,
    isClickedSeat: MutableList<Boolean>
) {
    val isExpanded = remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = { newValue ->
            isExpanded.value = newValue
        },
        modifier = Modifier
            .width(140.dp)
            .padding(top = 5.dp, start = 10.dp, end = 2.dp)
    ) {
        OutlinedTextField(
            value = if(sharedViewModel.seats.size == 0) "" else sharedViewModel.seats[index][column].value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            label = {
                Text(
                    fontSize = 16.sp,
                    text = "Seat"
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color(0xFF023E8A),
                focusedBorderColor = Color(0xFF023E8A),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFF00B4D8),
                errorContainerColor = Color.White
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            isError = (sharedViewModel.seats.size!=0 && sharedViewModel.seats[index][column].value == "" && seatsViewModel.buttonClicked)
        )
        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = {
                isExpanded.value = false
            },
            modifier = Modifier
                .width(120.dp)
                .background(color = Color.White)
        ) {
            for (i in 0 until seats.size) {
                DropdownMenuItem(
                    text = {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = seats[i],
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
                        for(j in 0 until seats.size) {
                            if(seats[j]==sharedViewModel.seats[index][column].value) {
                                isClickedSeat[j] = false
                            }
                        }
                        isExpanded.value = false
                        isClickedSeat[i] = true
                        sharedViewModel.bookingFailed = false
                        sharedViewModel.seats.forEachIndexed { index1,seat ->
                            if(index1==index) {
                                seat.forEachIndexed { index2,seat1 ->
                                    if(index2==column) {
                                        seat1.value = seats[i]
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.background(color = Color.White),
                    enabled = !isClickedSeat[i]
                )
            }
        }
    }
}