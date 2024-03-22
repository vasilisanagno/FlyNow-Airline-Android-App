package com.example.flynow.model

import com.google.gson.annotations.SerializedName

//data class for flights that has one stop to reach in a destination
data class OneStopFlight(
    @SerializedName("first_flightid") val firstFlightId: String,
    @SerializedName("first_flightdate") val firstFlightDate: String,
    @SerializedName("first_flight_departuretime") val firstDepartureTime: String,
    @SerializedName("first_flight_arrivaltime") val firstArrivalTime: String,
    @SerializedName("first_flight_economyprice") val firstEconomyPrice: Double,
    @SerializedName("first_flight_flexprice") val firstFlexPrice: Double,
    @SerializedName("first_flight_businessprice") val firstBusinessPrice: Double,
    @SerializedName("first_flight_departureairport") val firstDepartureAirport: String,
    @SerializedName("first_flight_arrivalairport") val firstArrivalAirport: String,
    @SerializedName("first_flight_departurecity") val firstDepartureCity: String,
    @SerializedName("first_flight_arrivalcity") val firstArrivalCity: String,
    @SerializedName("first_flight_airplanemodel") val firstAirplaneModel: String,
    @SerializedName("first_flightduration") val firstFlightDuration: String,

    @SerializedName("second_flightid") val secondFlightId: String,
    @SerializedName("second_flightdate") val secondFlightDate: String,
    @SerializedName("second_flight_departuretime") val secondDepartureTime: String,
    @SerializedName("second_flight_arrivaltime") val secondArrivalTime: String,
    @SerializedName("second_flight_economyprice") val secondEconomyPrice: Double,
    @SerializedName("second_flight_flexprice") val secondFlexPrice: Double,
    @SerializedName("second_flight_businessprice") val secondBusinessPrice: Double,
    @SerializedName("second_flight_departureairport") val secondDepartureAirport: String,
    @SerializedName("second_flight_arrivalairport") val secondArrivalAirport: String,
    @SerializedName("second_flight_departurecity") val secondDepartureCity: String,
    @SerializedName("second_flight_arrivalcity") val secondArrivalCity: String,
    @SerializedName("second_flight_airplanemodel") val secondAirplaneModel: String,
    @SerializedName("second_flightduration") val secondFlightDuration: String
)