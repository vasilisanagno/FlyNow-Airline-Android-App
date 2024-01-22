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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

//navController helps to navigate to previous page or next page,
//selectedIndex helps if there is the bottom bar in previous page,
//totalPrice that is the total price of the booking,
//passengersCount is the number of passengers in the booking,
//passengers, seats, selectedFlights and the baggagePerPassenger are the list that
//is saved the information about passengers, seats, selected flights and the baggage
//pagePrevious show if the search begins from one way flights or round trip flights,
//selectedFlightOutbound, selectedFlightInbound is to check if the flight is direct or with one stop,
//pet size is for pets and classTypeOutbound, classTypeInbound is for the class of the flight that is selected,
//prevTotalPrice is variable that is useful for the back button and seats screen
//airportFrom, airportTo and whatAirport is for initialization when the booking is finished
//bookingFailed variable is if some seat is taken from someone else during the completion of the reservation
//this variable becomes true and send the user back to seats screen to select again seats for the passengers
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BaggageAndPetsScreen(navController: NavController,
                         selectedIndex: MutableIntState,
                         totalPrice: MutableDoubleState,
                         passengersCount: MutableIntState,
                         passengers: MutableList<PassengerInfo>,
                         seats: MutableList<MutableList<MutableState<String>>>,
                         pagePrevious: MutableIntState,
                         selectedFlights: MutableList<SelectedFlightDetails>,
                         selectedFlightOutbound: MutableIntState,
                         selectedFlightInbound: MutableIntState,
                         baggagePerPassenger: MutableList<MutableList<MutableIntState>>,
                         classTypeOutbound: MutableState<String>,
                         classTypeInbound: MutableState<String>,
                         prevTotalPrice: MutableDoubleState,
                         petSize: MutableState<String>,
                         airportFrom: MutableState<String>,
                         airportTo: MutableState<String>,
                         whatAirport: MutableIntState,
                         bookingFailed: MutableState<Boolean>

){
    val state = "Baggage&Pets"
    //variables that helps for the alert dialog, price, baggage, pets and finishing the reservation
    val showDialog = remember {
        mutableStateOf(false)
    }
    val tempPetPrice = remember {
        mutableIntStateOf(0)
    }
    val prevPetPrice = remember {
        mutableIntStateOf(0)
    }
    val tempBaggagePrice = remember {
        mutableIntStateOf(0)
    }
    val prevBaggagePrice = remember {
        mutableIntStateOf(0)
    }
    val oneTimeExecution = remember {
        mutableStateOf(false)
    }
    val numOfPassengers = passengersCount.intValue
    if(baggagePerPassenger.size == 0) {
        if(pagePrevious.intValue == 0) {
            repeat(numOfPassengers) {
                baggagePerPassenger.add(mutableStateListOf(
                    remember { mutableIntStateOf(0)},
                    remember { mutableIntStateOf(0)}
                ))
            }
        }
        else {
            repeat(numOfPassengers*2) {
                baggagePerPassenger.add(mutableStateListOf(
                    remember { mutableIntStateOf(0)},
                    remember { mutableIntStateOf(0)}
                ))
            }
        }
    }
    val radioOptions = listOf("Yes","No")
    val selectedOption = remember {
        mutableStateOf(radioOptions[1])
    }
    val selectedOptionForYes = remember {
        mutableStateOf("")
    }
    val finishReservation = remember {
        mutableIntStateOf(0)
    }
    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    val buttonClicked = remember {
        mutableStateOf(false)
    }

    Scaffold(bottomBar = {
        //Bottom navigation bar that shows the total price and the "Continue" button
        BottomAppBar(backgroundColor = Color.Transparent,
            contentColor = Color.Transparent,
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    "Total Price: ${totalPrice.doubleValue} €",
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
                //Button "Continue" that navigates the user to the next step of the reservation
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
                    )
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
    }) {
        ShowDialogToCommitReservation(
            navController = navController,
            selectedIndex = selectedIndex,
            showDialog = showDialog,
            passengersCount = passengersCount,
            passengers = passengers,
            seats = seats,
            selectedFlights = selectedFlights,
            selectedFlightOutbound = selectedFlightOutbound,
            selectedFlightInbound = selectedFlightInbound,
            baggagePerPassenger = baggagePerPassenger,
            classTypeOutbound = classTypeOutbound,
            classTypeInbound = classTypeInbound,
            pagePrevious = pagePrevious,
            totalPrice = totalPrice,
            prevTotalPrice = prevTotalPrice,
            petSize = petSize,
            finishReservation = finishReservation,
            airportFrom = airportFrom,
            airportTo = airportTo,
            whatAirport = whatAirport,
            bookingFailed = bookingFailed)
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //"Back" button
                IconButton(onClick = {
                    baggagePerPassenger.clear()
                    petSize.value = ""
                    totalPrice.doubleValue = prevTotalPrice.doubleValue
                    navController.navigate(Seats.route) {
                        popUpTo(BaggageAndPets.route)
                        launchSingleTop = true
                    }
                }) {
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
                    Text(
                        text = "Baggage&Pets",
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
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color(0xFF00B4D8))

            if(finishReservation.intValue == 0) {
                Column(
                    Modifier
                        .background(gradient)
                        .fillMaxSize()){
                    BaggageField(state,
                        baggagePerPassenger = baggagePerPassenger,
                        numOfPassengers = numOfPassengers,
                        classTypeInbound = classTypeInbound.value,
                        classTypeOutbound = classTypeOutbound.value,
                        tempBaggagePrice = tempBaggagePrice,
                        tempPetPrice = tempPetPrice,
                        pagePrevious = pagePrevious,
                        oneTimeExecution = oneTimeExecution,
                        passengersInfo = passengers,
                        petSize = petSize,
                        radioOptions = radioOptions,
                        selectedOption = selectedOption,
                        selectedOptionForYes = selectedOptionForYes,
                        limitBaggageFromMore = mutableListOf()
                    )
                    //add pet price
                    if(tempPetPrice.intValue != prevPetPrice.intValue && prevPetPrice.intValue != 0){
                        totalPrice.doubleValue -= prevPetPrice.intValue
                        totalPrice.doubleValue += tempPetPrice.intValue
                    }
                    else if(tempPetPrice.intValue != prevPetPrice.intValue && prevPetPrice.intValue == 0){
                        totalPrice.doubleValue += tempPetPrice.intValue
                    }
                    else if(tempPetPrice.intValue == 0 && prevPetPrice.intValue != 0){
                        totalPrice.doubleValue -= prevPetPrice.intValue
                    }
                    prevPetPrice.intValue = tempPetPrice.intValue
                    //add baggage price
                    if(tempBaggagePrice.intValue != prevBaggagePrice.intValue && prevBaggagePrice.intValue != 0){
                        totalPrice.doubleValue -= prevBaggagePrice.intValue
                        totalPrice.doubleValue += tempBaggagePrice.intValue
                    }
                    else if(tempBaggagePrice.intValue != prevBaggagePrice.intValue && prevBaggagePrice.intValue == 0){
                        totalPrice.doubleValue += tempBaggagePrice.intValue
                    }
                    prevBaggagePrice.intValue = tempBaggagePrice.intValue
                }
            }
        }
    }
}

//state is to check from what page is called the function "Baggage&Pets" or "BaggageFromMore"
//oneTimeExecution is to initialize some variables only once and not more and have information for the pets
//because inside this function is called the function for the pets and information for the state "BaggageFromMore"
//all the other variables have more details above
@Composable
fun BaggageField(state: String,
                 baggagePerPassenger: MutableList<MutableList<MutableIntState>>,
                 numOfPassengers: Int,
                 classTypeInbound: String,
                 classTypeOutbound: String,
                 tempBaggagePrice: MutableIntState,
                 tempPetPrice: MutableIntState,
                 pagePrevious: MutableIntState,
                 oneTimeExecution: MutableState<Boolean>,
                 passengersInfo: MutableList<PassengerInfo>,
                 petSize: MutableState<String>,
                 radioOptions: List<String>,
                 selectedOption: MutableState<String>,
                 selectedOptionForYes: MutableState<String>,
                 limitBaggageFromMore: MutableList<MutableIntState>
){

    //for each passenger to have what baggage has selected
    val passengers: MutableList<MutableList<Buttons>> = remember {
        mutableListOf()
    }
    val isClickPerPassenger: MutableList<MutableList<IsClickedBaggage>> = remember {
        mutableListOf()
    }
    val limit: MutableList<MutableIntState> = remember {
        mutableListOf()
    }

    if(!oneTimeExecution.value) {
        oneTimeExecution.value = true
        if(pagePrevious.intValue == 0) {
            repeat(numOfPassengers) {
                passengers.add(mutableStateListOf(Buttons()))
            }
            repeat(numOfPassengers) {
                isClickPerPassenger.add(mutableStateListOf(IsClickedBaggage()))
            }
            if(state == "BaggageFromMore") {
                repeat(numOfPassengers) {
                    limit.add(mutableIntStateOf(limitBaggageFromMore[it].intValue))
                }
            }
        }
        else {
            repeat(2*numOfPassengers) {
                passengers.add(mutableStateListOf(Buttons()))
            }
            repeat(2*numOfPassengers) {
                isClickPerPassenger.add(mutableStateListOf(IsClickedBaggage()))
            }
            if(state == "BaggageFromMore") {
                repeat(2*numOfPassengers) {
                    limit.add(mutableIntStateOf(limitBaggageFromMore[it].intValue))
                }
            }
        }
    }

    var baggage23kgPriceInbound = ""
    var baggage23kgPriceOutbound = ""
    var baggage32kgPriceInbound = ""
    var baggage32kgPriceOutbound = ""

    if(state == "Baggage&Pets") {
        when(classTypeInbound) {
            "Economy" -> {
                baggage23kgPriceInbound = "15€"
                baggage32kgPriceInbound = "25€"
            }
            "Flex" -> {
                baggage23kgPriceInbound = "Free"
                baggage32kgPriceInbound = "25€"
            }
            "Business" -> {
                baggage23kgPriceInbound = "Free"
                baggage32kgPriceInbound = "Free"
            }
        }
        when(classTypeOutbound) {
            "Economy" -> {
                baggage23kgPriceOutbound = "15€"
                baggage32kgPriceOutbound = "25€"
            }
            "Flex" -> {
                baggage23kgPriceOutbound = "Free"
                baggage32kgPriceOutbound = "25€"
            }
            "Business" -> {
                baggage23kgPriceOutbound = "Free"
                baggage32kgPriceOutbound = "Free"
            }
        }
    }
    else {
        baggage23kgPriceInbound = "15€"
        baggage23kgPriceOutbound = "15€"
        baggage32kgPriceInbound = "25€"
        baggage32kgPriceOutbound = "25€"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = if (state == "BaggageFromMore") 60.dp else 0.dp)
    )
    {
        items(numOfPassengers) { index ->
            if (index == 0) {
                if(state != "BaggageFromMore") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Baggage",
                            fontSize = 22.sp,
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
                        Icon(
                            Icons.Filled.Luggage,
                            contentDescription = "Luggage",
                            modifier = Modifier.padding(start = 5.dp, end = 45.dp, top = 15.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.baggage),
                    contentDescription = "baggage",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f)
                        .padding(start = 10.dp, end = 10.dp)
                )
                Row {
                    Icon(
                        Icons.Filled.WarningAmber,
                        contentDescription = "Warning",
                        modifier = Modifier.padding(start = 10.dp, top = 15.dp),
                        tint = Color(0xFF023E8A)
                    )
                    Text(
                        text = "Αll passengers are entitled to a free 8kg baggage in the aircraft cabin.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp),
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
                Text(
                    text = "Outbound",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 5.dp),
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
                text =
                if (passengersInfo[index].gender.value == "Female")
                    "Mrs ${passengersInfo[index].firstname.value} ${passengersInfo[index].lastname.value}"
                else
                    "Mr ${passengersInfo[index].firstname.value} ${passengersInfo[index].lastname.value}",
                fontSize = 20.sp,
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = if (index == 0) 10.dp else 5.dp
                ),
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
            if(state == "Baggage&Pets" || (state == "BaggageFromMore" && limitBaggageFromMore[index].intValue < 5)) {
                Column {
                    passengers[index].forEachIndexed{ buttonIndex,_ ->
                        Row(
                            modifier = Modifier
                                .padding(start = 30.dp, top = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column {
                                Text(
                                    text = "Baggage ${buttonIndex + 1}", fontSize = 16.sp,
                                    modifier = Modifier.padding(
                                        top = 32.dp,
                                        end = 5.dp
                                    ),
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
                            Column(verticalArrangement = Arrangement.Center) {
                                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Text(
                                        text = "23kg",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(
                                            start = 25.dp,
                                            top = 3.dp
                                        ),
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
                                        Icons.Filled.Luggage,
                                        contentDescription = "Luggage",
                                        modifier = Modifier.padding(
                                            start = 2.dp
                                        ),
                                        tint = Color(0xFF023E8A)
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        if(!isClickPerPassenger[index][buttonIndex].isClicked23kg.value) {
                                            if(isClickPerPassenger[index][buttonIndex].isClicked32kg.value){
                                                isClickPerPassenger[index][buttonIndex].isClicked32kg.value = false
                                                baggagePerPassenger[index][1].intValue -= 1
                                                if(classTypeOutbound == "Business" && buttonIndex == 0){
                                                    tempBaggagePrice.intValue -= 0
                                                }
                                                else{
                                                    tempBaggagePrice.intValue -= 25
                                                }
                                            }
                                            isClickPerPassenger[index][buttonIndex].isClicked23kg.value = true
                                            baggagePerPassenger[index][0].intValue += 1
                                            if((classTypeOutbound == "Flex" || classTypeOutbound == "Business") && buttonIndex == 0){
                                                tempBaggagePrice.intValue += 0
                                            }
                                            else{
                                                tempBaggagePrice.intValue += 15
                                            }
                                        }
                                        if(!passengers[index][buttonIndex].firstButton.value) {
                                            passengers[index][buttonIndex].firstButton.value =
                                                !passengers[index][buttonIndex].firstButton.value
                                        }
                                        passengers[index][buttonIndex].secondButton.value = false
                                    },
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(40.dp)
                                        .padding(
                                            start = 5.dp,
                                            top = 2.dp,
                                            end = 5.dp
                                        ),
                                    shape = RectangleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor =
                                        if (passengers[index][buttonIndex].firstButton.value) Color(0xFF023E8A)
                                        else if (passengers[index][buttonIndex].secondButton.value) Color.Transparent
                                        else Color.Transparent,
                                        contentColor = if (passengers[index][buttonIndex].firstButton.value) Color.White else Color(
                                            0xFF023E8A
                                        )
                                    )

                                ) {
                                    Text(text = if (buttonIndex == 0 && state != "BaggageFromMore") baggage23kgPriceOutbound else "15€",
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ))
                                }
                            }
                            Column {
                                Row {
                                    Text(
                                        text = "32kg",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(
                                            start = 25.dp,
                                            top = 3.dp
                                        ),
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
                                        Icons.Filled.Luggage,
                                        contentDescription = "Luggage",
                                        modifier = Modifier.padding(
                                            start = 2.dp
                                        ),
                                        tint = Color(0xFF023E8A)
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        if(!isClickPerPassenger[index][buttonIndex].isClicked32kg.value) {
                                            if(isClickPerPassenger[index][buttonIndex].isClicked23kg.value){
                                                isClickPerPassenger[index][buttonIndex].isClicked23kg.value = false
                                                baggagePerPassenger[index][0].intValue -= 1
                                                if((classTypeOutbound == "Flex" || classTypeOutbound == "Business") && buttonIndex == 0){
                                                    tempBaggagePrice.intValue -= 0
                                                }
                                                else {
                                                    tempBaggagePrice.intValue -= 15
                                                }
                                            }
                                            isClickPerPassenger[index][buttonIndex].isClicked32kg.value = true
                                            baggagePerPassenger[index][1].intValue += 1
                                            if(classTypeOutbound == "Business" && buttonIndex == 0){
                                                tempBaggagePrice.intValue += 0
                                            }
                                            else{
                                                tempBaggagePrice.intValue += 25
                                            }
                                        }
                                        if(!passengers[index][buttonIndex].secondButton.value){
                                            passengers[index][buttonIndex].secondButton.value =
                                                !passengers[index][buttonIndex].secondButton.value
                                        }
                                        passengers[index][buttonIndex].firstButton.value = false
                                    },
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(40.dp)
                                        .padding(
                                            start = 5.dp,
                                            top = 2.dp,
                                            end = 5.dp
                                        ),
                                    shape = RectangleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor =
                                        if (passengers[index][buttonIndex].secondButton.value) Color(0xFF023E8A)
                                        else if (passengers[index][buttonIndex].firstButton.value) Color.Transparent
                                        else Color.Transparent,
                                        contentColor = if (passengers[index][buttonIndex].secondButton.value) Color.White else Color(
                                            0xFF023E8A
                                        )
                                    )
                                ) {
                                    Text(if (buttonIndex == 0 && state != "BaggageFromMore") baggage32kgPriceOutbound else "25€",
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ))
                                }
                            }
                            if(buttonIndex == passengers[index].size-1){
                                Row{
                                    //Button for removing piece of baggage in specific passenger
                                    IconButton(
                                        modifier = Modifier.padding(top = 15.dp),
                                        onClick = {
                                            if(isClickPerPassenger[index][buttonIndex].isClicked23kg.value){
                                                isClickPerPassenger[index][buttonIndex].isClicked23kg.value = false
                                                passengers[index][buttonIndex].firstButton.value = false
                                                baggagePerPassenger[index][0].intValue -= 1
                                                if(buttonIndex == 0 && (classTypeOutbound == "Flex" || classTypeOutbound == "Business")){
                                                    tempBaggagePrice.intValue -= 0
                                                }
                                                else{
                                                    tempBaggagePrice.intValue -= 15
                                                }
                                            }
                                            else if(isClickPerPassenger[index][buttonIndex].isClicked32kg.value){
                                                isClickPerPassenger[index][buttonIndex].isClicked32kg.value = false
                                                baggagePerPassenger[index][1].intValue -= 1
                                                passengers[index][buttonIndex].secondButton.value = false
                                                if(buttonIndex == 0 && classTypeOutbound == "Business"){
                                                    tempBaggagePrice.intValue -= 0
                                                }
                                                else{
                                                    tempBaggagePrice.intValue -= 25
                                                }
                                            }

                                            if(buttonIndex != 0) {
                                                passengers[index].removeAt(passengers[index].size - 1)
                                                if(state == "BaggageFromMore") {
                                                    limit[index].intValue --
                                                }
                                            }
                                        }) {
                                        Icon(
                                            Icons.Filled.DeleteForever,
                                            contentDescription = "deleteBaggage",
                                            modifier = Modifier.padding(
                                                start = 2.dp,
                                                top = 12.dp
                                            ),
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }

                        if (buttonIndex == passengers[index].size - 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp)
                            ) {
                                //Button for adding new piece of baggage in specific passenger
                                IconButton(onClick = {
                                    if(state == "Baggage&Pets") {
                                        if(buttonIndex < 4) {
                                            passengers[index].add(Buttons())
                                            isClickPerPassenger[index].add(IsClickedBaggage())
                                        }
                                    }
                                    else {
                                        if(limit[index].intValue < 4) {
                                            limit[index].intValue ++
                                            passengers[index].add(Buttons())
                                            isClickPerPassenger[index].add(IsClickedBaggage())
                                        }
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.AddCircleOutline,
                                        contentDescription = "addBaggage",
                                        modifier = Modifier.padding(
                                            start = 2.dp,
                                            top = 12.dp
                                        ),
                                        tint = Color(0xFF023E8A)
                                    )
                                }
                                Text(
                                    text = "Add a new piece of baggage",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(
                                        fonts = listOf(
                                            Font(
                                                resId = R.font.opensans
                                            )
                                        )
                                    ),
                                    color = Color(0xFF023E8A),
                                    modifier = Modifier.padding(top = 18.dp),
                                )
                            }
                            if(index < numOfPassengers-1) {
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
            else {
                Text(
                    text = "The passenger has already selected the maximum number of baggage pieces!",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.opensans
                            )
                        )
                    ),
                    color = Color(0xFF023E8A),
                    modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 18.dp),
                )
                if (index < numOfPassengers - 1) {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color(0xFF023E8A)
                    )
                }
            }
        }
        //***************************INBOUND*********************************
        if(pagePrevious.intValue == 1) {
            items(numOfPassengers) { index ->
                if (index == 0) {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color(0xFF023E8A)
                    )
                    Text(
                        text = "Inbound",
                        fontSize = 22.sp,
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
                    text =
                    if (passengersInfo[index].gender.value == "Female")
                        "Mrs ${passengersInfo[index].firstname.value} ${passengersInfo[index].lastname.value}"
                    else
                        "Mr ${passengersInfo[index].firstname.value} ${passengersInfo[index].lastname.value}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(
                        start = 10.dp,
                        top = if (index == 0) 10.dp else 5.dp
                    ),
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
                if(state == "Baggage&Pets" || (state == "BaggageFromMore" && limitBaggageFromMore[index + numOfPassengers].intValue < 5)) {
                    Column {
                        passengers[index + numOfPassengers].forEachIndexed { buttonIndex, _ ->
                            Row(
                                modifier = Modifier
                                    .padding(start = 30.dp, top = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column {
                                    Text(
                                        text = "Baggage ${buttonIndex + 1}", fontSize = 16.sp,
                                        modifier = Modifier.padding(
                                            top = 32.dp,
                                            end = 5.dp
                                        ),
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
                                Column(verticalArrangement = Arrangement.Center) {
                                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                                        Text(
                                            text = "23kg",
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(
                                                start = 25.dp,
                                                top = 3.dp
                                            ),
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
                                            Icons.Filled.Luggage,
                                            contentDescription = "Luggage",
                                            modifier = Modifier.padding(
                                                start = 2.dp
                                            ),
                                            tint = Color(0xFF023E8A)
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            if (!isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked23kg.value) {
                                                if (isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked32kg.value) {
                                                    isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked32kg.value =
                                                        false
                                                    baggagePerPassenger[index + numOfPassengers][1].intValue -= 1
                                                    if (classTypeInbound == "Business" && buttonIndex == 0) {
                                                        tempBaggagePrice.intValue -= 0
                                                    } else {
                                                        tempBaggagePrice.intValue -= 25
                                                    }
                                                }
                                                isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked23kg.value =
                                                    true
                                                baggagePerPassenger[index + numOfPassengers][0].intValue += 1
                                                if ((classTypeInbound == "Flex" || classTypeInbound == "Business") && buttonIndex == 0) {
                                                    tempBaggagePrice.intValue += 0
                                                } else {
                                                    tempBaggagePrice.intValue += 15
                                                }
                                            }
                                            if (!passengers[index + numOfPassengers][buttonIndex].firstButton.value) {
                                                passengers[index + numOfPassengers][buttonIndex].firstButton.value =
                                                    !passengers[index + numOfPassengers][buttonIndex].firstButton.value
                                            }
                                            passengers[index + numOfPassengers][buttonIndex].secondButton.value =
                                                false
                                        },
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(40.dp)
                                            .padding(
                                                start = 5.dp,
                                                top = 2.dp,
                                                end = 5.dp
                                            ),
                                        shape = RectangleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (passengers[index + numOfPassengers][buttonIndex].firstButton.value) Color(
                                                0xFF023E8A
                                            )
                                            else if (passengers[index + numOfPassengers][buttonIndex].secondButton.value) Color.Transparent
                                            else Color.Transparent,
                                            contentColor = if (passengers[index + numOfPassengers][buttonIndex].firstButton.value) Color.White else Color(
                                                0xFF023E8A
                                            )
                                        )
                                    ) {
                                        Text(
                                            text = if (buttonIndex == 0 && state != "BaggageFromMore") baggage23kgPriceInbound else "15€",
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
                                Column {
                                    Row {
                                        Text(
                                            text = "32kg",
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(
                                                start = 25.dp,
                                                top = 3.dp
                                            ),
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
                                            Icons.Filled.Luggage,
                                            contentDescription = "Luggage",
                                            modifier = Modifier.padding(
                                                start = 2.dp
                                            ),
                                            tint = Color(0xFF023E8A)
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            if (!isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked32kg.value) {
                                                if (isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked23kg.value) {
                                                    isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked23kg.value =
                                                        false
                                                    baggagePerPassenger[index + numOfPassengers][0].intValue -= 1
                                                    if ((classTypeInbound == "Flex" || classTypeInbound == "Business") && buttonIndex == 0) {
                                                        tempBaggagePrice.intValue -= 0
                                                    } else {
                                                        tempBaggagePrice.intValue -= 15
                                                    }
                                                }
                                                isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked32kg.value =
                                                    true
                                                baggagePerPassenger[index + numOfPassengers][1].intValue += 1
                                                if (classTypeInbound == "Business" && buttonIndex == 0) {
                                                    tempBaggagePrice.intValue += 0
                                                } else {
                                                    tempBaggagePrice.intValue += 25
                                                }
                                            }
                                            if (!passengers[index + numOfPassengers][buttonIndex].secondButton.value) {
                                                passengers[index + numOfPassengers][buttonIndex].secondButton.value =
                                                    !passengers[index + numOfPassengers][buttonIndex].secondButton.value
                                            }
                                            passengers[index + numOfPassengers][buttonIndex].firstButton.value =
                                                false
                                        },
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(40.dp)
                                            .padding(
                                                start = 5.dp,
                                                top = 2.dp,
                                                end = 5.dp
                                            ),
                                        shape = RectangleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                            if (passengers[index + numOfPassengers][buttonIndex].secondButton.value) Color(
                                                0xFF023E8A
                                            )
                                            else if (passengers[index + numOfPassengers][buttonIndex].firstButton.value) Color.Transparent
                                            else Color.Transparent,
                                            contentColor = if (passengers[index + numOfPassengers][buttonIndex].secondButton.value) Color.White else Color(
                                                0xFF023E8A
                                            )
                                        )
                                    ) {
                                        Text(
                                            if (buttonIndex == 0 && state != "BaggageFromMore") baggage32kgPriceInbound else "25€",
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
                                if (buttonIndex == passengers[index + numOfPassengers].size - 1) {
                                    Row {
                                        //Button for removing piece of baggage in specific passenger
                                        IconButton(
                                            modifier = Modifier.padding(top = 15.dp),
                                            onClick = {
                                                if (isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked23kg.value) {
                                                    isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked23kg.value =
                                                        false
                                                    passengers[index + numOfPassengers][buttonIndex].firstButton.value =
                                                        false
                                                    baggagePerPassenger[index + numOfPassengers][0].intValue -= 1
                                                    if (buttonIndex == 0 && (classTypeInbound == "Flex" || classTypeInbound == "Business")) {
                                                        tempBaggagePrice.intValue -= 0
                                                    } else {
                                                        tempBaggagePrice.intValue -= 15
                                                    }
                                                } else if (isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked32kg.value) {
                                                    isClickPerPassenger[index + numOfPassengers][buttonIndex].isClicked32kg.value =
                                                        false
                                                    baggagePerPassenger[index + numOfPassengers][1].intValue -= 1
                                                    passengers[index + numOfPassengers][buttonIndex].secondButton.value =
                                                        false
                                                    if (buttonIndex == 0 && classTypeInbound == "Business") {
                                                        tempBaggagePrice.intValue -= 0
                                                    } else {
                                                        tempBaggagePrice.intValue -= 25
                                                    }
                                                }

                                                if (buttonIndex != 0) {
                                                    passengers[index + numOfPassengers].removeAt(
                                                        passengers[index + numOfPassengers].size - 1
                                                    )
                                                    if(state == "BaggageFromMore") {
                                                        limit[index + numOfPassengers].intValue --
                                                    }
                                                }
                                            }) {
                                            Icon(
                                                Icons.Filled.DeleteForever,
                                                contentDescription = "deleteBaggage",
                                                modifier = Modifier.padding(
                                                    start = 2.dp,
                                                    top = 12.dp
                                                ),
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }

                            if (buttonIndex == passengers[index + numOfPassengers].size - 1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp)
                                ) {
                                    //Button for adding new piece of baggage in specific passenger
                                    IconButton(onClick = {
                                        if (state == "Baggage&Pets") {
                                            if (buttonIndex < 4) {
                                                passengers[index + numOfPassengers].add(Buttons())
                                                isClickPerPassenger[index + numOfPassengers].add(
                                                    IsClickedBaggage()
                                                )
                                            }
                                        } else {
                                            if (limit[index + numOfPassengers].intValue < 4) {
                                                limit[index + numOfPassengers].intValue ++
                                                passengers[index + numOfPassengers].add(Buttons())
                                                isClickPerPassenger[index + numOfPassengers].add(
                                                    IsClickedBaggage()
                                                )
                                            }
                                        }
                                    }) {
                                        Icon(
                                            Icons.Filled.AddCircleOutline,
                                            contentDescription = "addBaggage",
                                            modifier = Modifier.padding(
                                                start = 2.dp,
                                                top = 12.dp
                                            ),
                                            tint = Color(0xFF023E8A)
                                        )
                                    }
                                    Text(
                                        text = "Add a new piece of baggage",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(
                                            fonts = listOf(
                                                Font(
                                                    resId = R.font.opensans
                                                )
                                            )
                                        ),
                                        color = Color(0xFF023E8A),
                                        modifier = Modifier.padding(top = 18.dp),
                                    )
                                }
                                if (index < numOfPassengers - 1) {
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        thickness = 1.dp,
                                        color = Color(0xFF023E8A)
                                    )
                                } else {
                                    if (state != "BaggageFromMore") {
                                        ShowPetField(
                                            state,
                                            tempPetPrice,
                                            petSize,
                                            radioOptions,
                                            selectedOption,
                                            selectedOptionForYes,
                                            remember {
                                                mutableStateOf("")
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    Text(
                        text = "The passenger has already selected the maximum number of baggage pieces!",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        color = Color(0xFF023E8A),
                        modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 18.dp),
                    )
                    if (index < numOfPassengers - 1) {
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color(0xFF023E8A)
                        )
                    }
                }
            }
        }
        else {
            items(1) {
                if (state != "BaggageFromMore") {
                    ShowPetField(state, tempPetPrice, petSize, radioOptions, selectedOption, selectedOptionForYes, remember {
                        mutableStateOf("")
                    })
                }
            }
        }
    }
}

//radio buttons yes and no for pets
@Composable
fun radioButtonsPetYesNo(radioOptions: List<String>,
                         selectedOption: MutableState<String>): MutableState<String> {

    Row {
        radioOptions.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (option == selectedOption.value),
                    onClick = { selectedOption.value = option },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF00B4D8),
                        unselectedColor = Color(0xFF00B4D8)
                    )
                )
                Text(
                    text = option,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 5.dp),
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
    return selectedOption
}

//radio buttons for sizes of pets
@Composable
fun radioButtonsPetSize(onChange: (String) -> Unit,
                        selectedOptionForYes: MutableState<String>,
                        petSizeInfo: MutableState<String>): MutableState<String> {
    var radioOptions: List<String> = emptyList()
    when (petSizeInfo.value) {
        "" -> {
            radioOptions = listOf("Small (<8kg) - 35€","Medium (<25kg) - 50€", "Large (>25kg) - 90€")
        }
        "Small" -> {
            radioOptions = listOf("Medium (<25kg) - 15€", "Large (>25kg) - 55€")
        }
        "Medium" -> {
            radioOptions = listOf("Large (>25kg) - 40€")
        }
    }

    Column(Modifier.fillMaxWidth()) {
        radioOptions.forEach { option ->
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (option == selectedOptionForYes.value),
                    onClick = { selectedOptionForYes.value = option
                                onChange(selectedOptionForYes.value)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF00B4D8),
                        unselectedColor = Color(0xFF00B4D8)
                    )
                )
                Text(
                    text = option,
                    fontSize = 16.sp,
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
    return selectedOptionForYes
}

//function that shows the pets with the radio options
@Composable
fun ShowPetField(
    state: String,
    tempPetPrice: MutableIntState,
    petSize: MutableState<String>,
    radioOptions: List<String>,
    selectedOption: MutableState<String>,
    selectedOptionForYes: MutableState<String>,
    petSizeInfo: MutableState<String>
) {
    var option: MutableState<String> = remember {
        mutableStateOf("")
    }

    if(state != "PetsFromMore"){
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 3.dp,
            color = Color(0xFF023E8A)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp)
        ) {
            Text(
                text = "Pets",
                fontSize = 22.sp,
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
            Icon(
                Icons.Filled.Pets,
                contentDescription = "Pets",
                modifier = Modifier.padding(
                    start = 5.dp,
                    end = 45.dp,
                    top = 12.dp
                ),
                tint = Color(0xFF023E8A)
            )
        }
    }
    Column(
        Modifier
            .fillMaxHeight()
            .padding(bottom = 100.dp)) {
        if(state != "PetsFromMore") {
            Image(
                painter = painterResource(id = R.drawable.pet),
                contentDescription = "pet",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.5f)
                    .padding(start = 10.dp, end = 10.dp)
            )
            Text(
                text = "Are you travelling with pet?",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
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
            option = radioButtonsPetYesNo(radioOptions,selectedOption)
        }
        else {//state == "PetsFromMore"
            Text(
                text = "Travel with Pet",
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 10.dp),
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
            option.value = "Yes"
        }
        if((state=="PetsFromMore" && petSizeInfo.value != "Large")
            || state=="Baggage&Pets") {
            if (option.value == "Yes") {
                Text(
                    text = "Pet Size",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 5.dp, start = 10.dp),
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
                radioButtonsPetSize({ selectedOption ->
                    val endString = selectedOption.length
                    tempPetPrice.intValue =
                        selectedOption.substring(endString - 3, endString - 1).toInt()
                },selectedOptionForYes, petSizeInfo)
                when(tempPetPrice.intValue) {
                    35 -> {
                        petSize.value = "Small"
                    }
                    50 -> {
                        petSize.value = "Medium"
                    }
                    90 -> {
                        petSize.value = "Large"
                    }
                    15 -> {
                        petSize.value = "Medium"
                    }
                    55 -> {
                        petSize.value = "Large"
                    }
                    40 -> {
                        petSize.value = "Large"
                    }
                }
            }
            else {
                tempPetPrice.intValue = 0
                selectedOptionForYes.value = ""
            }
        }
        else {
            Column {
                Text(
                    text = "Pet Size",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 5.dp, start = 10.dp),
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
                Text(
                    text = "You have picked already the largest size of pet(>25kg)!",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp),
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

//final alert dialog that it is for completion of the reservation
@Composable
fun ShowDialogToCommitReservation(
    navController: NavController,
    selectedIndex: MutableIntState,
    showDialog: MutableState<Boolean>,
    passengersCount: MutableIntState,
    passengers: MutableList<PassengerInfo>,
    seats: MutableList<MutableList<MutableState<String>>>,
    selectedFlights: MutableList<SelectedFlightDetails>,
    selectedFlightOutbound: MutableIntState,
    selectedFlightInbound: MutableIntState,
    baggagePerPassenger: MutableList<MutableList<MutableIntState>>,
    classTypeOutbound: MutableState<String>,
    classTypeInbound: MutableState<String>,
    pagePrevious: MutableIntState,
    totalPrice: MutableDoubleState,
    prevTotalPrice: MutableDoubleState,
    petSize: MutableState<String>,
    finishReservation: MutableIntState,
    airportFrom: MutableState<String>,
    airportTo: MutableState<String>,
    whatAirport: MutableIntState,
    bookingFailed: MutableState<Boolean>
) {

    val showDialogConfirm = remember {
        mutableStateOf(false)
    }
    val insertNewBooking = remember {
        mutableStateOf(false)
    }
    val bookingReference = remember {
        mutableStateOf("")
    }
    val showBookingReference = remember {
        mutableStateOf(false)
    }
    val ctx = LocalContext.current

    //api that makes insert query in the database to add the new data for the new booking that is created from the user
    LaunchedEffect(insertNewBooking.value) {
        if (insertNewBooking.value) {
            val url = "http://100.106.205.30:5000/flynow/new-booking"
            // on below line we are creating a variable for
            // our request queue and initializing it.
            val queue: RequestQueue = Volley.newRequestQueue(ctx)
            // on below line we are creating a variable for request
            // and initializing it with json object request
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            val passengersJSONArray = JSONArray()
            for (i in 0 until passengersCount.intValue) {
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
            if (pagePrevious.intValue == 0) {
                for (i in 0 until passengersCount.intValue) {
                    val seatsId = JSONObject()
                    val seatsOutbound = JSONObject()
                    val seatJSONObject = JSONObject()
                    seatJSONObject.put("email", passengers[i].email.value)
                    if (selectedFlightOutbound.intValue == 0) {
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
                for (i in 0 until passengersCount.intValue) {
                    val seatsOutbound = JSONObject()
                    val seatsInbound = JSONObject()
                    val seatJSONObjectOutbound = JSONObject()
                    val seatJSONObjectInbound = JSONObject()
                    val seatsPerPassenger = JSONObject()
                    seatsPerPassenger.put("email", passengers[i].email.value)

                    if (selectedFlightOutbound.intValue == 0) {
                        seatJSONObjectOutbound.put("flightid_1", selectedFlights[0].flightId.value)
                        seatJSONObjectOutbound.put("seat_1", seats[i][0].value)
                        seatsOutbound.put("direct", seatJSONObjectOutbound)
                        seatsPerPassenger.put("outbound", seatsOutbound)
                        if (selectedFlightInbound.intValue == 0) {
                            seatJSONObjectInbound.put(
                                "flightid_1",
                                selectedFlights[1].flightId.value
                            )
                            seatJSONObjectInbound.put(
                                "seat_1",
                                seats[i + passengersCount.intValue][0].value
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
                                seats[i + passengersCount.intValue][0].value
                            )
                            seatJSONObjectInbound.put(
                                "flightid_2",
                                selectedFlights[2].flightId.value
                            )
                            seatJSONObjectInbound.put(
                                "seat_2",
                                seats[i + passengersCount.intValue][1].value
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
                        if (selectedFlightInbound.intValue == 0) {
                            seatJSONObjectInbound.put(
                                "flightid_1",
                                selectedFlights[2].flightId.value
                            )
                            seatJSONObjectInbound.put(
                                "seat_1",
                                seats[i + passengersCount.intValue][0].value
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
                                seats[i + passengersCount.intValue][0].value
                            )
                            seatJSONObjectInbound.put(
                                "flightid_2",
                                selectedFlights[3].flightId.value
                            )
                            seatJSONObjectInbound.put(
                                "seat_2",
                                seats[i + passengersCount.intValue][1].value
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
            if (pagePrevious.intValue == 0) {
                for (i in 0 until passengersCount.intValue) {
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
                for (i in 0 until passengersCount.intValue) {
                    val baggageJSONObject = JSONObject()
                    val baggageJSONObjectOutbound = JSONObject()
                    val baggageJSONObjectInbound = JSONObject()
                    baggageJSONObject.put("email", passengers[i].email.value)
                    baggageJSONObjectOutbound.put("baggage23kg", baggagePerPassenger[i][0].intValue)
                    baggageJSONObjectOutbound.put("baggage32kg", baggagePerPassenger[i][1].intValue)
                    baggageJSONObject.put("outbound", baggageJSONObjectOutbound)
                    baggageJSONObjectInbound.put(
                        "baggage23kg",
                        baggagePerPassenger[i + passengersCount.intValue][0].intValue
                    )
                    baggageJSONObjectInbound.put(
                        "baggage32kg",
                        baggagePerPassenger[i + passengersCount.intValue][1].intValue
                    )
                    baggageJSONObject.put("inbound", baggageJSONObjectInbound)
                    baggageJSONArray.put(baggageJSONObject)
                }
                jsonObject.put("baggage", baggageJSONArray)
            }
            jsonObject.put("classTypeOutbound", classTypeOutbound.value)
            if (pagePrevious.intValue == 1) {
                jsonObject.put("classTypeInbound", classTypeInbound.value)
            }
            jsonObject.put("petSize", petSize.value)
            jsonObject.put("price", totalPrice.doubleValue)

            jsonArray.put(jsonObject)

            val request = JsonArrayRequest(Request.Method.POST, url, jsonArray, { response ->
                try {
                    if(response.getJSONObject(0).getBoolean("success")) {
                        bookingReference.value = response.getJSONObject(0).getString("bookingId")
                    }
                    else {
                        bookingFailed.value = true
                        showBookingReference.value = false
                        showDialogConfirm.value = false
                        showDialog.value = false
                        finishReservation.intValue = 1
                        baggagePerPassenger.clear()
                        seats.clear()
                        petSize.value = ""
                        totalPrice.doubleValue = prevTotalPrice.doubleValue
                        navController.navigate(Seats.route) {
                            popUpTo(BaggageAndPets.route)
                            launchSingleTop = true
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
            delay(3000)
            insertNewBooking.value = false
            showBookingReference.value = true
        }
    }

    if (showDialog.value || showDialogConfirm.value) {
        Box(contentAlignment = Alignment.Center) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(
                        text = if (showDialog.value) "Confirm Reservation" else if (!insertNewBooking.value) "Reservation Booked Successfully!" else "",
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
                    if (!insertNewBooking.value) {
                        Icon(
                            if (showDialog.value) Icons.Filled.QuestionMark
                            else Icons.Filled.Verified,
                            contentDescription = "question",
                            modifier =
                            if (showDialog.value) Modifier.padding(start = 190.dp)
                            else Modifier.padding(top = 33.dp, start = 125.dp),
                            tint = Color(0xFF023E8A)
                        )
                    }
                },
                text = {
                    if (showDialog.value && !insertNewBooking.value) {
                        Text(
                            text = "Are you sure you want to book this flight?",
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
                    if (insertNewBooking.value) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp, bottom = 15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF023E8A)
                            )
                        }
                    }
                    if (showBookingReference.value) {
                        Text(
                            text = "Your booking reference is: ${bookingReference.value}",
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
                },
                confirmButton = {
                    if (showDialog.value && !insertNewBooking.value) {
                        Button(
                            onClick = {
                                insertNewBooking.value = true
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
                        if (!insertNewBooking.value) {
                            Button(
                                onClick = {
                                    showBookingReference.value = false
                                    showDialogConfirm.value = false
                                    showDialog.value = false
                                    finishReservation.intValue = 1
                                    passengersCount.intValue = 1
                                    passengers.clear()
                                    seats.clear()
                                    baggagePerPassenger.clear()
                                    selectedFlights.forEach { flight ->
                                        flight.flightId.value = ""
                                        flight.airplaneModel.value = ""
                                        flight.arrivalCity.value = ""
                                        flight.departureCity.value = ""
                                    }
                                    selectedFlightOutbound.intValue = 0
                                    selectedFlightInbound.intValue = 0
                                    classTypeOutbound.value = ""
                                    classTypeInbound.value = ""
                                    petSize.value = ""
                                    totalPrice.doubleValue = 0.0
                                    pagePrevious.intValue = 0
                                    prevTotalPrice.doubleValue = 0.0
                                    selectedIndex.intValue = 0
                                    airportFrom.value = ""
                                    airportTo.value = ""
                                    whatAirport.intValue = 0
                                    navController.navigate(Home.route) {
                                        popUpTo(BaggageAndPets.route)
                                        launchSingleTop = true
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
                    if (showDialog.value && !insertNewBooking.value) {
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
}


