package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//component that creates the icon button with trash icon to delete baggage
@Composable
fun DeleteBaggage(
    state: String,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel,
    index: Int,
    buttonIndex: Int,
    outboundOrNot: Boolean
) {
    if(buttonIndex == baggageAndPetsViewModel.passengersBaggage[index].size-1){
        Row{
            //Button for removing piece of baggage in specific passenger
            IconButton(
                modifier = Modifier.padding(top = 15.dp),
                onClick = {
                    if(baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked23kg.value){
                        baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked23kg.value = false
                        baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].firstButton.value = false
                        sharedViewModel.baggagePerPassenger[index][0].intValue -= 1
                        if(((outboundOrNot && (sharedViewModel.classTypeOutbound == "Flex" || sharedViewModel.classTypeOutbound == "Business"))||
                                    (!outboundOrNot && (sharedViewModel.classTypeInbound == "Flex" || sharedViewModel.classTypeInbound == "Business"))) && buttonIndex == 0){
                            sharedViewModel.tempBaggagePrice -= 0
                        }
                        else{
                            sharedViewModel.tempBaggagePrice -= 15
                        }
                    }
                    else if(baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked32kg.value){
                        baggageAndPetsViewModel.isClickPerPassenger[index][buttonIndex].isClicked32kg.value = false
                        sharedViewModel.baggagePerPassenger[index][1].intValue -= 1
                        baggageAndPetsViewModel.passengersBaggage[index][buttonIndex].secondButton.value = false
                        if(((outboundOrNot && sharedViewModel.classTypeOutbound == "Business")
                                    ||(!outboundOrNot && sharedViewModel.classTypeInbound == "Business")) && buttonIndex == 0){
                            sharedViewModel.tempBaggagePrice -= 0
                        }
                        else{
                            sharedViewModel.tempBaggagePrice -= 25
                        }
                    }

                    if(buttonIndex != 0) {
                        baggageAndPetsViewModel.passengersBaggage[index].removeAt(baggageAndPetsViewModel.passengersBaggage[index].size - 1)
                        if(state == "BaggageFromMore") {
                            sharedViewModel.limitBaggageFromMore[index] --
                        }
                    }
                }) {
                Icon(
                    Icons.Filled.DeleteForever,
                    contentDescription = "deleteBaggage",
                    modifier = Modifier.padding(
                        start = 2.dp,
                        top = 12.dp
                    ),
                    tint = Color.Red
                )
            }
        }
    }
}