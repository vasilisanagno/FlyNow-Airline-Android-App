package com.example.flynow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.utils.Constants

//component that is used in FlyNowCredentials component
//and shows the image, text fields, the "continue" button
//and the progress indicator
@Composable
fun CredentialsInfo(
    state: String,
    sharedViewModel: SharedViewModel,
    checkBooking: MutableState<Boolean>
) {
    Column(
        Modifier
            .background(Constants.gradient)
            .fillMaxSize()
    ) {
        Image(
            painter = when (state) {
                "PetsFromMore" -> painterResource(id = R.drawable.pet)
                "BaggageFromMore" -> painterResource(id = R.drawable.baggage)
                "Wifi" -> painterResource(id = R.drawable.wifionboard)
                "UpgradeClass" -> painterResource(id = R.drawable.upgradeclass)
                "MyBooking" -> painterResource(id = R.drawable.mybook)
                else -> painterResource(id = R.drawable.checkin)
            },
            contentDescription = "many screens",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = if (state == "Wifi") 2.6f else if (state == "PetsFromMore") 2.7f else 3f)
                .padding(start = 10.dp, top = 5.dp, end = 10.dp)
        )
        if(!sharedViewModel.showProgressBar) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                //text input for the booking reference
                FlyNowTextField(
                    text = sharedViewModel.textBookingId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    label = "Booking reference",
                    readOnly = false,
                    onTextChange = {
                        sharedViewModel.textBookingId = it
                        sharedViewModel.buttonClickedCredentials = false
                        sharedViewModel.hasError = false
                        sharedViewModel.checkInOpen = true
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AirplaneTicket,
                            contentDescription = "bookingId",
                            tint = Color(0xFF00B4D8)
                        )
                    },
                    isError = (sharedViewModel.buttonClickedCredentials && sharedViewModel.textBookingId == "" || sharedViewModel.hasError)
                )
                //text input for the last name
                FlyNowTextField(
                    text = sharedViewModel.textLastname,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                    label = "Last name",
                    readOnly = false,
                    onTextChange = {
                        sharedViewModel.textLastname = it
                        sharedViewModel.buttonClickedCredentials = false
                        sharedViewModel.hasError = false
                        sharedViewModel.checkInOpen = true
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.passenger),
                            contentDescription = "lastname",
                            tint = Color(0xFF00B4D8)
                        )
                    },
                    isError = (sharedViewModel.buttonClickedCredentials && sharedViewModel.textLastname == "" || sharedViewModel.hasError)
                )
                //error if the booking is wrong or does not exist
                if (sharedViewModel.hasError) {
                    Text(
                        text = "Incorrect booking reference or lastname. Please check your details and try again.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color.Red
                    )
                }
                //if the check-in is not available yet throws an error message
                if (!sharedViewModel.checkInOpen) {
                    Text(
                        text = "Check-in is available 24 hours to 30 minutes before your departure.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color.Red
                    )
                }
                //button to continue to alert dialog
                FlyNowButton(
                    text = "Continue",
                    modifier = Modifier.padding(top = 50.dp),
                    onClick = {
                        sharedViewModel.buttonClickedCredentials = true
                        checkBooking.value = true
                    }
                )
            }
        }
        else {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 100.dp),
                    color = Color(0xFF023E8A)
                )
            }
        }
    }
}