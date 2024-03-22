package com.example.flynow.model

import androidx.compose.runtime.MutableState
import com.google.gson.annotations.SerializedName

//data class that is saved the info for the passengers
data class PassengerInfo(
    @SerializedName("gender") var gender: MutableState<String>,
    @SerializedName("firstname") var firstname: MutableState<String>,
    @SerializedName("lastname") var lastname: MutableState<String>,
    @SerializedName("birthdate") var birthdate: MutableState<String>,
    @SerializedName("email") var email: MutableState<String>,
    @SerializedName("phonenumber") var phonenumber: MutableState<String>
)