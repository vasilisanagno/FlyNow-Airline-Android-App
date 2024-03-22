package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//component that contains the info about the baggage fields in inbound
@Composable
fun BaggageFieldsInbound(
    state: String,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel,
    index: Int
) {
    if (index == 0) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color(0xFF023E8A)
        )
        Text(
            text = "Inbound",
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
    Text(
        text =
        if (sharedViewModel.passengers[index].gender.value == "Female")
            "Mrs ${sharedViewModel.passengers[index].firstname.value} ${sharedViewModel.passengers[index].lastname.value}"
        else
            "Mr ${sharedViewModel.passengers[index].firstname.value} ${sharedViewModel.passengers[index].lastname.value}",
        fontSize = 20.sp,
        modifier = Modifier.padding(
            start = 10.dp,
            top = if (index == 0) 10.dp else 5.dp
        ),
        fontFamily = FontFamily(
            fonts = listOf(
                Font(
                    resId = R.font.opensans
                )
            )
        ),
        color = Color(0xFF023E8A),
        fontWeight = FontWeight.Bold
    )
    if(state == "Baggage&Pets" || (state == "BaggageFromMore" && sharedViewModel.limitBaggageFromMore[index + sharedViewModel.passengersCounter] < 5)) {
        Column {
            baggageAndPetsViewModel.passengersBaggage[index + sharedViewModel.passengersCounter].forEachIndexed { buttonIndex, _ ->
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        Text(
                            text = "Baggage ${buttonIndex + 1}", fontSize = 16.sp,
                            modifier = Modifier.padding(
                                top = 32.dp,
                                end = 5.dp
                            ),
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
                    Baggage23Kg(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index + sharedViewModel.passengersCounter,
                        buttonIndex = buttonIndex,
                        outboundOrNot = false
                    )
                    Baggage32Kg(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index + sharedViewModel.passengersCounter,
                        buttonIndex = buttonIndex,
                        outboundOrNot = false
                    )
                    DeleteBaggage(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index + sharedViewModel.passengersCounter,
                        buttonIndex = buttonIndex,
                        outboundOrNot = false
                    )
                }
                if (buttonIndex == baggageAndPetsViewModel.passengersBaggage[index + sharedViewModel.passengersCounter].size - 1) {
                    AddBaggage(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index + sharedViewModel.passengersCounter,
                        buttonIndex = buttonIndex
                    )
                    if(index < sharedViewModel.passengersCounter-1) {
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color(0xFF023E8A)
                        )
                    }
                    else {
                        if (state != "BaggageFromMore") {
                            ShowPetField(
                                state = state,
                                baggageAndPetsViewModel = baggageAndPetsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    }
                }
            }
        }
    }
    else {
        Text(
            text = "The passenger has already selected the maximum number of baggage pieces!",
            fontSize = 16.sp,
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            color = Color(0xFF023E8A),
            modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 18.dp),
        )
        if (index < sharedViewModel.passengersCounter - 1) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFF023E8A)
            )
        }
    }
}