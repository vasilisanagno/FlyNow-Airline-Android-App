//In this screen the passengers of the reservation, that is being processed,
//fill in their personal information
package com.example.flynow

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.navigation.NavController

//Superscript for the "*" that means mandatory field
val superscript = SpanStyle(
    baselineShift = BaselineShift.Superscript,
    fontSize = 16.sp
)


//function that creates the passengers screen
//navController helps to navigate to previous page or next page,
//passengersCount is the number of passengers in the booking,
//passengers, selectedFlights are the list that
//is saved the information about passengers and selected flights
//total price is from previous page that is flights screen so it is the price for the flight that is selected,
//the two variables with class buttons contains all the buttons of the outbound and inbound flights,
//classTypeOutbound, classTypeInbound is for the class of the flight that is selected
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PassengersScreen(navController: NavController,
                     totalPrice: MutableDoubleState,
                     passengersCount: MutableIntState,
                     passengers: MutableList<PassengerInfo>,
                     listOfClassButtonsOutbound: MutableList<ReservationType>,
                     listOfClassButtonsInbound: MutableList<ReservationType>,
                     selectedFlights: MutableList<SelectedFlightDetails>,
                     classTypeOutbound: MutableState<String>,
                     classTypeInbound: MutableState<String>
){

    val numOfPassengers = passengersCount.intValue
    if(passengers.size == 0) {
        repeat(numOfPassengers) {
            val passenger = PassengerInfo(
                mutableStateOf(""), mutableStateOf(""),
                mutableStateOf(""), mutableStateOf(""),
                mutableStateOf(""), mutableStateOf("")
            )
            passengers.add(passenger)
        }
    }
    val buttonClicked = remember {
        mutableStateOf(false)
    }
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val checkForEmptyInputFields = remember {
        mutableStateOf(false)
    }
    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        BottomAppBar(backgroundColor = Color.Transparent,
            contentColor = Color.Transparent,
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    "Total Price: ${totalPrice.doubleValue} €",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    modifier = Modifier.padding(start = 5.dp)
                )
                //Button "Continue" that navigates the user to the next step of the reservation
                Button(
                    onClick = {
                        buttonClicked.value = true
                        checkForEmptyInputFields.value = false
                        passengers.forEachIndexed{ index, passenger ->
                            if((index + 1 <= numOfPassengers) &&
                                (passenger.gender.value == "" ||
                                        passenger.firstname.value == "" ||
                                        passenger.lastname.value == "" ||
                                        passenger.birthdate.value == "" ||
                                        passenger.email.value == "" ||
                                        passenger.phonenumber.value == "")) {
                                checkForEmptyInputFields.value = true
                            }
                        }
                        if(!checkForEmptyInputFields.value) {
                            navController.navigate(Seats.route) {
                                popUpTo(Passengers.route)
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(start = 45.dp)
                        .width(130.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00B4D8),
                        disabledContentColor = Color(0xFF023E8A)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp
                    )
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                }
            }
        }
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //"Back" button
                IconButton(onClick = {
                    passengers.clear()
                    listOfClassButtonsOutbound.clear()
                    listOfClassButtonsInbound.clear()
                    classTypeInbound.value = ""
                    classTypeOutbound.value = ""
                    selectedFlights.forEach { flight ->
                        flight.flightId.value = ""
                        flight.departureCity.value = ""
                        flight.arrivalCity.value = ""
                        flight.airplaneModel.value = ""
                    }
                    navController.navigate(Flights.route) {
                        popUpTo(Passengers.route)
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        Icons.Outlined.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF023E8A)
                    )
                }
                //"Passengers" text field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Passengers",
                        fontSize = 22.sp,
                        modifier = Modifier,
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
                    Icon(
                        Icons.Filled.People,
                        contentDescription = "passengers",
                        modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 5.dp),
                        tint = Color(0xFF023E8A)
                    )
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))

            //Text input fields for each passenger's personal information
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
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
                    GenderDropdownMenu(buttonClicked, passengers[index].gender)

                    //Text Input "First name"
                    OutlinedTextField(
                        value = passengers[index].firstname.value,
                        onValueChange = { passengers[index].firstname.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        label = {
                            Text(
                                fontSize = 16.sp,
                                text = buildAnnotatedString {
                                    append("First Name")
                                    withStyle(superscript) {
                                        append("*")
                                    }
                                }
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = Color(0xFF023E8A),
                            focusedBorderColor = Color(0xFF023E8A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFF00B4D8),
                            errorContainerColor = Color.White,
                            cursorColor = Color(0xFF023E8A)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                        isError = (passengers[index].firstname.value == "" && buttonClicked.value)
                    )

                    //Text input "Last Name"
                    OutlinedTextField(
                        value = passengers[index].lastname.value,
                        onValueChange = { passengers[index].lastname.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        label = {
                            Text(
                                fontSize = 16.sp,
                                text = buildAnnotatedString {
                                    append("Last Name")
                                    withStyle(superscript) {
                                        append("*")
                                    }
                                }
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = Color(0xFF023E8A),
                            focusedBorderColor = Color(0xFF023E8A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFF00B4D8),
                            errorContainerColor = Color.White,
                            cursorColor = Color(0xFF023E8A)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                        isError = (passengers[index].lastname.value == "" && buttonClicked.value)
                    )

                    //Select "BirthDate" calendar
                    BirthDatePickerDialog(birthDate = passengers[index].birthdate, buttonClicked)

                    //Text input "Email"
                    EmailTextField(
                        email = passengers[index].email,
                        onEmailChange = { passengers[index].email.value = it },
                        buttonClicked
                    )

                    if (passengers[index].email.value == "" && buttonClicked.value) {
                        Text(
                            text = "Email is not valid",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                    if (passengers[index].email.value.isNotEmpty()) {
                        if (isValidEmail(passengers[index].email.value)) {
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
                    OutlinedTextField(
                        value = passengers[index].phonenumber.value,
                        onValueChange = { passengers[index].phonenumber.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        label = {
                            Text(
                                fontSize = 16.sp,
                                text = buildAnnotatedString {
                                    append("Phone Number")
                                    withStyle(superscript) {
                                        append("*")
                                    }
                                }
                            )
                        },
                        placeholder = {
                            Text("(+xxx)")
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = Color(0xFF023E8A),
                            focusedBorderColor = Color(0xFF023E8A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFF00B4D8),
                            errorContainerColor = Color.White,
                            cursorColor = Color(0xFF023E8A)
                        ),
                        keyboardOptions =
                        if(index + 1 == passengersCount.intValue)
                            KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Phone)
                        else
                            KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Phone),
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
                        isError = (passengers[index].phonenumber.value == "" && buttonClicked.value)
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
}

//function that creates the dropdown menu for the gender selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownMenu(buttonClicked: MutableState<Boolean>, gender: MutableState<String>) {
    val isExpanded = remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = { newValue ->
            isExpanded.value = newValue
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        OutlinedTextField(
            value = gender.value,
            onValueChange = {},
//            value = gender.value,
//            onValueChange = {gender.value = it
//                            Log.d("gender", gender.value)},
            label = {Text(
                fontSize = 16.sp,
                text = buildAnnotatedString {
                    append("Gender")
                    withStyle(superscript) {
                        append("*")
                    }
                })},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
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
            textStyle = TextStyle.Default.copy(fontSize = 18.sp,
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
            isError = (gender.value == "" && buttonClicked.value)
        )

        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = {
                isExpanded.value = false
            },
            modifier = Modifier
                .width(200.dp)
                .background(color = Color.White)
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Male",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ))
                },
                onClick = {
                    gender.value = "Male"
                    isExpanded.value = false
                },
                modifier = Modifier.background(color = Color.White)
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Female",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ))
                },
                onClick = {
                    gender.value = "Female"
                    isExpanded.value = false
                },
                modifier = Modifier.background(color = Color.White)
            )
        }
    }
}

//function tha converts milliseconds to the date format below
@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

//function that creates the calendar for the birthdate selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDatePicker(
    onSelectedDate: (String) -> Unit,
    onDismiss: () -> Unit) {

    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val utcDate = convertMillisToDate(utcTimeMillis)
                val currentDate = convertMillisToDate(System.currentTimeMillis())

                return  (((utcDate.substring(3,5).toInt() == currentDate.substring(3,5).toInt()
                        &&
                        utcDate.substring(0,2).toInt() <= currentDate.substring(0,2).toInt()
                        ||
                        utcDate.substring(3,5).toInt() < currentDate.substring(3,5).toInt()
                        )
                        &&
                        utcDate.substring(6,10).toInt() == currentDate.substring(6,10).toInt()
                        )
                        ||
                        utcDate.substring(6,10).toInt() < currentDate.substring(6,10).toInt())

            }
        })
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    onSelectedDate(selectedDate)
                    onDismiss()
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(end = 90.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
                Text(text = "OK",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF023E8A)
                )
            ) {
                Text(text = "Cancel",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color(0xFFEBF2FA)
        ),
        modifier = Modifier.scale(scaleX = 0.9f, scaleY = 0.9f)
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .background(color = Color(0xFFEBF2FA))
                .height(250.dp),
            title = {
                Text(
                    text = "Select your birthdate",
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    fontSize = 24.sp,
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
                currentYearContentColor = Color(0xFF023E8A),
                dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF023E8A),
                    focusedBorderColor = Color(0xFF023E8A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFF4361EE),
                    cursorColor = Color(0xFF023E8A)
                )
            )
        )
    }
}


//function that displays the birthdate text input with the calendar
@Composable
fun BirthDatePickerDialog(birthDate: MutableState<String>,
                          buttonClicked: MutableState<Boolean>
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = birthDate.value,
        onValueChange = {birthDate.value = it},
        modifier = Modifier
            .width(500.dp)
            .padding(
                start = 10.dp,
                top = 10.dp,
                bottom = 5.dp,
                end = 10.dp
            )
            .clickable(enabled = false, onClickLabel = null, onClick = {}),
        label = {Text(
            fontSize = 16.sp,
            text = buildAnnotatedString {
                append("Birthdate")
                withStyle(superscript) {
                    append("*")
                }
            }
        )},
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = Color(0xFF023E8A),
            focusedBorderColor = Color(0xFF023E8A),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFF00B4D8),
            errorContainerColor = Color.White
        ),
        leadingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painterResource(id = R.drawable.calendar),
                    contentDescription = "calendar",
                    tint = Color(0xFF00B4D8)
                )
            }
        },
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
        readOnly = true,
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
        isError = (birthDate.value == "" && buttonClicked.value)
    )
    if (showDatePicker) {
        BirthDatePicker(
            onSelectedDate = {birthDate.value = it},
            onDismiss = { showDatePicker = false}
        )
    }
}

//function that creates the email text field
@Composable
fun EmailTextField(email: MutableState<String>, onEmailChange: (String) -> Unit, buttonClicked: MutableState<Boolean>) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        value = email.value,
        onValueChange = { onEmailChange(it) },
        label = { Text(
            fontSize = 16.sp,
            text = buildAnnotatedString {
                append("Email")
                withStyle(superscript) {
                    append("*")
                }
            }
        )},
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email),
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = Color(0xFF023E8A),
            focusedBorderColor = Color(0xFF023E8A),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFF00B4D8),
            errorContainerColor = Color.White,
            cursorColor = Color(0xFF023E8A)
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
        isError = (email.value == "" && buttonClicked.value)
    )
}

//function that checks if the email is valid
fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailRegex)
}