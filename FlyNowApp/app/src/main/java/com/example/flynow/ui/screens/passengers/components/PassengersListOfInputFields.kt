package com.example.flynow.ui.screens.passengers.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowTextField
import com.example.flynow.ui.screens.passengers.PassengersViewModel
import com.example.flynow.utils.Constants
import com.example.flynow.utils.Validation

@Composable
fun PassengersListOfInputFields(
    numOfPassengers: Int,
    passengersViewModel: PassengersViewModel,
    sharedViewModel: SharedViewModel
) {
    if(sharedViewModel.passengers.size!=0) {
        //Text input fields for each passenger's personal information
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Constants.gradient)
        )
        {
            items(numOfPassengers) { index ->
                //"Passenger $num" text field
                Text(
                    text = "Passenger ${index + 1}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 5.dp),
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

                //Select "Gender" dropdown menu
                GenderDropdownMenu(
                    buttonClicked = passengersViewModel.buttonClicked,
                    sharedViewModel = sharedViewModel,
                    index = index
                )

                //Text Input "First name"
                FlyNowTextField(
                    text = sharedViewModel.passengers[index].firstname.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    label = buildAnnotatedString {
                        append("First Name")
                        withStyle(Constants.superscript) {
                            append("*")
                        }
                    }.toString(),
                    readOnly = false,
                    onTextChange = { sharedViewModel.passengers[index].firstname.value = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = (sharedViewModel.passengers[index].firstname.value == "" && passengersViewModel.buttonClicked)
                )

                //Text input "Last Name"
                FlyNowTextField(
                    text = sharedViewModel.passengers[index].lastname.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    label = buildAnnotatedString {
                        append("Last Name")
                        withStyle(Constants.superscript) {
                            append("*")
                        }
                    }.toString(),
                    readOnly = false,
                    onTextChange = { sharedViewModel.passengers[index].lastname.value = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = (sharedViewModel.passengers[index].lastname.value == "" && passengersViewModel.buttonClicked)
                )

                //Select "BirthDate" calendar
                BirthDatePickerDialog(
                    sharedViewModel = sharedViewModel,
                    buttonClicked = passengersViewModel.buttonClicked,
                    index = index
                )
                //Text input "Email"
                EmailTextField(
                    sharedViewModel = sharedViewModel,
                    onEmailChange = { sharedViewModel.passengers[index].email.value = it },
                    buttonClicked = passengersViewModel.buttonClicked,
                    index = index
                )

                if (sharedViewModel.passengers[index].email.value == "" && passengersViewModel.buttonClicked) {
                    Text(
                        text = "Email is not valid",
                        color = Color.Red,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                if (sharedViewModel.passengers[index].email.value.isNotEmpty()) {
                    if (Validation.isValidEmail(sharedViewModel.passengers[index].email.value)) {
                        Text(
                            text = "Email is valid",
                            color = Color.Blue,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    } else {
                        Text(
                            text = "Email is not valid",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                }

                //Text input "Phone Number"
                FlyNowTextField(
                    text = sharedViewModel.passengers[index].phonenumber.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    label = buildAnnotatedString {
                        append("Phone Number")
                        withStyle(Constants.superscript) {
                            append("*")
                        }
                    }.toString(),
                    placeholder = {
                        Text("(+xxx)")
                    },
                    readOnly = false,
                    onTextChange = { sharedViewModel.passengers[index].phonenumber.value = it },
                    keyboardOptions =
                    if(index + 1 == sharedViewModel.passengersCounter)
                        KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Phone)
                    else
                        KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Phone),
                    isError = (sharedViewModel.passengers[index].phonenumber.value == "" && passengersViewModel.buttonClicked)
                )
                if (index < numOfPassengers - 1) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 10.dp),
                        thickness = 1.dp,
                        color = Color(0xFF023E8A)
                    )
                }
                else {
                    Spacer(modifier = Modifier.padding(bottom = 80.dp))
                }
            }
        }
    }
}