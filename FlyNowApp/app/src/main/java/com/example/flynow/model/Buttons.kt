package com.example.flynow.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

//buttons for 23kg, 32kg for the baggage and pets screen that shows if they are clicked
data class Buttons(
    val firstButton: MutableState<Boolean> = mutableStateOf(false),
    val secondButton: MutableState<Boolean> = mutableStateOf(false)
)