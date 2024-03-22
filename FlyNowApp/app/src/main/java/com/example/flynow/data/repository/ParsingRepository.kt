package com.example.flynow.data.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.model.Airport
import com.example.flynow.model.BaggageAndSeatPerPassenger
import com.example.flynow.model.BasicFlight
import com.example.flynow.model.CarDetailsMyBooking
import com.example.flynow.model.DirectFlight
import com.example.flynow.model.OneStopFlight
import com.example.flynow.model.PassengerInfo
import com.example.flynow.utils.Converters.Companion.byteArrayToBitmap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//class that contains function to parse different json objects
//from responses of the server
class ParsingRepository {
    companion object {
        //function that parse the data that returns the query and storing them to a list
        //that is a type of Airport data class
        fun parseAirportsJson(jsonString: String): List<Airport> {
            val gson = Gson()
            return gson.fromJson(jsonString, Array<Airport>::class.java).toList()
        }

        //function that parse the data from the database and storing them to a list
        //that is a type of DirectFlight data class
        fun parseDirectJson(jsonString: String): SnapshotStateList<DirectFlight?>? {
            if(jsonString != "null" && jsonString != "[]") {
                val gson = Gson()
                val type = object : TypeToken<SnapshotStateList<DirectFlight?>?>() {}.type
                return gson.fromJson<SnapshotStateList<DirectFlight?>?>(jsonString, type)
            }
            return null
        }

        //function that parse the data from the database and storing them to a list
        //that is a type of OneStopFlight data class
        fun parseOneStopJson(jsonString: String): SnapshotStateList<OneStopFlight?>? {
            if(jsonString != "null" && jsonString != "[]") {
                Log.d("gary", jsonString)
                val gson = Gson()
                val type = object : TypeToken<SnapshotStateList<OneStopFlight?>?>() {}.type
                return gson.fromJson<SnapshotStateList<OneStopFlight?>?>(jsonString, type)
            }
            return null
        }

        //function that parse the info for the passengers and save them in a list that is type of the data class PassengerInfo
        fun parsePassengersJson(jsonString: String): SnapshotStateList<PassengerInfo?>? {
            if(jsonString != "null" && jsonString != "[]") {
                val gson = GsonBuilder()
                    .registerTypeAdapter(MutableState::class.java, MutableStateDeserializer())
                    .create()
                val type = object : TypeToken<SnapshotStateList<PassengerInfo?>?>() {}.type
                return gson.fromJson<SnapshotStateList<PassengerInfo?>?>(jsonString, type)
            }
            return null
        }

        //is useful for gson library to deserialize in MutableState<String>
        class MutableStateDeserializer: JsonDeserializer<MutableState<String>> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): MutableState<String> {
                val stringValue = json?.asString
                return mutableStateOf(stringValue ?: "")
            }
        }

        //function that parse the info for the flights and save them in a list that is type of the data class BasicFlight
        fun parseFlightsJson(jsonString: String): SnapshotStateList<BasicFlight?>? {
            if(jsonString != "null" && jsonString != "[]") {
                val gson = Gson()
                val type = object : TypeToken<SnapshotStateList<BasicFlight?>?>() {}.type
                return gson.fromJson<SnapshotStateList<BasicFlight?>?>(jsonString, type)
            }
            return null
        }

        //function that parse the info for the seats and baggage
        //and save them in a list that is type of the data class BaggageAndSeatPerPassenger
        fun parseBaggageAndSeatPerPassengerJson(jsonString: String): SnapshotStateList<BaggageAndSeatPerPassenger?>? {
            if(jsonString != "null" && jsonString != "[]") {
                val gson = Gson()
                val type = object : TypeToken<SnapshotStateList<BaggageAndSeatPerPassenger?>?>() {}.type
                return gson.fromJson<SnapshotStateList<BaggageAndSeatPerPassenger?>?>(jsonString, type)
            }
            return null
        }

        //is useful for gson library to deserialize in Bitmap
        class BitmapDeserializer : JsonDeserializer<Bitmap> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Bitmap {
                val base64ImageData = json.asString
                val decodedBytes: ByteArray = Base64.decode(base64ImageData, Base64.DEFAULT)
                return byteArrayToBitmap(decodedBytes)
            }
        }

        //function that parse the info for the cars
        //and save them in a list that is type of the data class CarDetailsMyBooking
        fun parseCarMyBookingJson(
            jsonString: String
        ): SnapshotStateList<CarDetailsMyBooking?>? {
            if(jsonString != "null" && jsonString != "[]") {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Bitmap::class.java, BitmapDeserializer())
                    .create()
                val type = object : TypeToken<SnapshotStateList<CarDetailsMyBooking?>?>() {}.type
                return gson.fromJson<SnapshotStateList<CarDetailsMyBooking?>?>(jsonString, type)
            }
            return null
        }
    }
}