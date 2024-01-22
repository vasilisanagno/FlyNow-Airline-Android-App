package com.example.flynow

import androidx.compose.runtime.MutableState

//data class that is saved the info for the passengers
data class PassengerInfo(
    val gender: MutableState<String>,
    val firstname: MutableState<String>,
    val lastname: MutableState<String>,
    val birthdate: MutableState<String>,
    val email: MutableState<String>,
    val phonenumber: MutableState<String>
)