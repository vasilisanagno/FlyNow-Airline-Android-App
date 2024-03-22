package com.example.flynow

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flynow.navigation.FlyNowNavigation
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.components.FlyNowBottomNavigation
import dagger.hilt.android.AndroidEntryPoint

//main activity that starts the app
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for splash screen to apply the correct icon and some delay
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.loading.value
            }
        }

        setContent {
            FlyNowApp(mainViewModel = hiltViewModel<MainViewModel>())
        }
        @SuppressLint("SourceLockedOrientationActivity")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

//the center App that start the whole app with different pages
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlyNowApp(mainViewModel: MainViewModel = hiltViewModel()) {
    //initialization of shared view model
    val sharedViewModel = hiltViewModel<SharedViewModel>()
    //navController to navigate to different pages
    val navController = rememberNavController()
    //navigation between pages and connection with .kt files for each route
    Scaffold(bottomBar = {
        FlyNowBottomNavigation(
            navController = navController,
            mainViewModel = mainViewModel,
            sharedViewModel = sharedViewModel
        )
    }) {
        Box(Modifier.padding(it)) {
            FlyNowNavigation(
                navController = navController,
                mainViewModel = mainViewModel,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
