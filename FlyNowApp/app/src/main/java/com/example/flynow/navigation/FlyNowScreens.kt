package com.example.flynow.navigation

import com.example.flynow.R

//routes in the app for the different pages
interface FlyNowScreens {
    val route: String
    val icon: Int
    val title: String
}

object Home: FlyNowScreens {
    override val route = "Home"
    override val icon = R.drawable.home
    override val title = "Home"
}

object Book: FlyNowScreens {
    override val route = "Book"
    override val icon = R.drawable.book
    override val title = "Book"
}

object MyBooking: FlyNowScreens {
    override val route = "MyBooking"
    override val icon = R.drawable.mybooking
    override val title = "MyBooking"
}

object MyBookingDetails: FlyNowScreens {
    override val route = "MyBookingDetails"
    override val icon = 0
    override val title = "MyBookingDetails"
}

object More: FlyNowScreens {
    override val route = "More"
    override val icon = R.drawable.more
    override val title = "More"
}

object Airports: FlyNowScreens {
    override val route = "Airports"
    override val icon: Int = 0
    override val title = "Airports"
}

object CheckIn: FlyNowScreens {
    override val route = "CheckIn"
    override val icon: Int = 0
    override val title = "CheckIn"
}

object CheckInDetails: FlyNowScreens {
    override val route = "CheckInDetails"
    override val icon = 0
    override val title = "CheckInDetails"
}

object CarCredentials: FlyNowScreens {
    override val route = "CarCredentials"
    override val icon: Int = 0
    override val title = "CarCredentials"
}

object Flights: FlyNowScreens {
    override val route = "Flights"
    override val icon: Int = 0
    override val title = "Flights"
}

object Passengers: FlyNowScreens {
    override val route = "Passengers"
    override val icon: Int = 0
    override val title = "Passengers"
}

object Seats: FlyNowScreens {
    override val route = "Seats"
    override val icon: Int = 0
    override val title = "Seats"
}

object BaggageAndPets: FlyNowScreens {
    override val route = "BaggageAndPets"
    override val icon: Int = 0
    override val title = "BaggageAndPets"
}

object Cars: FlyNowScreens {
    override val route = "Cars"
    override val icon: Int = 0
    override val title = "Cars"
}

object BaggageFromMore: FlyNowScreens {
    override val route = "BaggageFromMore"
    override val icon: Int = 0
    override val title = "BaggageFromMore"
}

object BaggageFromMoreDetails: FlyNowScreens {
    override val route = "BaggageFromMoreDetails"
    override val icon: Int = 0
    override val title = "BaggageFromMoreDetails"
}

object PetsFromMore: FlyNowScreens {
    override val route = "PetsFromMore"
    override val icon: Int = 0
    override val title = "PetsFromMore"
}

object PetsFromMoreDetails: FlyNowScreens {
    override val route = "PetsFromMoreDetails"
    override val icon: Int = 0
    override val title = "PetsFromMoreDetails"
}

object Wifi: FlyNowScreens {
    override val route = "Wifi"
    override val icon: Int = 0
    override val title = "Wifi"
}

object WifiDetails: FlyNowScreens {
    override val route = "WifiDetails"
    override val icon: Int = 0
    override val title = "WifiDetails"
}

object UpgradeClass: FlyNowScreens {
    override val route = "UpgradeClass"
    override val icon: Int = 0
    override val title = "UpgradeClass"
}

object UpgradeClassDetails: FlyNowScreens {
    override val route = "UpgradeClassDetails"
    override val icon: Int = 0
    override val title = "UpgradeClassDetails"
}