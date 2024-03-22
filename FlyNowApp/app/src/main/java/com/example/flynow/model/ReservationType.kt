package com.example.flynow.model

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

//data class that is saved the buttons boolean values for each flight and if is direct or one stop flight
data class ReservationType(
    val economyClassClicked: MutableState<Boolean> = mutableStateOf(false),
    val flexClassClicked: MutableState<Boolean> = mutableStateOf(false),
    val businessClassClicked: MutableState<Boolean> = mutableStateOf(false),
    val directOrOneStop: MutableIntState = mutableIntStateOf(0)
)