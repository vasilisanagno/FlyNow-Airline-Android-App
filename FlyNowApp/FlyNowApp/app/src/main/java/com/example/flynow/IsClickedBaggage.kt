package com.example.flynow

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

//data class that shows what baggage is clicked 23kg or 32kg
data class IsClickedBaggage(
    val isClicked23kg: MutableState<Boolean> = mutableStateOf(false),
    val isClicked32kg: MutableState<Boolean> = mutableStateOf(false)
)