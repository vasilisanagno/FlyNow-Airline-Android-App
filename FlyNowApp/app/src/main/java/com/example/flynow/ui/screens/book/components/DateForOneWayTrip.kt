package com.example.flynow.ui.screens.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R

//component that shows the date picker for only the one way trip without return
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateForOneWayTrip(
    datePickerState: DatePickerState,
    selectedDate: String
) {
    DatePicker(
        state = datePickerState,
        modifier = Modifier
            .background(color = Color(0xFFEBF2FA))
            .height(500.dp),
        title = {
            Text(
                text = "Select date to travel",
                fontSize = 24.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        },
        headline = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (datePickerState.selectedDateMillis != null) selectedDate else "No Date",
                    fontSize = 20.sp,
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
        showModeToggle = false,
        colors = DatePickerDefaults.colors(
            titleContentColor = Color(0xFF4361EE),
            headlineContentColor = Color(0xFF4361EE),
            weekdayContentColor = Color(0xFF023E8A),
            navigationContentColor = Color(0xFF023E8A),
            yearContentColor = Color(0xFF023E8A),
            dayContentColor = Color(0xFF023E8A),
            todayDateBorderColor = Color(0xFF4361EE),
            todayContentColor = Color(0xFF023E8A),
            subheadContentColor = Color(0xFF4361EE),
            dividerColor = Color(0xFF4361EE),
            selectedDayContainerColor = Color(0xFF023E8A),
            selectedYearContainerColor = Color(0xFF023E8A),
            containerColor = Color(0xFFEBF2FA),
            currentYearContentColor = Color(0xFF023E8A)
        )
    )
}
