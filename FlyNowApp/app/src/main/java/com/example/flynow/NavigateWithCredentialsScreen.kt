package com.example.flynow

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AirlineSeatReclineNormal
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

//screen that shows the more and has two parameters navController and selectedIndex
//for the navigation back and in home page, the third parameter that is state is for
//knowing from what route this composable is called
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigateWithCredentialsScreen(
    navController: NavController,
    selectedIndex: MutableIntState,
    state : String
) {
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    //variables for storing the two text inputs values for booking id and lastname
    val textBookingId = remember {
        mutableStateOf("")
    }
    val textLastname = remember {
        mutableStateOf("")
    }
    val continueClicked = remember {
        mutableStateOf(false)
    }
    val buttonClicked = remember {
        mutableStateOf(false)
    }
    //variables to check if all is alright
    val bookingExists = remember {
        mutableStateOf(false)
    }
    val falseCredentials = remember {
        mutableStateOf(false)
    }
    //variable to show the alert dialog to complete the update
    val showDialog = remember {
        mutableStateOf(false)
    }
    val showScreen = remember {
        mutableStateOf("")
    }
    val ctx = LocalContext.current
    val checkBooking = remember {
        mutableStateOf(false)
    }
    val hasError = remember {
        mutableStateOf(false)
    }
    //variables for the wifi on board
    val wifiOnBoard = remember {
        mutableStateOf(false)
    }
    //info from database for wifi
    val wifiOnBoardInfo = remember {
        mutableIntStateOf(0)
    }
    val updateWifi = remember {
        mutableStateOf(false)
    }
    val priceWifi = remember {
        mutableDoubleStateOf(0.0)
    }
    //wifi option that the user selected
    val selectedWifi = remember {
        mutableIntStateOf(0)
    }
    //variables for the upgrade to business class
    val upgradeToBusiness = remember {
        mutableStateOf(false)
    }
    //info from database for upgrade to business
    val upgradeToBusinessInfo = remember {
        mutableListOf<MutableState<String>>()
    }
    val priceUpgradeBusiness = remember {
        mutableDoubleStateOf(0.0)
    }
    val updateBusiness = remember {
        mutableStateOf(false)
    }
    //list to store the selected values for the outbound and inbound for business option
    val selectedUpgradeBusiness = remember {
        mutableListOf<MutableState<Boolean>>()
    }
    //variables for the travelling with pets
    val updatePets = remember {
        mutableStateOf(false)
    }
    val petsFromMore = remember {
        mutableStateOf(false)
    }
    val pricePets = remember {
        mutableDoubleStateOf(0.0)
    }
    val tempPricePets = remember {
        mutableIntStateOf(0)
    }
    //pet size option that the user selected
    val selectedPetSize = remember {
        mutableStateOf("")
    }
    //info from database for pets
    val petSizeInfo = remember {
        mutableStateOf("")
    }
    val showDialogConfirm = remember {
        mutableStateOf(false)
    }
    val radioOptions = listOf("Yes","No")
    val selectedOption = remember {
        mutableStateOf(radioOptions[1])
    }
    val selectedOptionForYes = remember {
        mutableStateOf("")
    }
    //variables for the extra baggage
    val updateBaggage = remember {
        mutableStateOf(false)
    }
    val baggageFromMore = remember {
        mutableStateOf(false)
    }
    val priceBaggage = remember {
        mutableDoubleStateOf(0.0)
    }
    val tempPriceBaggage = remember {
        mutableIntStateOf(0)
    }
    //baggage that the user selected
    val selectedBaggage: MutableList<MutableList<MutableIntState>> = remember {
        mutableStateListOf()
    }
    //info for passengers to select baggage per passenger
    val passengersInfo: MutableList<PassengerInfo> = remember {
        mutableListOf()
    }
    val numOfPassengers = remember {
        mutableIntStateOf(0)
    }
    //is for initialization to be executed one time
    val oneTimeExecution = remember {
        mutableStateOf(false)
    }
    val oneWay = remember {
        mutableStateOf(false)
    }
    //how many baggage more can select, the maximum is five baggage
    val limitBaggageFromMore: MutableList<MutableIntState> = remember {
        mutableListOf()
    }

    //api for checking the booking if exists and continue to other routes
    LaunchedEffect(checkBooking.value) {
        if (checkBooking.value && textLastname.value != "" && textBookingId.value != "") {
            val url = "http://100.106.205.30:5000/flynow/check-booking"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("lastname", textLastname.value)
            jsonObject.put("bookingid", textBookingId.value)
            jsonArray.put(jsonObject)
            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    bookingExists.value = response.getJSONObject(0).getBoolean("success")
                    if (!bookingExists.value){
                        hasError.value = true
                    }
                    Log.d("response", response.getJSONObject(0).getBoolean("success").toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(1000)
            //accordingly to state goes to the specific route
            if(bookingExists.value) {
                falseCredentials.value = false
                when (state) {
                    "WifiOnBoard" -> {
                        wifiOnBoard.value = true
                    }
                    "UpgradeClass" -> {
                        upgradeToBusiness.value = true
                    }
                    "PetsFromMore" -> {
                        petsFromMore.value = true
                    }
                    "BaggageFromMore" -> {
                        baggageFromMore.value = true
                    }
                }
            }
            else {
                checkBooking.value = false
            }
        }
        else {
            checkBooking.value = false
        }
    }

    //api that stores the return value from query in variable wifiOnBoardInfo and shows
    //if the user has wifi or not
    LaunchedEffect(wifiOnBoard.value) {
        if(wifiOnBoard.value) {
            val url = "http://100.106.205.30:5000/flynow/wifi-on-board"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", textBookingId.value)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    wifiOnBoardInfo.intValue = response.getJSONObject(0).getInt("wifiOnBoard")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(1000)
            wifiOnBoard.value = false
            continueClicked.value = true
            showScreen.value = "WifiOnBoard"
        }
    }

    //api that complete the update query for the selecting value from the user for the wifi
    LaunchedEffect(updateWifi.value) {
        if(updateWifi.value) {
            val url = "http://100.106.205.30:5000/flynow/update-wifi"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", textBookingId.value)
            jsonObject.put("wifiOnBoard", selectedWifi.intValue)
            jsonObject.put("price", priceWifi.doubleValue)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { _ ->
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(2000)
            updateWifi.value = false
        }
    }

    //api that stores the return value from query in variable upgradeToBusinessInfo and shows
    //the user state about the outbound and inbound flights, what class it is
    LaunchedEffect(upgradeToBusiness.value) {
        if(upgradeToBusiness.value) {
            val url = "http://100.106.205.30:5000/flynow/upgrade-to-business"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingId", textBookingId.value)
            jsonArray.put(jsonObject)
            upgradeToBusinessInfo.clear()

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    if(response.getJSONObject(0).getBoolean("oneWay")) {
                        upgradeToBusinessInfo.add(mutableStateOf(""))
                        upgradeToBusinessInfo[0].value = response.getJSONObject(0).getJSONObject("outbound").getString("classType")
                    }
                    else {
                        upgradeToBusinessInfo.add(mutableStateOf(""))
                        upgradeToBusinessInfo.add(mutableStateOf(""))
                        upgradeToBusinessInfo[0].value = response.getJSONObject(0).getJSONObject("outbound").getString("classType")
                        upgradeToBusinessInfo[1].value = response.getJSONObject(0).getJSONObject("inbound").getString("classType")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(1000)
            upgradeToBusiness.value = false
            continueClicked.value = true
            showScreen.value = "UpgradeClass"
        }
    }

    //api that complete the update query for the selecting value from the user for the upgrade class
    LaunchedEffect(updateBusiness.value) {
        if(updateBusiness.value) {
            val url = "http://100.106.205.30:5000/flynow/update-business"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingId", textBookingId.value)
            if(selectedUpgradeBusiness.size == 1) {
                jsonObject.put("outbound", selectedUpgradeBusiness[0].value)
                jsonObject.put("inbound", false)
            }
            else {
                jsonObject.put("outbound", selectedUpgradeBusiness[0].value)
                jsonObject.put("inbound", selectedUpgradeBusiness[1].value)
            }
            jsonObject.put("price", priceUpgradeBusiness.doubleValue)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { _ ->
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(2000)
            updateBusiness.value = false
        }
    }

    //api that stores the return value from query in variable petSizeInfo and shows
    //if the user has pet or not and what size
    LaunchedEffect(petsFromMore.value) {
        if(petsFromMore.value) {
            val url = "http://100.106.205.30:5000/flynow/pets-from-more"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", textBookingId.value)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    petSizeInfo.value = response.getJSONObject(0).getString("petSize")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(1000)
            petsFromMore.value = false
            continueClicked.value = true
            showScreen.value = "Pets"
        }
    }

    //api that complete the update query for the selecting value from the user for the pets
    LaunchedEffect(updatePets.value) {
        if(updatePets.value) {
            val url = "http://100.106.205.30:5000/flynow/update-pets"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingid", textBookingId.value)
            jsonObject.put("petSize", selectedPetSize.value)
            jsonObject.put("price", pricePets.doubleValue)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { _ ->
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(2000)
            updatePets.value = false
        }
    }

    //api that stores the passengers info and the currently number of baggage pieces per passenger
    //to check how many pieces of baggage remain to select the passenger
    LaunchedEffect(baggageFromMore.value) {
        if(baggageFromMore.value) {
            val url = "http://100.106.205.30:5000/flynow/baggage-from-more"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingId", textBookingId.value)
            jsonArray.put(jsonObject)
            selectedBaggage.clear()
            passengersInfo.clear()
            limitBaggageFromMore.clear()

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    numOfPassengers.intValue = response.length() - 1
                    oneWay.value = response.getJSONObject(0).getBoolean("oneWay")
                    if(oneWay.value) {
                        repeat(numOfPassengers.intValue) {
                            selectedBaggage.add(mutableStateListOf(mutableIntStateOf(0),
                                mutableIntStateOf(0)
                            ))
                        }
                    }
                    else {
                        repeat(numOfPassengers.intValue*2) {
                            selectedBaggage.add(mutableStateListOf(mutableIntStateOf(0),
                                mutableIntStateOf(0)
                            ))
                        }
                    }
                    for(i in 1 until numOfPassengers.intValue + 1) {
                        passengersInfo.add(PassengerInfo(mutableStateOf(""),
                            mutableStateOf(""),mutableStateOf(""),
                            mutableStateOf(""),mutableStateOf(""),
                            mutableStateOf("")))
                        passengersInfo[i-1].firstname.value = response.getJSONObject(i).getString("firstname")
                        passengersInfo[i-1].lastname.value = response.getJSONObject(i).getString("lastname")
                        passengersInfo[i-1].gender.value = response.getJSONObject(i).getString("gender")
                        passengersInfo[i-1].birthdate.value = response.getJSONObject(i).getString("birthdate")
                        passengersInfo[i-1].email.value = response.getJSONObject(i).getString("email")
                        passengersInfo[i-1].phonenumber.value = response.getJSONObject(i).getString("phonenumber")
                    }
                    for(i in 0 until numOfPassengers.intValue) {
                        limitBaggageFromMore.add(mutableIntStateOf(0))
                        limitBaggageFromMore[i].intValue = response.getJSONObject(i+1).getInt("baggageOutbound")
                    }
                    if(!oneWay.value) {
                        for(i in 0 until numOfPassengers.intValue) {
                            limitBaggageFromMore.add(mutableIntStateOf(0))
                            limitBaggageFromMore[i+numOfPassengers.intValue].intValue = response.getJSONObject(i+1).getInt("baggageInbound")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(2000)
            baggageFromMore.value = false
            continueClicked.value = true
            showScreen.value = "Baggage"
        }
    }

    //api that complete the update query for the selecting values for the baggage from the user
    LaunchedEffect(updateBaggage.value) {
        if(updateBaggage.value) {
            val url = "http://100.106.205.30:5000/flynow/update-baggage"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("bookingId", textBookingId.value)
            val baggageJSONArray = JSONArray()
            if (oneWay.value) {
                for (i in 0 until numOfPassengers.intValue) {
                    val baggageJSONObject = JSONObject()
                    val baggageJSONObjectOutbound = JSONObject()
                    baggageJSONObject.put("email", passengersInfo[i].email.value)
                    baggageJSONObjectOutbound.put("baggage23kg", selectedBaggage[i][0].intValue)
                    baggageJSONObjectOutbound.put("baggage32kg", selectedBaggage[i][1].intValue)
                    baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                    baggageJSONArray.put(baggageJSONObject)
                }
                jsonObject.put("oneWay", true)
                jsonObject.put("baggage", baggageJSONArray)
            }
            else {
                for (i in 0 until numOfPassengers.intValue) {
                    val baggageJSONObject = JSONObject()
                    val baggageJSONObjectOutbound = JSONObject()
                    val baggageJSONObjectInbound = JSONObject()
                    baggageJSONObject.put("email", passengersInfo[i].email.value)
                    baggageJSONObjectOutbound.put("baggage23kg", selectedBaggage[i][0].intValue)
                    baggageJSONObjectOutbound.put("baggage32kg", selectedBaggage[i][1].intValue)
                    baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                    baggageJSONObjectInbound.put(
                        "baggage23kg",
                        selectedBaggage[i + numOfPassengers.intValue][0].intValue
                    )
                    baggageJSONObjectInbound.put(
                        "baggage32kg",
                        selectedBaggage[i + numOfPassengers.intValue][1].intValue
                    )
                    baggageJSONObject.put("inbound", baggageJSONObjectInbound)
                    baggageJSONArray.put(baggageJSONObject)
                }
                jsonObject.put("oneWay", false)
                jsonObject.put("baggage", baggageJSONArray)
            }
            jsonObject.put("price", priceBaggage.doubleValue)
            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { _ ->
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.d("Error", error.toString())
                Toast.makeText(ctx, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            queue.add(request)
            delay(2000)
            updateBaggage.value = false
        }
    }

    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        BottomAppBar(
            backgroundColor = Color.Transparent,
            contentColor = Color.Transparent,
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            contentPadding = PaddingValues(0.dp),
            elevation = 0.dp
        ) {
            if (showScreen.value != "") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        "Total Price: ${
                            when (showScreen.value) {
                                "WifiOnBoard" -> priceWifi.doubleValue
                                "UpgradeClass" -> priceUpgradeBusiness.doubleValue
                                "Pets" -> pricePets.doubleValue
                                "Baggage" -> priceBaggage.doubleValue
                                else -> 0.0
                            }
                        } €",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    //Button "Continue" that navigates the user to the next step of the updating
                    //of the booking
                    Button(
                        onClick = {
                            buttonClicked.value = true
                            showDialog.value = true
                        },
                        modifier = Modifier
                            .padding(start = 45.dp)
                            .width(130.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00B4D8),
                            disabledContentColor = Color(0xFF023E8A)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp
                        ),
                        enabled = (state=="WifiOnBoard" && selectedWifi.intValue != 0)
                                || (state=="UpgradeClass" && priceUpgradeBusiness.doubleValue != 0.0)
                                || (state=="PetsFromMore" && pricePets.doubleValue != 0.0)
                                || (state=="BaggageFromMore" && priceBaggage.doubleValue != 0.0)
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }) {
        //alert dialog that is shown in the end for the completion of the update query and after that
        //has an ok button that returns the user to the home page
        if (showDialog.value || showDialogConfirm.value) {
            Box(contentAlignment = Alignment.Center) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = {
                        Text(
                            text =
                            if(state == "WifiOnBoard") {
                                if (showDialog.value)
                                    "Confirm Updating Wifi"
                                else if(!updateWifi.value)
                                    "The Update Of Wifi Was Done Successfully!"
                                else ""
                            }
                            else if(state == "UpgradeClass") {
                                if (showDialog.value)
                                    "Confirm Upgrading Class"
                                else if(!updateBusiness.value)
                                    "The Upgrade Of Class Was Done Successfully!"
                                else ""
                            }
                            else if(state == "PetsFromMore") {
                                if (showDialog.value)
                                    "Confirm Addition Of Pet"
                                else if(!updatePets.value)
                                    "The Addition Of Pet Was Done Successfully!"
                                else ""
                            }
                            else if(state == "BaggageFromMore") {
                                if (showDialog.value)
                                    "Confirm Addition Of Baggage"
                                else if(!updateBaggage.value)
                                    "The Addition Of Baggage Was Done Successfully!"
                                else ""
                            }
                            else {""},
                            fontSize = 20.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        if(state == "WifiOnBoard" && !updateWifi.value) {
                            Icon(
                                if (showDialog.value) Icons.Filled.QuestionMark
                                else Icons.Filled.Verified,
                                contentDescription = "question",
                                modifier =
                                if (showDialog.value) Modifier.padding(start = 205.dp)
                                else Modifier.padding(top = 33.dp, start = 175.dp),
                                tint = Color(0xFF023E8A)
                            )
                        }
                        else if(state == "UpgradeClass" && !updateBusiness.value) {
                            Icon(
                                if (showDialog.value) Icons.Filled.QuestionMark
                                else Icons.Filled.Verified,
                                contentDescription = "question",
                                modifier =
                                if (showDialog.value) Modifier.padding(start = 227.dp)
                                else Modifier.padding(top = 33.dp, start = 175.dp),
                                tint = Color(0xFF023E8A)
                            )
                        }
                        else if(state == "PetsFromMore" && !updatePets.value) {
                            Icon(
                                if (showDialog.value) Icons.Filled.QuestionMark
                                else Icons.Filled.Verified,
                                contentDescription = "question",
                                modifier =
                                if (showDialog.value) Modifier.padding(start = 224.dp)
                                else Modifier.padding(top = 33.dp, start = 175.dp),
                                tint = Color(0xFF023E8A)
                            )
                        }
                        else if(state == "BaggageFromMore" && !updateBaggage.value) {
                            Icon(
                                if (showDialog.value) Icons.Filled.QuestionMark
                                else Icons.Filled.Verified,
                                contentDescription = "question",
                                modifier =
                                if (showDialog.value) Modifier.padding(top = 33.dp, start = 75.dp)
                                else Modifier.padding(top = 33.dp, start = 225.dp),
                                tint = Color(0xFF023E8A)
                            )
                        }
                    },
                    text = {
                        if (showDialog.value && ((state == "WifiOnBoard" && !updateWifi.value)
                        ||(state == "UpgradeClass" && !updateBusiness.value)
                        ||(state == "PetsFromMore" && !updatePets.value)
                        ||(state == "BaggageFromMore" && !updateBaggage.value))) {
                            Text(
                                text = when (state) {
                                    "WifiOnBoard" -> "Are you sure you want to update the wifi?"
                                    "UpgradeClass" -> "Are you sure you want to upgrade the class?"
                                    "PetsFromMore" -> "Are you sure you want to add a pet?"
                                    "BaggageFromMore" -> "Are you sure you want to add baggage?"
                                    else -> ""
                                },
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                        if ((state == "WifiOnBoard" && updateWifi.value)
                            ||(state == "UpgradeClass" && updateBusiness.value)
                            ||(state == "PetsFromMore" && updatePets.value)
                            ||(state == "BaggageFromMore" && updateBaggage.value)) {
                            Column(modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFF023E8A)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        if (showDialog.value && ((state == "WifiOnBoard" && !updateWifi.value)
                        ||(state == "UpgradeClass" && !updateBusiness.value)
                        ||(state == "PetsFromMore" && !updatePets.value)
                        ||(state == "BaggageFromMore" && !updateBaggage.value))) {
                            Button(
                                onClick = {
                                    when (state) {
                                        "WifiOnBoard" -> {
                                            updateWifi.value = true
                                        }
                                        "UpgradeClass" -> {
                                            updateBusiness.value = true
                                        }
                                        "PetsFromMore" -> {
                                            updatePets.value = true
                                        }
                                        "BaggageFromMore" -> {
                                            updateBaggage.value = true
                                        }
                                    }
                                    showDialog.value = false
                                    showDialogConfirm.value = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF023E8A)
                                )
                            ) {
                                Text(
                                    "Yes",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    )
                                )
                            }
                        } else {
                            if((state == "WifiOnBoard" && !updateWifi.value)
                                ||(state == "UpgradeClass" && !updateBusiness.value)
                                ||(state == "PetsFromMore" && !updatePets.value)
                                ||(state == "BaggageFromMore" && !updateBaggage.value)) {
                                Button(
                                    onClick = {
                                        showDialogConfirm.value = false
                                        showDialog.value = false
                                        selectedIndex.intValue = 0

                                        when (state) {
                                            "WifiOnBoard" -> {
                                                navController.navigate(Home.route) {
                                                    popUpTo(WifiOnBoard.route)
                                                    launchSingleTop = true
                                                }
                                            }
                                            "UpgradeClass" -> {
                                                navController.navigate(Home.route) {
                                                    popUpTo(UpgradeClass.route)
                                                    launchSingleTop = true
                                                }
                                            }
                                            "PetsFromMore" -> {
                                                navController.navigate(Home.route) {
                                                    popUpTo(PetsFromMore.route)
                                                    launchSingleTop = true
                                                }
                                            }
                                            "BaggageFromMore" -> {
                                                navController.navigate(Home.route) {
                                                    popUpTo(BaggageFromMore.route)
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF023E8A)
                                    )
                                ) {
                                    Text(
                                        "OK",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    },
                    dismissButton = {
                        if (showDialog.value
                            && ((state == "WifiOnBoard" && !updateWifi.value)||
                            (state == "UpgradeClass" && !updateBusiness.value)||
                            (state == "PetsFromMore" && !updatePets.value)||
                            (state == "BaggageFromMore" && !updateBaggage.value))) {
                            Button(
                                onClick = {
                                    showDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF023E8A)
                                )
                            ) {
                                Text(
                                    "No",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    )
                                )
                            }
                        }
                    },
                    containerColor = Color(0xFFEBF2FA),
                    textContentColor = Color(0xFF023E8A),
                    titleContentColor = Color(0xFF023E8A),
                    tonalElevation = 30.dp,
                    properties = DialogProperties(dismissOnClickOutside = false)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        when (state) {
                            "UpgradeClass" -> {
                                navController.navigate(More.route) {
                                    popUpTo(UpgradeClass.route)
                                    launchSingleTop = true
                                }
                            }
                            "PetsFromMore" -> {
                                navController.navigate(More.route) {
                                    popUpTo(PetsFromMore.route)
                                    launchSingleTop = true
                                }
                            }
                            "BaggageFromMore" -> {
                                navController.navigate(More.route) {
                                    popUpTo(BaggageFromMore.route)
                                    launchSingleTop = true
                                }
                            }
                            "WifiOnBoard" -> {
                                navController.navigate(More.route) {
                                    popUpTo(WifiOnBoard.route)
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
                {
                    Icon(
                        Icons.Outlined.ArrowBackIos,
                        contentDescription = "back",
                        tint = Color(0xFF023E8A)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text =
                        when (state) {
                            "PetsFromMore" -> "Pets"
                            "BaggageFromMore" -> "Baggage"
                            "WifiOnBoard" -> "Wifi οn Board"
                            "UpgradeClass" -> "Upgrade to Business Class"
                            else -> "Select Seat"
                        },
                        fontSize = 22.sp,
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
                    Icon(
                        when (state) {
                            "PetsFromMore" -> Icons.Outlined.Pets
                            "BaggageFromMore" -> Icons.Outlined.Luggage
                            "WifiOnBoard" -> Icons.Outlined.Wifi
                            "UpgradeClass" -> Icons.Outlined.KeyboardDoubleArrowUp
                            else -> Icons.Outlined.AirlineSeatReclineNormal
                        },
                        contentDescription =
                            when (state) {
                                "PetsFromMore" -> "Pets"
                                "BaggageFromMore" -> "Baggage"
                                "WifiOnBoard" -> "WifiOnBoard"
                                "UpgradeClass" -> "UpgradeClass"
                                else -> "SeatSelection"
                            },
                        modifier = Modifier.padding(start = 5.dp, top = 2.dp, end = 40.dp),
                        tint = Color(0xFF023E8A)
                    )
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
            Column(
                Modifier
                    .background(gradient)
                    .fillMaxSize()) {
                if (showScreen.value != "Baggage") {
                    Image(
                        painter = when (state) {
                            "PetsFromMore" -> painterResource(id = R.drawable.pet)
                            "BaggageFromMore" -> painterResource(id = R.drawable.baggage)
                            "WifiOnBoard" -> painterResource(id = R.drawable.wifionboard)
                            "UpgradeClass" -> painterResource(id = R.drawable.upgradeclass)
                            else -> painterResource(id = R.drawable.checkin)
                        },
                        contentDescription = "pet",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio = if (state == "WifiOnBoard") 2.6f else if (state == "PetsFromMore") 2.7f else 3f)
                            .padding(start = 10.dp, top = 5.dp, end = 10.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    //text input for the booking reference
                    if (!continueClicked.value || falseCredentials.value) {
                        OutlinedTextField(
                            value = textBookingId.value,
                            onValueChange = {
                                textBookingId.value = it
                                buttonClicked.value = false
                                hasError.value = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                            label = { Text("Booking reference", fontSize = 16.sp) },
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
                                    Icons.Filled.AirplaneTicket,
                                    contentDescription = "bookingId",
                                    tint = Color(0xFF00B4D8)
                                )
                            },
                            isError = (buttonClicked.value && textBookingId.value == "" || hasError.value)
                        )
                        //text input for the last name
                        OutlinedTextField(
                            value = textLastname.value,
                            onValueChange = {
                                textLastname.value = it
                                buttonClicked.value = false
                                hasError.value = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                            label = { Text("Last name", fontSize = 16.sp) },
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
                                    painterResource(id = R.drawable.passenger),
                                    contentDescription = "lastname",
                                    tint = Color(0xFF00B4D8)
                                )
                            },
                            isError = (buttonClicked.value && textLastname.value == "" || hasError.value)
                        )
                        //error if the booking is wrong or does not exist
                        if (hasError.value) {
                            Text(
                                text = "Incorrect booking reference or lastname. Please check your details and try again.",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp),
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                ),
                                color = Color.Red
                            )
                        }
                        //button to continue to alert dialog
                        ElevatedButton(
                            onClick = {
                                buttonClicked.value = true
                                checkBooking.value = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(top = 50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8)),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Text(text = "Continue",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(
                                    fonts = listOf(
                                        Font(
                                            resId = R.font.opensans
                                        )
                                    )
                                )
                            )
                        }
                    } else {
                        when (showScreen.value) {
                            //for pets and baggage these functions are in BaggageAndPetsScreen
                            "Pets" -> {
                                //function that is called to show the screen for the pets
                                ShowPetField(
                                    state = "PetsFromMore",
                                    tempPetPrice = tempPricePets,
                                    petSize = selectedPetSize,
                                    radioOptions = radioOptions,
                                    selectedOption = selectedOption,
                                    selectedOptionForYes = selectedOptionForYes,
                                    petSizeInfo = petSizeInfo
                                )
                                //add pet price
                                pricePets.doubleValue = tempPricePets.intValue.toDouble()
                            }
                            "Baggage" -> {
                                //function that is called to show the screen for the baggage
                                if(oneWay.value) {
                                    BaggageField(
                                        state = "BaggageFromMore",
                                        baggagePerPassenger = selectedBaggage,
                                        numOfPassengers = numOfPassengers.intValue,
                                        classTypeInbound = "",
                                        classTypeOutbound = "",
                                        tempBaggagePrice = tempPriceBaggage,
                                        tempPetPrice = remember {mutableIntStateOf(0)},
                                        pagePrevious = remember {mutableIntStateOf(0)},
                                        oneTimeExecution = oneTimeExecution,
                                        passengersInfo = passengersInfo,
                                        petSize = remember {mutableStateOf("")},
                                        radioOptions = radioOptions,
                                        selectedOption = selectedOption,
                                        selectedOptionForYes = selectedOptionForYes,
                                        limitBaggageFromMore = limitBaggageFromMore
                                    )
                                }
                                else {
                                    BaggageField(
                                        state = "BaggageFromMore",
                                        baggagePerPassenger = selectedBaggage,
                                        numOfPassengers = numOfPassengers.intValue,
                                        classTypeInbound = "",
                                        classTypeOutbound = "",
                                        tempBaggagePrice = tempPriceBaggage,
                                        tempPetPrice = remember {mutableIntStateOf(0)},
                                        pagePrevious = remember {mutableIntStateOf(1)},
                                        oneTimeExecution = oneTimeExecution,
                                        passengersInfo = passengersInfo,
                                        petSize = remember {mutableStateOf("")},
                                        radioOptions = radioOptions,
                                        selectedOption = selectedOption,
                                        selectedOptionForYes = selectedOptionForYes,
                                        limitBaggageFromMore = limitBaggageFromMore
                                    )
                                }
                                //add baggage price
                                priceBaggage.doubleValue = tempPriceBaggage.intValue.toDouble()
                            }
                            "WifiOnBoard" -> {
                                //function that is called to show the screen for the wifi
                                AddWifiOnBoard(
                                    wifiOnBoardInfo = wifiOnBoardInfo,
                                    price = priceWifi,
                                    selectedWifi = selectedWifi
                                )
                            }
                            "UpgradeClass" -> {
                                //function that is called to show the screen for the upgrade to business class
                                UpgradeToBusinessClass(
                                    price = priceUpgradeBusiness,
                                    upgradeToBusinessInfo = upgradeToBusinessInfo,
                                    selectedUpgradeBusiness = selectedUpgradeBusiness
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//function that shows the wifi on board route
//and takes the price, info from the query and the variable that will be stored the
//selected values from the user
@Composable
fun AddWifiOnBoard(wifiOnBoardInfo: MutableIntState,
                   price: MutableDoubleState,
                   selectedWifi: MutableIntState) {
    val radioOptions = listOf(
        "Web browsing & Social Media, \nup to 1.5Mbps - 6€",
        "Audio/Video streaming, High speed Web Browsing & Social Media, \nup to 15Mbps - 12€")
    var selectedOption by remember {
        mutableStateOf("")
    }
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Choose the Wifi package that suits you!",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
        if(wifiOnBoardInfo.intValue != 2) {
            radioOptions.forEach { option ->
                Row(Modifier.fillMaxWidth()) {
                    if(wifiOnBoardInfo.intValue == 0) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option
                                if(option == radioOptions[0]) {
                                    selectedWifi.intValue = 1
                                    price.doubleValue = 6.0
                                }
                                else {
                                    selectedWifi.intValue = 2
                                    price.doubleValue = 12.0
                                }
                            },
                            modifier = Modifier.padding(top = if(option == radioOptions[1]) 10.dp else 0.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00B4D8),
                                unselectedColor = Color(0xFF00B4D8)
                            )
                        )
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = if(option == radioOptions[1]) 22.dp else 12.dp),
                            color = Color(0xFF023E8A),
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    }
                    else if(wifiOnBoardInfo.intValue == 1
                        && option == radioOptions[1]) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option
                                selectedWifi.intValue = 2
                                price.doubleValue = 6.0
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF00B4D8),
                                unselectedColor = Color(0xFF00B4D8)
                            )
                        )
                        Text(
                            text = radioOptions[1].substring(0,78)+" 6€",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 12.dp),
                            color = Color(0xFF023E8A),
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
        else {
            Text(
                text = "You have already selected Audio/Video streaming, \nHigh speed Web Browsing & Social Media, \nup to 15Mbps!",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 10.dp, top = 12.dp),
                color = Color(0xFF023E8A),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                )
            )
        }
    }
}

//function that shows the upgrade to business route
//and takes the price, info from the query and the variable that will be stored the
//selected values from the user
@Composable
fun UpgradeToBusinessClass(price: MutableDoubleState,
                           upgradeToBusinessInfo: MutableList<MutableState<String>>,
                           selectedUpgradeBusiness: MutableList<MutableState<Boolean>>
){
    val numOfFlights = upgradeToBusinessInfo.size//outbound inbound

    if(selectedUpgradeBusiness.size == 0 && upgradeToBusinessInfo.size == 1) {
        selectedUpgradeBusiness.add(remember {mutableStateOf(false)})
    }
    else if(selectedUpgradeBusiness.size == 0 && upgradeToBusinessInfo.size == 2) {
        selectedUpgradeBusiness.add(remember {mutableStateOf(false)})
        selectedUpgradeBusiness.add(remember {mutableStateOf(false)})
    }

    LazyColumn(modifier = Modifier.fillMaxSize()
        .padding(bottom = 65.dp)) {
        items(numOfFlights) { index ->
            var myState by remember { mutableStateOf(false) }
            if (index == 0) {
                Text(
                    text = "Live the Business Class experience now!",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
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
            Text(
                text = if (index == 0) "Outbound" else "Inbound",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A),
                fontWeight = FontWeight.Bold
            )
            Text(
                text =
                when (upgradeToBusinessInfo[index].value) {
                    "BUSINESS CLASS" -> "Your flight is already in Business Class."
                    "FLEX CLASS" -> "Your flight is in Flex Class."
                    else -> "Your flight is in Economy Class."
                },
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 10.dp, top = 15.dp),
                fontFamily = FontFamily(
                    fonts = listOf(
                        Font(
                            resId = R.font.opensans
                        )
                    )
                ),
                color = Color(0xFF023E8A)
            )
            if (upgradeToBusinessInfo[index].value != "BUSINESS CLASS") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = myState,
                        onCheckedChange = {
                            myState = it
                            if (myState) {
                                price.doubleValue += 55.0
                                selectedUpgradeBusiness[index].value = true
                            } else {
                                price.doubleValue -= 55.0
                                selectedUpgradeBusiness[index].value = false
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00B4D8),
                            uncheckedColor = Color(0xFF00B4D8)
                        )
                    )
                    Text(
                        text = "Upgrade to Business Class - 55€",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A)
                    )
                }
            }
            if(index == numOfFlights - 1) {
                Text(
                    text = "Some of the Βusiness Class features and services:",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 30.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1) Airport priorities",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "2) Business lounges",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "3) Gastronomics",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}