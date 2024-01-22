//In this screen the user of the app can search for an airport
package com.example.flynow

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalAirport
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray

//class for searching inside the list of airports when typing letters in the text field
@OptIn(FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _airports = MutableStateFlow<List<Airport>>(emptyList())
    val airports = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_airports) { text, airports ->
            if (text.isBlank()) {
                airports
            } else {
                delay(2000L)
                airports.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        // Initialize airports on viewModel creation
        fetchAndInitializeAirports()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun fetchAndInitializeAirports() {
        viewModelScope.launch {
            fetchDataFromApi {
                _airports.value = it
            }
        }
    }

    //function that runs in the initialization of the airports list that the api returns with the get method
    private fun fetchDataFromApi(onResult: (List<Airport>) -> Unit) {
        val url = "http://100.106.205.30:5000/flynow/airports"
        val queue: RequestQueue = Volley.newRequestQueue(getApplication())

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val airports = parseJson(response.toString())
                    onResult(airports)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("Error", error.toString())
                Toast.makeText(getApplication(), "Fail to get response", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }
}

//function that parse the data that returns the query and storing them to a list
//that is a type of Airport data class
private fun parseJson(jsonString: String): List<Airport> {
    val jsonArray = JSONArray(jsonString)
    val airports = mutableListOf<Airport>()

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        val name = jsonObject.getString("name")
        val city = jsonObject.getString("city")

        val airport = Airport(name, city)
        airports.add(airport)
    }

    return airports
}

//airports screen with the textfield and the list of the airports to select
//navController helps to navigate to previous page, selectedIndex helps if there is the bottom bar
//in previous page to go in the correct icon of the bottom bar, airportFrom and airportTo is in what variable will
//be stored the airport that is selected, whatAirport shows if the click from previous page has come from
//airportTo or airportFrom, rentCar shows if the previous page was "rent a car" and locationToRentCar is the variable that
//if the previous page is "rent a car" to store the airport that is selected
@Composable
fun AirportsScreen(navController: NavController,
                   selectedIndex: MutableIntState,
                   airportFrom: MutableState<String>,
                   airportTo: MutableState<String>,
                   whatAirport: MutableIntState,
                   rentCar: MutableState<Boolean>,
                   locationToRentCar: MutableState<String>) {
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val viewModel = viewModel<MainViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val airports by viewModel.airports.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    //variables that help in searching textfield

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //back button to go back to previous page
            IconButton(onClick = {
                if(!rentCar.value) {
                    //if the selection is from home of the airport returns to home
                    if (selectedIndex.intValue == 0) {
                        navController.navigate(Home.route) {
                            popUpTo(Airports.route)
                            launchSingleTop = true
                        }
                    }
                    //if the selection is from book of the airport returns to book
                    else if (selectedIndex.intValue == 1) {
                        navController.navigate(Book.route) {
                            popUpTo(Airports.route)
                            launchSingleTop = true
                        }
                    }
                }
                //if the selection is from rent a car of the airport returns to rent a car
                else {
                    navController.navigate(Car.route) {
                        popUpTo(Airports.route)
                        launchSingleTop = true
                    }
                }
            }) {
                Icon(
                    Icons.Outlined.ArrowBackIos,
                    contentDescription = "back",
                    tint = Color(0xFF023E8A)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                //title of the page
                Text(
                    text = "Select Airport",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(end = 45.dp),
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
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //textfield to insert the airport that you want
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 10.dp, end = 10.dp),
                label = { Text("Airport", fontSize = 16.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF023E8A),
                    focusedBorderColor = Color(0xFF023E8A),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFF00B4D8),
                    errorContainerColor = Color.White,
                    cursorColor = Color(0xFF023E8A)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                textStyle = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    )
                ),
                leadingIcon = {
                    Icon(
                        Icons.Filled.LocalAirport,
                        contentDescription = "airport",
                        tint = Color(0xFF00B4D8)
                    )
                }
            )
            //when searching from typing letters shows a circular progress
            //otherwise the list of the result of searching
            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 200.dp),
                        color = Color(0xFF023E8A)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        airports
                    )
                    { airportItem ->
                        if(airportFrom.value != airportItem.name && airportTo.value != airportItem.name) {
                            //airport texts that are clickable and when are clicked
                            //go back to previous page the selected airport, in "To" destination
                            //there is also the selection of keyword everywhere
                            ClickableText(
                                onClick = {
                                    if(!rentCar.value) {
                                        if (whatAirport.intValue == 0) {
                                            airportFrom.value = airportItem.name
                                        } else if (whatAirport.intValue == 1) {
                                            airportTo.value = airportItem.name
                                        }
                                        if (selectedIndex.intValue == 0) {
                                            navController.navigate(Home.route) {
                                                popUpTo(Airports.route)
                                                launchSingleTop = true
                                            }
                                        } else if (selectedIndex.intValue == 1) {
                                            navController.navigate(Book.route) {
                                                popUpTo(Airports.route)
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                    else {
                                        locationToRentCar.value = airportItem.name
                                        navController.navigate(Car.route) {
                                            popUpTo(Airports.route)
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                text = AnnotatedString(
                                    "${airportItem.city} (${airportItem.name})"),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color(0xFF023E8A),
                                    textIndent = TextIndent(20.sp, 0.sp)
                                )
                            )
                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                }
            }
        }
    }
}