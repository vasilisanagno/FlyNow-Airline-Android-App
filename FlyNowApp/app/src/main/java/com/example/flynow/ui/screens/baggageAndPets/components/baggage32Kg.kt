package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//component that creates an outlined button for baggage 32kg that shows the price inside
//according the class of the flight that select the user
@Composable
fun Baggage32Kg(
    state: String,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel,
    index: Int,
    buttonIndex: Int,
    outboundOrNot: Boolean
) {
    Column {
        Row {
            Text(
                text = "32kg",
                fontSize = 16.sp,
                modifier = Modifier.padding(
                    start = 25.dp,
                    top = 3.dp
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
            Icon(
                Icons.Filled.Luggage,
                contentDescription = "Luggage",
                modifier = Modifier.padding(
                    start = 2.dp
                ),
                tint = Color(0xFF023E8A)
            )
        }
        OutlinedButton(
            onClick = {
                if(!baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked32kg.value) {
                    if(baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked23kg.value){
                        baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked23kg.value = false
                        sharedViewModel.baggagePerPassenger[index][0].intValue -= 1
                        if(((outboundOrNot && (sharedViewModel.classTypeOutbound == "Flex" || sharedViewModel.classTypeOutbound == "Business"))||
                                    (!outboundOrNot && (sharedViewModel.classTypeInbound == "Flex" || sharedViewModel.classTypeInbound == "Business"))) && buttonIndex == 0){
                            sharedViewModel.tempBaggagePrice -= 0
                        }
                        else {
                            sharedViewModel.tempBaggagePrice -= 15
                        }
                    }
                    baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked32kg.value = true
                    sharedViewModel.baggagePerPassenger[index][1].intValue += 1
                    if(((outboundOrNot && sharedViewModel.classTypeOutbound == "Business")
                                ||(!outboundOrNot && sharedViewModel.classTypeInbound == "Business")) && buttonIndex == 0){
                        sharedViewModel.tempBaggagePrice += 0
                    }
                    else{
                        sharedViewModel.tempBaggagePrice += 25
                    }
                }
                if(!baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].secondButton.value){
                    baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].secondButton.value =
                        !baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].secondButton.value
                }
                baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].firstButton.value = false
            },
            modifier = Modifier
                .width(100.dp)
                .height(40.dp)
                .padding(
                    start = 5.dp,
                    top = 2.dp,
                    end = 5.dp
                ),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor =
                if (baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].secondButton.value) Color(0xFF023E8A)
                else if (baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].firstButton.value) Color.Transparent
                else Color.Transparent,
                contentColor = if (baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].secondButton.value) Color.White else Color(
                    0xFF023E8A
                )
            )
        ) {
            Text(
                text =
                if (buttonIndex == 0 && state != "BaggageFromMore")
                    if(outboundOrNot) {
                        baggageAndPetsViewModel.baggage32kgPriceOutbound
                    }
                    else {
                        baggageAndPetsViewModel.baggage32kgPriceInbound
                    }
                else "25€",
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