package com.example.flynow

//routes in the app for the different pages
interface Destinations {
    val route: String
    val icon: Int
    val title: String
}

object Home: Destinations {
    override val route = "Home"
    override val icon = R.drawable.home
    override val title = "Home"
}

object Book: Destinations {
    override val route = "Book"
    override val icon = R.drawable.book
    override val title = "Book"
}

object MyBooking: Destinations {
    override val route = "MyBooking"
    override val icon = R.drawable.mybooking
    override val title = "MyBooking"
}

object MyBookingDetails: Destinations {
    override val route = "MyBookingDetails"
    override val icon = 0
    override val title = "MyBookingDetails"
}

object More: Destinations {
    override val route = "More"
    override val icon = R.drawable.more
    override val title = "More"
}

object Airports: Destinations {
    override val route = "Airports"
    override val icon: Int = 0
    override val title = "Airports"
}

object CheckIn: Destinations {
    override val route = "CheckIn"
    override val icon: Int = 0
    override val title = "CheckIn"
}

object CheckInDetails: Destinations {
    override val route = "CheckInDetails"
    override val icon = 0
    override val title = "CheckInDetails"
}

object Car: Destinations {
    override val route = "Car"
    override val icon: Int = 0
    override val title = "Car"
}

object Flights: Destinations {
    override val route = "Flights"
    override val icon: Int = 0
    override val title = "Flights"
}

object Passengers: Destinations {
    override val route = "Passengers"
    override val icon: Int = 0
    override val title = "Passengers"
}

object Seats: Destinations {
    override val route = "Seats"
    override val icon: Int = 0
    override val title = "Seats"
}

object BaggageAndPets: Destinations {
    override val route = "BaggageAndPets"
    override val icon: Int = 0
    override val title = "BaggageAndPets"
}

object SearchingCars: Destinations {
    override val route = "SearchingCars"
    override val icon: Int = 0
    override val title = "SearchingCars"
}

object BaggageFromMore: Destinations {
    override val route = "BaggageFromMore"
    override val icon: Int = 0
    override val title = "BaggageFromMore"
}

object PetsFromMore: Destinations {
    override val route = "PetsFromMore"
    override val icon: Int = 0
    override val title = "PetsFromMore"
}

object WifiOnBoard: Destinations {
    override val route = "WifiOnBoard"
    override val icon: Int = 0
    override val title = "WifiOnBoard"
}

object UpgradeClass: Destinations {
    override val route = "UpgradeClass"
    override val icon: Int = 0
    override val title = "UpgradeClass"
}