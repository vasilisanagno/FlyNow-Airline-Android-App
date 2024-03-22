package com.example.flynow.ui.screens.book.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.flynow.R
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.book.BookViewModel

//component that shows the outlined text field where the user can click the leading icon to
//select date or dates(for return)
@Composable
fun DatePickerDialog(
    bookViewModel: BookViewModel,
    page: Int
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    FlyNowTextField(
        text = if(page == 1 && bookViewModel.departureDate!="" && bookViewModel.returnDate!="") "${bookViewModel.departureDate} - ${bookViewModel.returnDate}" else bookViewModel.departureDate,
        modifier = Modifier
            .width(500.dp).padding(start = 10.dp, bottom = 5.dp, end = 10.dp)
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label = if(page == 0) "Departure" else "Departure - Return",
        readOnly = true,
        onTextChange = {
            if (page == 0) {
                bookViewModel.departureDate = it
                bookViewModel.buttonClicked = false
            } else {
                bookViewModel.returnDate = it
                bookViewModel.buttonClicked = false
            }
        },
        leadingIcon = {
            //clicking the icon to see the dates to select
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "calendar",
                    tint = Color(0xFF00B4D8)
                )
            }
        },
        supportingText = {
            Text(
                if(page == 0) "Click calendar to select date" else "Click calendar to select departure and return dates",
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        },
        isError = (bookViewModel.buttonClicked &&
                ((bookViewModel.departureDate == "" && page == 0) ||
                        ((bookViewModel.returnDate == "" || bookViewModel.departureDate == "") && page == 1)))
    )
    //stores in the correct variables the values in according the page that the user is
    if (showDatePicker) {
        DatePickerForFlight(
            page,
            onSelectedDate = {if(page == 0) bookViewModel.departureDate = it},
            onStartDateSelected = { if(page == 1) bookViewModel.departureDate = it},
            onEndDateSelected = { if(page == 1) bookViewModel.returnDate = it},
            onDismiss = { showDatePicker = false }
        )
    }
}