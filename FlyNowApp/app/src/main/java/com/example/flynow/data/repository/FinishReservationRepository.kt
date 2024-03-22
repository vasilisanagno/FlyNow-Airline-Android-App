package com.example.flynow.data.repository

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flynow.data.network.FlyNowApi
import com.example.flynow.model.PassengerInfo
import com.example.flynow.model.SelectedFlightDetails
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

//class that complete the reservation and add the new reservation
//in the database with all details that the user select during this process
class FinishReservationRepository @Inject constructor(
    private val flyNowApi: FlyNowApi
) {
    suspend fun makeReservation(
        passengers: MutableList<PassengerInfo>,
        seats: MutableList<MutableList<MutableState<String>>>,
        selectedFlightOutbound: Int,
        selectedFlightInbound: Int,
        page: Int,
        numOfPassengers: Int,
        selectedFlights: MutableList<SelectedFlightDetails>,
        baggagePerPassenger: SnapshotStateList<MutableList<MutableIntState>>,
        classTypeOutbound: String,
        classTypeInbound: String,
        petSize: String,
        totalPrice: Double
    ): String {
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        val passengersJSONArray = JSONArray()
        for (i in 0 until numOfPassengers) {
            val passengerJSONObject = JSONObject()
            passengerJSONObject.put("gender", passengers[i].gender.value)
            passengerJSONObject.put("firstname", passengers[i].firstname.value)
            passengerJSONObject.put("lastname", passengers[i].lastname.value)
            passengerJSONObject.put("birthdate", passengers[i].birthdate.value)
            passengerJSONObject.put("email", passengers[i].email.value)
            passengerJSONObject.put("phonenumber", passengers[i].phonenumber.value)
            passengersJSONArray.put(passengerJSONObject)
        }
        jsonObject.put("passengers", passengersJSONArray)

        val seatsJSONArray = JSONArray()
        if (page == 0) {
            for (i in 0 until numOfPassengers) {
                val seatsId = JSONObject()
                val seatsOutbound = JSONObject()
                val seatJSONObject = JSONObject()
                seatJSONObject.put("email", passengers[i].email.value)
                if (selectedFlightOutbound == 0) {
                    seatJSONObject.put("flightid_1", selectedFlights[0].flightId.value)
                    seatJSONObject.put("seat_1", seats[i][0].value)
                    seatsId.put("direct", seatJSONObject)
                } else {
                    seatJSONObject.put("flightid_1", selectedFlights[0].flightId.value)
                    seatJSONObject.put("seat_1", seats[i][0].value)
                    seatJSONObject.put("flightid_2", selectedFlights[1].flightId.value)
                    seatJSONObject.put("seat_2", seats[i][1].value)
                    seatsId.put("oneStop", seatJSONObject)
                }
                seatsOutbound.put("outbound", seatsId)
                seatsJSONArray.put(seatsOutbound)
            }
            jsonObject.put("seats", seatsJSONArray)
        } else {
            for (i in 0 until numOfPassengers) {
                val seatsOutbound = JSONObject()
                val seatsInbound = JSONObject()
                val seatJSONObjectOutbound = JSONObject()
                val seatJSONObjectInbound = JSONObject()
                val seatsPerPassenger = JSONObject()
                seatsPerPassenger.put("email", passengers[i].email.value)

                if (selectedFlightOutbound == 0) {
                    seatJSONObjectOutbound.put("flightid_1", selectedFlights[0].flightId.value)
                    seatJSONObjectOutbound.put("seat_1", seats[i][0].value)
                    seatsOutbound.put("direct", seatJSONObjectOutbound)
                    seatsPerPassenger.put("outbound", seatsOutbound)
                    if (selectedFlightInbound == 0) {
                        seatJSONObjectInbound.put(
                            "flightid_1",
                            selectedFlights[1].flightId.value
                        )
                        seatJSONObjectInbound.put(
                            "seat_1",
                            seats[i + numOfPassengers][0].value
                        )
                        seatsInbound.put("direct", seatJSONObjectInbound)
                        seatsPerPassenger.put("inbound", seatsInbound)
                    } else {
                        seatJSONObjectInbound.put(
                            "flightid_1",
                            selectedFlights[1].flightId.value
                        )
                        seatJSONObjectInbound.put(
                            "seat_1",
                            seats[i + numOfPassengers][0].value
                        )
                        seatJSONObjectInbound.put(
                            "flightid_2",
                            selectedFlights[2].flightId.value
                        )
                        seatJSONObjectInbound.put(
                            "seat_2",
                            seats[i + numOfPassengers][1].value
                        )
                        seatsInbound.put("oneStop", seatJSONObjectInbound)
                        seatsPerPassenger.put("inbound", seatsInbound)
                    }
                } else {
                    seatJSONObjectOutbound.put("flightid_1", selectedFlights[0].flightId.value)
                    seatJSONObjectOutbound.put("seat_1", seats[i][0].value)
                    seatJSONObjectOutbound.put("flightid_2", selectedFlights[1].flightId.value)
                    seatJSONObjectOutbound.put("seat_2", seats[i][1].value)
                    seatsOutbound.put("oneStop", seatJSONObjectOutbound)
                    seatsPerPassenger.put("outbound", seatsOutbound)
                    if (selectedFlightInbound == 0) {
                        seatJSONObjectInbound.put(
                            "flightid_1",
                            selectedFlights[2].flightId.value
                        )
                        seatJSONObjectInbound.put(
                            "seat_1",
                            seats[i + numOfPassengers][0].value
                        )
                        seatsInbound.put("direct", seatJSONObjectInbound)
                        seatsPerPassenger.put("inbound", seatsInbound)
                    } else {
                        seatJSONObjectInbound.put(
                            "flightid_1",
                            selectedFlights[2].flightId.value
                        )
                        seatJSONObjectInbound.put(
                            "seat_1",
                            seats[i + numOfPassengers][0].value
                        )
                        seatJSONObjectInbound.put(
                            "flightid_2",
                            selectedFlights[3].flightId.value
                        )
                        seatJSONObjectInbound.put(
                            "seat_2",
                            seats[i + numOfPassengers][1].value
                        )
                        seatsInbound.put("oneStop", seatJSONObjectInbound)
                        seatsPerPassenger.put("inbound", seatsInbound)
                    }
                }
                seatsJSONArray.put(seatsPerPassenger)
            }
            jsonObject.put("seats", seatsJSONArray)
        }

        val baggageJSONArray = JSONArray()
        if (page == 0) {
            for (i in 0 until numOfPassengers) {
                val baggageJSONObject = JSONObject()
                val baggageJSONObjectOutbound = JSONObject()
                baggageJSONObject.put("email", passengers[i].email.value)
                baggageJSONObjectOutbound.put("baggage23kg", baggagePerPassenger[i][0].intValue)
                baggageJSONObjectOutbound.put("baggage32kg", baggagePerPassenger[i][1].intValue)
                baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                baggageJSONArray.put(baggageJSONObject)
            }
            jsonObject.put("baggage", baggageJSONArray)
        } else {
            for (i in 0 until numOfPassengers) {
                val baggageJSONObject = JSONObject()
                val baggageJSONObjectOutbound = JSONObject()
                val baggageJSONObjectInbound = JSONObject()
                baggageJSONObject.put("email", passengers[i].email.value)
                baggageJSONObjectOutbound.put("baggage23kg", baggagePerPassenger[i][0].intValue)
                baggageJSONObjectOutbound.put("baggage32kg", baggagePerPassenger[i][1].intValue)
                baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                baggageJSONObjectInbound.put(
                    "baggage23kg",
                    baggagePerPassenger[i + numOfPassengers][0].intValue
                )
                baggageJSONObjectInbound.put(
                    "baggage32kg",
                    baggagePerPassenger[i + numOfPassengers][1].intValue
                )
                baggageJSONObject.put("inbound", baggageJSONObjectInbound)
                baggageJSONArray.put(baggageJSONObject)
            }
            jsonObject.put("baggage", baggageJSONArray)
        }
        jsonObject.put("classTypeOutbound", classTypeOutbound)
        if (page == 1) {
            jsonObject.put("classTypeInbound", classTypeInbound)
        }
        jsonObject.put("petSize", petSize)
        jsonObject.put("price", totalPrice)

        jsonArray.put(jsonObject)

        val responseApi = flyNowApi.insertBooking(jsonArray)
        return if(responseApi.getJSONObject(0).getBoolean("success")) {
            responseApi.getJSONObject(0).getString("bookingId")
        } else {
            ""
        }
    }
}