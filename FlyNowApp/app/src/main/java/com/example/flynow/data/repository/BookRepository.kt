package com.example.flynow.data.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.flynow.data.model.FlightsResponse
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.data.repository.ParsingRepository.Companion.parseDirectJson
import com.example.flynow.data.repository.ParsingRepository.Companion.parseOneStopJson
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that gets all the flights according the use inputs through the api
class BookRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun getAllFlights(
        airportFrom: String,
        airportTo: String,
        departureDate: String,
        returnDate: String,
        checked: Boolean,
        amChecked: Boolean,
        pmChecked: Boolean,
        passengersCounter: Int
    ): FlightsResponse {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val details = JSONObject()
        val responseApi: JSONArray
        val response = FlightsResponse()

        details.put("from", airportFrom)
        details.put("to", airportTo)
        details.put("departureDate", departureDate)
        details.put("returnDate", returnDate)
        details.put("directFlights", checked)
        details.put("amFlights", amChecked)
        details.put("pmFlights", pmChecked)
        details.put("passengersCount", passengersCounter)
        jsonArray.put(details)

        try {
            responseApi = flyNowApi.getFlights(jsonArray)

            //store the data to lists
            val oneWayDirect = parseDirectJson(responseApi.getJSONObject(0).get("oneWayDirectResult").toString())
            if (oneWayDirect != null) {
                response.oneWayDirectFlights = oneWayDirect
            }
            else {
                response.oneWayDirectFlights = mutableStateListOf()
            }
            val oneWayOneStop = parseOneStopJson(responseApi.getJSONObject(1).get("oneWayOneStopResult").toString())
            if (oneWayOneStop != null) {
                response.oneWayOneStopFlights = oneWayOneStop
            }
            else {
                response.oneWayOneStopFlights = mutableStateListOf()
            }
            val returnDirect = parseDirectJson(responseApi.getJSONObject(2).get("returnDirectResult").toString())
            if (returnDirect != null) {
                response.returnDirectFlights = returnDirect
            }
            else {
                response.returnDirectFlights = mutableStateListOf()
            }
            val returnOneStop = parseOneStopJson(responseApi.getJSONObject(3).get("returnOneStopResult").toString())
            if (returnOneStop != null) {
                response.returnOneStopFlights = returnOneStop
            }
            else {
                response.returnOneStopFlights = mutableStateListOf()
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return response
    }
}