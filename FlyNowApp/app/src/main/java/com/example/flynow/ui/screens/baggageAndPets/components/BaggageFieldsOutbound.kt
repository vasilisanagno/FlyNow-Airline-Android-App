package com.example.flynow.ui.screens.baggageAndPets.components

import android.util.Log
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

//component that contains the info about the baggage fields in outbound
@Composable
fun BaggageFieldsOutbound(
    state: String,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel,
    index: Int
) {
    if (index == 0) {
        FirstBaggageInfo(state = state)
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
    if(state == "Baggage&Pets" || (state == "BaggageFromMore" && sharedViewModel.limitBaggageFromMore[index] < 5)) {
        Column {
            Log.d("Baggage", sharedViewModel.limitBaggageFromMore.size.toString()+baggageAndPetsViewModel.passengersBaggage.size.toString())
            baggageAndPetsViewModel.passengersBaggage[index].forEachIndexed{ buttonIndex,_ ->
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
                        index = index,
                        buttonIndex = buttonIndex,
                        outboundOrNot = true
                    )
                    Baggage32Kg(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index,
                        buttonIndex = buttonIndex,
                        outboundOrNot = true
                    )
                    DeleteBaggage(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index,
                        buttonIndex = buttonIndex,
                        outboundOrNot = true
                    )
                }
                if (buttonIndex == baggageAndPetsViewModel.passengersBaggage[index].size - 1) {
                    AddBaggage(
                        state = state,
                        baggageAndPetsViewModel = baggageAndPetsViewModel,
                        sharedViewModel = sharedViewModel,
                        index = index,
                        buttonIndex = buttonIndex
                    )
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