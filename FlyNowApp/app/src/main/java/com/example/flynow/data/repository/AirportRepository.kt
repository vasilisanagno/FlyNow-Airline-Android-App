package com.example.flynow.data.repository

import android.util.Log
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.data.repository.ParsingRepository.Companion.parseAirportsJson
import com.example.flynow.model.Airport
import org.json.JSONArray
import javax.inject.Inject

//class that gets all the airports through the api
class AirportRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun getAllAirports(): List<Airport> {
        var response = JSONArray()

        try {
            response = flyNowApi.getAirports()
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }

        return parseAirportsJson(response.toString())
    }
}