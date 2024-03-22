package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.model.Buttons
import com.example.flynow.model.IsClickedBaggage
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//component that creates the icon button "+" to add new baggage
@Composable
fun AddBaggage(
    state: String,
    baggageAndPetsViewModel: BaggageAndPetsViewModel,
    sharedViewModel: SharedViewModel,
    index: Int,
    buttonIndex: Int
) {
    if (buttonIndex == baggageAndPetsViewModel.passengersBaggage[index].size - 1) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            //Button for adding new piece of baggage in specific passenger
            IconButton(onClick = {
                if(state == "Baggage&Pets") {
                    if(buttonIndex < 4) {
                        baggageAndPetsViewModel.passengersBaggage[index].add(Buttons())
                        baggageAndPetsViewModel.isClickPerPassenger[index].add(IsClickedBaggage())
                    }
                }
                else {
                    if(sharedViewModel.limitBaggageFromMore[index] < 4) {
                        sharedViewModel.limitBaggageFromMore[index] ++
                        baggageAndPetsViewModel.passengersBaggage[index].add(Buttons())
                        baggageAndPetsViewModel.isClickPerPassenger[index].add(IsClickedBaggage())
                    }
                }
            }) {
                Icon(
                    Icons.Filled.AddCircleOutline,
                    contentDescription = "addBaggage",
                    modifier = Modifier.padding(
                        start = 2.dp,
                        top = 12.dp
                    ),
                    tint = Color(0xFF023E8A)
                )
            }
            Text(
                text = "Add a new piece of baggage",
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                modifier = Modifier.padding(top = 18.dp),
            )
        }
        if(index < sharedViewModel.passengersCounter-1) {
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFF023E8A)
            )
        }
    }
}