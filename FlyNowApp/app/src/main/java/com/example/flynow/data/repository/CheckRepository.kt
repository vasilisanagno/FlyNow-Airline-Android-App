package com.example.flynow.data.repository

import android.util.Log
import com.example.flynow.data.network.FlyNowApi
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that contains the check if the booking exists and the credentials
//that the user filled out are correct
class CheckRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun checkIfBookingExists(
        bookingId: String,
        lastname: String
    ): Boolean {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        var responseApi = JSONArray()

        jsonObject.put("lastname", lastname)
        jsonObject.put("bookingid", bookingId)
        jsonArray.put(jsonObject)

        try {
            responseApi = flyNowApi.checkBooking(jsonArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }

        return responseApi.getJSONObject(0).getBoolean("success")
    }
}