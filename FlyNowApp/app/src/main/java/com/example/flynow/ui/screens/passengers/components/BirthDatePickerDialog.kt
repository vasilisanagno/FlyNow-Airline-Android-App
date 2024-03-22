package com.example.flynow.ui.screens.passengers.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.utils.Constants

//component that displays the birthdate text input with the calendar
@Composable
fun BirthDatePickerDialog(
    sharedViewModel: SharedViewModel,
    buttonClicked: Boolean,
    index: Int
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    FlyNowTextField(
        text = sharedViewModel.passengers[index].birthdate.value,
        modifier = Modifier
            .width(500.dp)
            .padding(start = 10.dp, top = 10.dp,
                bottom = 5.dp, end = 10.dp)
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label = buildAnnotatedString {
            append("Birthdate")
            withStyle(Constants.superscript) {
                append("*")
            }
        }.toString(),
        readOnly = true,
        onTextChange = { sharedViewModel.passengers[index].birthdate.value = it },
        leadingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "calendar",
                    tint = Color(0xFF00B4D8)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email),
        supportingText = {
            Text("Click calendar to select birthdate",
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        },
        isError = (sharedViewModel.passengers[index].birthdate.value == "" && buttonClicked)
    )
    if (showDatePicker) {
        BirthDatePicker(
            onSelectedDate = {sharedViewModel.passengers[index].birthdate.value = it},
            onDismiss = { showDatePicker = false}
        )
    }
}