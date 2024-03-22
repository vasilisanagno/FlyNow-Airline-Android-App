package com.example.flynow.model

import com.google.gson.annotations.SerializedName

//data class for flights that are direct to reach in a destination
data class BasicFlight(
    @SerializedName("flightdate") val flightDate: String,
    @SerializedName("departuretime") val departureTime: String,
    @SerializedName("arrivaltime") val arrivalTime: String,
    @SerializedName("departurecity") val departureCity: String,
    @SerializedName("arrivalcity") val arrivalCity: String,
    @SerializedName("departureairport") val departureAirp: String,
    @SerializedName("arrivalairport") val arrivalAirp: String,
    @SerializedName("duration") val flightDuration: String,
    @SerializedName("flightid") val flightId: String,
    @SerializedName("airplanemodel") val airplaneModel: String,
    @SerializedName("classtype") val classType: String
)
