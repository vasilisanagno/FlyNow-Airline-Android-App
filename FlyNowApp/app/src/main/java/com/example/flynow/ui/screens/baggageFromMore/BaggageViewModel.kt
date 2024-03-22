package com.example.flynow.ui.screens.baggageFromMore

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flynow.data.repository.MoreRepository
import com.example.flynow.model.PassengerInfo
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//view model class that communicates with the server to take the info about the baggage in a reservation
@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class BaggageViewModel @Inject constructor(
    private val repository: MoreRepository,
    private val baggageAndPetsViewModel: BaggageAndPetsViewModel,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    fun getBaggagePerPassenger() {
        viewModelScope.launch {
            sharedViewModel.baggagePerPassenger.clear()
            sharedViewModel.passengers.clear()
            sharedViewModel.limitBaggageFromMore.clear()
            val response = repository.getBaggagePerPassenger(sharedViewModel.textBookingId)
            sharedViewModel.passengersCounter = response.passengersCounter
            sharedViewModel.oneWayInBaggage = response.oneWayInBaggage
            if(sharedViewModel.oneWayInBaggage) {
                repeat(sharedViewModel.passengersCounter) {
                    sharedViewModel.baggagePerPassenger.add(
                        mutableStateListOf(
                            mutableIntStateOf(0),
                            mutableIntStateOf(0)
                        )
                    )
                }
            }
            else {
                repeat(sharedViewModel.passengersCounter*2) {
                    sharedViewModel.baggagePerPassenger.add(
                        mutableStateListOf(
                            mutableIntStateOf(0),
                            mutableIntStateOf(0)
                        )
                    )
                }
            }
            for(i in 1 until sharedViewModel.passengersCounter + 1) {
                sharedViewModel.passengers.add(
                    PassengerInfo(
                        mutableStateOf(""),mutableStateOf(""),
                        mutableStateOf(""),mutableStateOf(""),
                        mutableStateOf(""),mutableStateOf("")
                    )
                )
                sharedViewModel.passengers[i-1].firstname.value = response.passengers[i-1].firstname.value
                sharedViewModel.passengers[i-1].lastname.value = response.passengers[i-1].lastname.value
                sharedViewModel.passengers[i-1].gender.value = response.passengers[i-1].gender.value
                sharedViewModel.passengers[i-1].birthdate.value = response.passengers[i-1].birthdate.value
                sharedViewModel.passengers[i-1].email.value = response.passengers[i-1].email.value
                sharedViewModel.passengers[i-1].phonenumber.value = response.passengers[i-1].phonenumber.value
            }
            for(i in 0 until sharedViewModel.passengersCounter) {
                sharedViewModel.limitBaggageFromMore.add(0)
                sharedViewModel.limitBaggageFromMore[i] = response.limitBaggageFromMore[i]
            }
            if(!sharedViewModel.oneWayInBaggage) {
                for(i in 0 until sharedViewModel.passengersCounter) {
                    sharedViewModel.limitBaggageFromMore.add(0)
                    sharedViewModel.limitBaggageFromMore[i+sharedViewModel.passengersCounter] = response.limitBaggageFromMore[i]
                }
            }
            baggageAndPetsViewModel.passengersBaggage.clear()
            baggageAndPetsViewModel.isClickPerPassenger.clear()
            baggageAndPetsViewModel.oneTimeExecution = false
        }
    }
}