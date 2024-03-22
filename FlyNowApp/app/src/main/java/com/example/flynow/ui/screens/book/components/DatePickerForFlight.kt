package com.example.flynow.ui.screens.book.components

import android.widget.Toast
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.utils.Converters

//component that shows the date picker and select a date the user,
//range of dates with return or one date for one-way
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerForFlight(
    page: Int,
    onSelectedDate: (String) -> Unit,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val utcDate = Converters.convertMillisToDate(utcTimeMillis)
            val currentDate = Converters.convertMillisToDate(System.currentTimeMillis())

            return (((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                    &&
                    utcDate.substring(0,2).toInt() >= currentDate.substring(0,2).toInt()
                    ||
                    utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt()
                    )
                    &&
                    utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt()
                    )
                    ||
                    utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt())
        }
    })

    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val utcDate = Converters.convertMillisToDate(utcTimeMillis)
            val currentDate = Converters.convertMillisToDate(System.currentTimeMillis())

            return (((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                    &&
                    utcDate.substring(0,2).toInt() >= currentDate.substring(0,2).toInt()
                    ||
                    utcDate.substring(3,5).toInt() > currentDate.substring(3,5).toInt()
                    )
                    &&
                    utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt()
                    )
                    ||
                    utcDate.substring(6,10).toInt() > currentDate.substring(6,10).toInt())
        }
    })

    val selectedStartDate = dateRangePickerState.selectedStartDateMillis?.let {
        Converters.convertMillisToDate(it)
    } ?: ""
    val selectedEndDate = dateRangePickerState.selectedEndDateMillis?.let {
        Converters.convertMillisToDate(it)
    } ?: ""

    val selectedDate = datePickerState.selectedDateMillis?.let {
        Converters.convertMillisToDate(it)
    } ?: ""

    val context = LocalContext.current

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    if (page == 1 && selectedStartDate != "" && selectedEndDate != "") {
                        onStartDateSelected(selectedStartDate)
                        onEndDateSelected(selectedEndDate)
                        onDismiss()
                    } else if (page == 0) {
                        onSelectedDate(selectedDate)
                        onDismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "You must select a departure and a return date!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
                Text(
                    text = "OK",
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
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
                Text(
                    text = "Cancel",
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
        colors = DatePickerDefaults.colors(
            containerColor = Color(0xFFEBF2FA)
        ),
        modifier = Modifier.scale(scaleX = 0.9f, scaleY = 0.9f)
    ) {
        if (page == 1) {
            DatesForRoundTrip(
                dateRangePickerState = dateRangePickerState,
                selectedStartDate = selectedStartDate,
                selectedEndDate = selectedEndDate
            )
        } else {
            DateForOneWayTrip(
                datePickerState = datePickerState,
                selectedDate = selectedDate
            )
        }
    }
}