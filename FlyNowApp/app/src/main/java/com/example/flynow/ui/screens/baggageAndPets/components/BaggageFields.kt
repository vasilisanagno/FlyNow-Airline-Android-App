package com.example.flynow.ui.screens.baggageAndPets.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel

//component that shows the fields about baggage what kg of baggage the user want to select
@Composable
fun BaggageFields(
    state: String,
    sharedViewModel: SharedViewModel,
    baggageAndPetsViewModel: BaggageAndPetsViewModel
) {
    baggageAndPetsViewModel.initializationVariablesInBaggageFields(
        state = state
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = if (state == "BaggageFromMore") 60.dp else 0.dp)
    )
    {
        items(sharedViewModel.passengersCounter) { index ->
            BaggageFieldsOutbound(
                state = state,
                baggageAndPetsViewModel = baggageAndPetsViewModel,
                sharedViewModel = sharedViewModel,
                index = index
            )
        }
        //***************************INBOUND*********************************
        if(if(state == "Baggage&Pets")
            sharedViewModel.page == 1
            else !sharedViewModel.oneWayInBaggage) {
            items(sharedViewModel.passengersCounter) { index ->
                BaggageFieldsInbound(
                    state = state,
                    baggageAndPetsViewModel = baggageAndPetsViewModel,
                    sharedViewModel = sharedViewModel,
                    index = index
                )
            }
        }
        else {
            items(1) {
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