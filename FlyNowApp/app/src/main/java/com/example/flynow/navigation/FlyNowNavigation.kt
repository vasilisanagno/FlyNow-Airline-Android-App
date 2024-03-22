package com.example.flynow.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flynow.MainViewModel
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.airports.AirportsScreen
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsScreen
import com.example.flynow.ui.screens.baggageAndPets.BaggageAndPetsViewModel
import com.example.flynow.ui.screens.baggageFromMore.BaggageScreen
import com.example.flynow.ui.screens.baggageFromMore.BaggageViewModel
import com.example.flynow.ui.screens.baggageFromMoreDetails.BaggageDetailsScreen
import com.example.flynow.ui.screens.baggageFromMoreDetails.BaggageDetailsViewModel
import com.example.flynow.ui.screens.book.BookScreen
import com.example.flynow.ui.screens.book.BookViewModel
import com.example.flynow.ui.screens.carCredentials.CarCredentialsScreen
import com.example.flynow.ui.screens.carCredentials.CarCredentialsViewModel
import com.example.flynow.ui.screens.cars.CarsScreen
import com.example.flynow.ui.screens.cars.CarsViewModel
import com.example.flynow.ui.screens.checkIn.CheckInScreen
import com.example.flynow.ui.screens.checkIn.CheckInViewModel
import com.example.flynow.ui.screens.checkInDetails.CheckInDetailsScreen
import com.example.flynow.ui.screens.checkInDetails.CheckInDetailsViewModel
import com.example.flynow.ui.screens.flights.FlightsScreen
import com.example.flynow.ui.screens.flights.FlightsViewModel
import com.example.flynow.ui.screens.home.HomeScreen
import com.example.flynow.ui.screens.more.MoreScreen
import com.example.flynow.ui.screens.more.MoreViewModel
import com.example.flynow.ui.screens.myBooking.MyBookingScreen
import com.example.flynow.ui.screens.myBooking.MyBookingViewModel
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsScreen
import com.example.flynow.ui.screens.myBookingDetails.MyBookingDetailsViewModel
import com.example.flynow.ui.screens.passengers.PassengersScreen
import com.example.flynow.ui.screens.passengers.PassengersViewModel
import com.example.flynow.ui.screens.petsFromMore.PetsScreen
import com.example.flynow.ui.screens.petsFromMore.PetsViewModel
import com.example.flynow.ui.screens.petsFromMoreDetails.PetsDetailsScreen
import com.example.flynow.ui.screens.petsFromMoreDetails.PetsDetailsViewModel
import com.example.flynow.ui.screens.seats.SeatsScreen
import com.example.flynow.ui.screens.seats.SeatsViewModel
import com.example.flynow.ui.screens.upgradeClass.UpgradeClassScreen
import com.example.flynow.ui.screens.upgradeClass.UpgradeClassViewModel
import com.example.flynow.ui.screens.upgradeClassDetails.UpgradeClassDetailsScreen
import com.example.flynow.ui.screens.upgradeClassDetails.UpgradeClassDetailsViewModel
import com.example.flynow.ui.screens.wifi.WifiScreen
import com.example.flynow.ui.screens.wifi.WifiViewModel
import com.example.flynow.ui.screens.wifiDetails.WifiDetailsScreen
import com.example.flynow.ui.screens.wifiDetails.WifiDetailsViewModel

//navigation throughout the app with the NavHost controller
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlyNowNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    sharedViewModel: SharedViewModel
) {
    //initialization of view models
    val bookViewModel = hiltViewModel<BookViewModel>()
    val flightsViewModel = hiltViewModel<FlightsViewModel>()
    val passengersViewModel = hiltViewModel<PassengersViewModel>()
    val seatsViewModel = hiltViewModel<SeatsViewModel>()
    val baggageAndPetsViewModel = hiltViewModel<BaggageAndPetsViewModel>()
    val wifiViewModel = hiltViewModel<WifiViewModel>()
    val wifiDetailsViewModel = hiltViewModel<WifiDetailsViewModel>()
    val upgradeClassViewModel = hiltViewModel<UpgradeClassViewModel>()
    val upgradeClassDetailsViewModel = hiltViewModel<UpgradeClassDetailsViewModel>()
    val baggageViewModel = hiltViewModel<BaggageViewModel>()
    val baggageDetailsViewModel = hiltViewModel<BaggageDetailsViewModel>()
    val petsViewModel = hiltViewModel<PetsViewModel>()
    val petsDetailsViewModel = hiltViewModel<PetsDetailsViewModel>()
    val carCredentialsViewModel = hiltViewModel<CarCredentialsViewModel>()
    val carsViewModel = hiltViewModel<CarsViewModel>()
    val moreViewModel = hiltViewModel<MoreViewModel>()
    val myBookingViewModel = hiltViewModel<MyBookingViewModel>()
    val myBookingDetailsViewModel = hiltViewModel<MyBookingDetailsViewModel>()
    val checkInViewModel = hiltViewModel<CheckInViewModel>()
    val checkInDetailsViewModel = hiltViewModel<CheckInDetailsViewModel>()
    NavHost(navController = navController, startDestination = Home.route) {
        composable(Home.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = true
            }
            HomeScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        composable(Book.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = true
            }
            BookScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                bookViewModel = bookViewModel
            )
        }
        composable(MyBooking.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = true
            }
            MyBookingScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                myBookingViewModel = myBookingViewModel
            )
        }
        composable(MyBookingDetails.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            MyBookingDetailsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                myBookingDetailsViewModel = myBookingDetailsViewModel,
                checkInDetailsViewModel = checkInDetailsViewModel
            )
        }
        composable(More.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = true
            }
            MoreScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                moreViewModel = moreViewModel
            )
        }
        composable(Airports.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            AirportsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        composable(CheckIn.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            CheckInScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                checkInViewModel = checkInViewModel
            )
        }
        composable(CheckInDetails.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            CheckInDetailsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                myBookingDetailsViewModel = myBookingDetailsViewModel,
                checkInDetailsViewModel = checkInDetailsViewModel
            )
        }
        composable(CarCredentials.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            CarCredentialsScreen(
                navController = navController,
                carCredentialsViewModel = carCredentialsViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(Flights.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            FlightsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                flightsViewModel = flightsViewModel
            )
        }
        composable(Passengers.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            PassengersScreen(
                navController = navController,
                passengersViewModel = passengersViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(Seats.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            SeatsScreen(
                navController = navController,
                seatsViewModel = seatsViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(BaggageAndPets.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            BaggageAndPetsScreen(
                navController = navController,
                baggageAndPetsViewModel = baggageAndPetsViewModel,
                sharedViewModel = sharedViewModel,
                seatsViewModel = seatsViewModel,
                passengersViewModel = passengersViewModel,
                flightsViewModel = flightsViewModel,
                bookViewModel = bookViewModel
            )
        }
        composable(Cars.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            CarsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                carsViewModel = carsViewModel
            )
        }
        composable(BaggageFromMore.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            BaggageScreen(
                navController = navController,
                baggageViewModel = baggageViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(BaggageFromMoreDetails.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            BaggageDetailsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                baggageDetailsViewModel = baggageDetailsViewModel,
                baggageAndPetsViewModel = baggageAndPetsViewModel
            )
        }
        composable(PetsFromMore.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            PetsScreen(
                navController = navController,
                petsViewModel = petsViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(PetsFromMoreDetails.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            PetsDetailsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                petsDetailsViewModel = petsDetailsViewModel,
                baggageAndPetsViewModel = baggageAndPetsViewModel
            )
        }
        composable(UpgradeClass.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            UpgradeClassScreen(
                navController = navController,
                upgradeClassViewModel = upgradeClassViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(UpgradeClassDetails.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            UpgradeClassDetailsScreen(
                navController = navController,
                upgradeClassDetailsViewModel = upgradeClassDetailsViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(Wifi.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            WifiScreen(
                navController = navController,
                wifiViewModel = wifiViewModel,
                sharedViewModel = sharedViewModel
            )
        }
        composable(WifiDetails.route) {
            LaunchedEffect(Unit) {
                mainViewModel.bottomBarState = false
            }
            WifiDetailsScreen(
                navController = navController,
                wifiDetailsViewModel = wifiDetailsViewModel,
                sharedViewModel = sharedViewModel
            )
        }
    }
}