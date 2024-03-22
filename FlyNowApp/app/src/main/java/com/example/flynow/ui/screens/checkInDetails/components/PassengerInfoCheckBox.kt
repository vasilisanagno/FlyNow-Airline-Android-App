package com.example.flynow.ui.screens.checkInDetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel

//function that shows the passengers with checkboxes in front of their names
//to be checked for the check-in button to be enabled
@Composable
fun PassengerInfoCheckBox(
    sharedViewModel: SharedViewModel,
    myBookingDetailsViewModel: MyBookingDetailsViewModel
) {

    Row {
        Icon(
            Icons.Filled.Person,
            contentDescription = "passenger",
            tint = Color(0xFF023E8A),
            modifier = Modifier.padding(top = 13.dp, start = 10.dp)
        )
        Text(
            text = "Passengers",
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
            color = Color(0xFF023E8A),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            fontWeight = FontWeight.Bold
        )
    }
    Column {
        for (index in 0 until sharedViewModel.numOfPassengersCheckIn) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox for each passenger
                Checkbox(
                    checked = sharedViewModel.checkedState[index].value,
                    onCheckedChange = { isChecked ->
                        sharedViewModel.checkedState[index].value = isChecked

                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF00B4D8),
                        uncheckedColor = Color(0xFF00B4D8)
                    ),
                    modifier = Modifier.padding(start = 10.dp, top = 12.5.dp )
                )

                Text(
                    text = if (sharedViewModel.passengersCheckIn[index]?.gender!!.value == "Female")
                        "Mrs ${sharedViewModel.passengersCheckIn[index]?.firstname!!.value} ${sharedViewModel.passengersCheckIn[index]?.lastname!!.value}"
                    else
                        "Mr ${sharedViewModel.passengersCheckIn[index]?.firstname!!.value} ${sharedViewModel.passengersCheckIn[index]?.lastname!!.value}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 5.dp, top = 10.dp),
                    color = Color(0xFF0077FF),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                )

                Box(
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(
                        onClick = {
                            myBookingDetailsViewModel.showDialogPassenger = true
                            myBookingDetailsViewModel.selectedIndexDetails = index
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "showDetails",
                            modifier = Modifier
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 9.dp
                                ),
                            tint = Color(0xFF023FCC)
                        )
                    }
                }
            }
        }
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        thickness = 1.dp,
        color = Color(0xFF023E8A)
    )
}