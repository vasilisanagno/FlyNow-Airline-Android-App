package com.example.flynow.ui.screens.seats.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.flynow.ui.screens.seats.SeatsViewModel
import kotlinx.coroutines.delay

//component that generates the seat list accordingly the capacity of the airplane of a flight
//removing the seats that is already taken for other passengers
@SuppressLint("MutableCollectionMutableState")
@Composable
fun GenerateSeatList(
    seatsViewModel: SeatsViewModel,
    flightId: MutableState<String>,
    seatList: MutableList<String>,
    airplaneModel: MutableState<String>,
    isClickedSeat: MutableList<Boolean>,
    index: Int
) {
    var capacity by remember {
        mutableIntStateOf(0)
    }
    var bookedSeats by remember {
        mutableStateOf(mutableListOf<String>())
    }
    //api that takes the capacity of the airplane and creates the list of seats
    LaunchedEffect(Unit) {
        seatsViewModel.fetchAirplaneCapacityFromApi(airplaneModel.value)
        delay(1500)
        capacity = seatsViewModel.capacity[index]
    }

    var index1 = 0
    for (row in 1..capacity/6) {
        for (seatChar in 'A'..'F') {
            val seat = "$row$seatChar"
            seatList.add("")
            seatList[index1] = seat
            index1++
        }
    }
    repeat(capacity) {
        isClickedSeat.add(false)
    }

    //api that takes the not available seats and store them in a list
    LaunchedEffect(Unit) {
        seatsViewModel.fetchBookedSeatsFromApi(flightId.value)
        delay(1000)
        bookedSeats = seatsViewModel.bookedSeats[index]
    }
    //removes the not available seats from the capacity of the airplane to remain finally
    //only the available seats inside the airplane of a flight
    seatList.removeAll { seat ->
        seat in bookedSeats
    }
}