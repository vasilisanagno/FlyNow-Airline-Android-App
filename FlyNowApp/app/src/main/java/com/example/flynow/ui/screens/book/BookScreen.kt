package com.example.flynow.ui.screens.book

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.book.components.ShowInputFields

//In this screen the user of the app can search for a flight with more details
//navController helps to navigate to previous page or next page,
//shared view model is for the shared data and book view model for the data that the state is kept in this screen
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BookScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    bookViewModel: BookViewModel
) {
    //helps for horizontal scrolling between two pages "One-way Trip" and "Round Trip"
    val pagerState = rememberPagerState(pageCount = 2,
        initialPage = sharedViewModel.page)
    val coroutineScope = rememberCoroutineScope()
    val list = listOf(
        "One-Way Trip",
        "Round Trip"
    )

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        //title for the screen
        Text(
            text = "Book a flight",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 5.dp),
            color = Color(0xFF023E8A),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            ),
            fontWeight = FontWeight.Bold
        )
        //tabs for two pages one way or round trip
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color = Color(0xFF023E8A)
                )
            }
        ) {
            list.forEachIndexed { index, listItem ->
                sharedViewModel.page = pagerState.currentPage
                Tab(
                    text = { Text(
                        listItem,
                        color = if (pagerState.currentPage == index) Color(0xFF023E8A) else Color(0x99023E8A),
                        fontSize = 16.sp
                    )},
                    selected = pagerState.currentPage == index,
                    onClick = {
                        if(pagerState.currentPage!=index) {
                            bookViewModel.departureDate=""
                            bookViewModel.returnDate=""
                        }
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        //horizontal scrolling between two pages and showing some different fields each time
        HorizontalPager(state = pagerState) { page ->
            bookViewModel.departureDate=""
            bookViewModel.returnDate=""
            when (page) {
                0 -> ShowInputFields(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    bookViewModel = bookViewModel,
                    page = 0
                )
                1 -> ShowInputFields(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    bookViewModel = bookViewModel,
                    page = 1
                )
            }
        }
    }
}