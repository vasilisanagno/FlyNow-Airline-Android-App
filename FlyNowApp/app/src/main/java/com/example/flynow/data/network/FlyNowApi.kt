package com.example.flynow.data.network

import org.json.JSONArray

//interface that contains the functions for the communication with the server
interface FlyNowApi {
    suspend fun getAirports(): JSONArray
    suspend fun getFlights(data: JSONArray): JSONArray
    suspend fun getCapacity(data: JSONArray): JSONArray
    suspend fun getBookedSeats(data: JSONArray): JSONArray
    suspend fun insertBooking(data: JSONArray): JSONArray
    suspend fun checkBooking(data: JSONArray): JSONArray
    suspend fun getWifiOnBoard(data: JSONArray): JSONArray
    suspend fun updateWifiOnBoard(data: JSONArray): JSONArray
    suspend fun getClassOfFlights(data: JSONArray): JSONArray
    suspend fun updateToBusinessClass(data: JSONArray): JSONArray
    suspend fun getBaggage(data: JSONArray): JSONArray
    suspend fun updateBaggage(data: JSONArray): JSONArray
    suspend fun getPets(data: JSONArray): JSONArray
    suspend fun updatePets(data: JSONArray): JSONArray
    suspend fun checkCarCredentials(data: JSONArray): JSONArray
    suspend fun getAvailableCars(data: JSONArray): JSONArray
    suspend fun rentCar(data: JSONArray): JSONArray
    suspend fun getBookingDetails(data: JSONArray): JSONArray
    suspend fun deleteBooking(data: JSONArray): JSONArray
    suspend fun getCheckInData(data: JSONArray): JSONArray
    suspend fun updateCheckIn(data: JSONArray): JSONArray
}