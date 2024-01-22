package com.example.flynow

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//screen that shows the more and has two parameters navController and selectedIndex
//because there is a bottom bar in this screen
@Composable
fun MoreScreen(navController: NavController,
               selectedIndex: MutableIntState) {

    val gradient = Brush.linearGradient(
        0.0f to Color(0xffdee2e6),
        500.0f to Color(0xff90e0ef),
        start = Offset.Zero,
        end = Offset.Infinite
    )
    //removes the card focus when is clicked
    val highlightIndication = remember { MyHighlightIndication() }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = Modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "More",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
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
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color(0xFF00B4D8))
        Column(modifier = Modifier
            .fillMaxSize()
            .background(gradient)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                //Card for "Check-In" and goes to "Check-In" when it is clicked
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .width(130.dp)
                        .height(130.dp)
                        .clickable(interactionSource = interactionSource,
                            indication = highlightIndication,
                            enabled = true, onClickLabel = null, onClick = {
                                navController.navigate(CheckIn.route) {
                                    popUpTo(More.route)
                                    launchSingleTop = true
                                }
                            }),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 20.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.FactCheck,
                            contentDescription = "checkIn",
                            tint = Color(0xFF00B4D8),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            "Check-In",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top= 20.dp),
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
                //Card for "Change your Booking" and goes to "My Booking" when it is clicked
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .width(130.dp)
                        .height(130.dp)
                        .clickable(interactionSource = interactionSource,
                            indication = highlightIndication,
                            enabled = true, onClickLabel = null, onClick = {
                                selectedIndex.intValue = 2
                                navController.navigate(MyBooking.route) {
                                    popUpTo(More.route)
                                    launchSingleTop = true
                                }
                            }),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 20.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.AirplaneTicket,
                            contentDescription = "myBooking",
                            tint = Color(0xFF00B4D8),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            "Change your Booking",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top= 20.dp),
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
            //a surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0f)
            ) {
                //list of selections in the more screen
                val textList = listOf("Upgrade to Business Class",
                    "Extra baggage", "Travelling with pets", "Rent a car",
                    "Upgrade wifi on board")
                LazyColumn(modifier = Modifier.padding(top = 92.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    items(textList.size) { buttonText ->
                        //these selections are clickable text and
                        ClickableText(
                            text =  AnnotatedString(textList[buttonText]),
                            onClick = {
                                //navigates to upgrade to business class route
                                if(textList[buttonText] == "Upgrade to Business Class"){
                                    navController.navigate(UpgradeClass.route) {
                                        popUpTo(More.route)
                                        launchSingleTop = true
                                    }
                                }
                                //navigates to rent a car route
                                else if(textList[buttonText] == "Rent a car") {
                                    navController.navigate(Car.route) {
                                        popUpTo(More.route)
                                        launchSingleTop = true
                                    }
                                }
                                //navigates to upgrade wifi on board route
                                else if(textList[buttonText] == "Upgrade wifi on board"){
                                    navController.navigate(WifiOnBoard.route) {
                                        popUpTo(More.route)
                                        launchSingleTop = true
                                    }
                                }
                                //navigates to travelling with pets route
                                else if(textList[buttonText] == "Travelling with pets"){
                                    navController.navigate(PetsFromMore.route) {
                                        popUpTo(More.route)
                                        launchSingleTop = true
                                    }
                                }
                                //navigates to extra baggage route
                                else if(textList[buttonText] == "Extra baggage"){
                                    navController.navigate(BaggageFromMore.route) {
                                        popUpTo(More.route)
                                        launchSingleTop = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(top = if (buttonText == 0) 28.dp else 0.dp)
                                .height(66.24.dp)
                                .border(width = 1.dp, color = Color.LightGray)
                                .background(color = Color.White)
                                .fillMaxWidth()
                                .wrapContentHeight(align = Alignment.CenterVertically),
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
                                textIndent = TextIndent(20.sp,0.sp)
                            )
                        )
                    }
                }
            }
        }
    }
}

//classes for removing the focusing in the card clicking
private class MyHighlightIndicationInstance(isEnabledState: androidx.compose.runtime.State<Boolean>) :
    IndicationInstance {
    private val isEnabled by isEnabledState
    override fun androidx.compose.ui.graphics.drawscope.ContentDrawScope.drawIndication() {
        drawContent()
        if (isEnabled) {
            drawRect(size = size, color = Color.White, alpha = 0.2f)
        }
    }
}

class MyHighlightIndication : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource):
            IndicationInstance {
        val isFocusedState = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            MyHighlightIndicationInstance(isEnabledState = isFocusedState)
        }
    }
}

