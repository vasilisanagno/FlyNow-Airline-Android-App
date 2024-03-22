package com.example.flynow.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.flynow.model.BasicFlight
import com.example.flynow.model.OneStopFlight
import java.time.LocalTime
import java.time.format.DateTimeFormatter

//class that contains functions that are useful about the duration
//of the one stop flights
class Time private constructor() {
    companion object {
        //this function finds the total hours and minutes for flights with one stop that is the adding
        //of the flight duration of the first flight + flight duration of the second flight
        //+ (time to depart the second flight - time to arrive the first flight
        @RequiresApi(Build.VERSION_CODES.O)
        fun findTotalHoursMinutes(flight: OneStopFlight?): Pair<Int, Int> {
            val time1 = flight!!.firstArrivalTime
            val time2 = flight.secondDepartureTime

            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            val localTime1 = LocalTime.parse(time1, formatter)
            val localTime2 = LocalTime.parse(time2, formatter)
            val (hour3, minutes3) = Converters.parseTime(flight.firstFlightDuration)
            val (hour4, minutes4) = Converters.parseTime(flight.secondFlightDuration)

            var hours = localTime2.hour - localTime1.hour + hour3 + hour4
            var minutes: Int
            if (localTime2.minute == 0 && localTime1.minute != 0) {
                minutes = 60 - localTime1.minute + minutes3 + minutes4
                hours -= 1
            } else {
                minutes = localTime2.minute - localTime1.minute + minutes3 + minutes4
            }
            if (minutes < 0) {
                minutes = -minutes
                hours -= 1
            }
            val totalHours = hours + (minutes / 60)
            val totalMinutes = minutes % 60
            return Pair(totalHours, totalMinutes)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun findTotalHoursMinutesMyBooking(flight1: BasicFlight?, flight2: BasicFlight?): Pair<Int,Int> {
            val time1 = flight1!!.arrivalTime
            val time2 = flight2?.departureTime

            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            val localTime1 = LocalTime.parse(time1, formatter)
            val localTime2 = LocalTime.parse(time2!!, formatter)
            val (hour3, minutes3) = Converters.parseTime(flight1.flightDuration)
            val (hour4, minutes4) = Converters.parseTime(flight2.flightDuration)

            var hours = localTime2.hour - localTime1.hour + hour3 + hour4
            var minutes: Int
            if(localTime2.minute == 0 && localTime1.minute !=0 ) {
                minutes = 60 - localTime1.minute + minutes3 + minutes4
                hours -= 1
            }
            else {
                minutes = localTime2.minute - localTime1.minute + minutes3 + minutes4
            }
            if(minutes < 0 ) {
                minutes = -minutes
                hours -= 1
            }
            val totalHours = hours + (minutes / 60)
            val totalMinutes = minutes % 60
            return Pair(totalHours,totalMinutes)
        }
    }
}