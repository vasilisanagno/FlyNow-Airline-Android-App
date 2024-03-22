package com.example.flynow.ui.screens.cars.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.SharedViewModel
import com.example.flynow.ui.screens.cars.CarsViewModel

//component that shows the car card with the details about the car
@Composable
fun CarCard(
    sharedViewModel: SharedViewModel,
    carsViewModel: CarsViewModel,
    index: Int
) {
    Card(
        modifier = Modifier
            .padding(
                top = if (index == 0)
                    20.dp else 30.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = if (index == sharedViewModel.listOfCars.size - 1) 80.dp else 0.dp
            )
            .width(350.dp)
            .height(250.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = ShapeDefaults.Small,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        ) {
            Column(modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.Start) {
                Image(
                    bitmap = sharedViewModel.listOfCars[index].carImage.value.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(160.dp, 160.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = sharedViewModel.listOfCars[index].model.value,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        ),
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF023E8A)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "person",
                            tint = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Text(
                            text = "x5",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Outlined.Luggage,
                            contentDescription = "baggage",
                            tint = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = "x2",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            color = Color(0xFF023E8A),
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column {
                Text(
                    text = sharedViewModel.listOfCars[index].company.value,
                    fontSize = 22.sp,
                    fontFamily = FontFamily(
                        fonts = listOf(
                            Font(
                                resId = R.font.roboto
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 10.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF023E8A),
                    textDecoration = TextDecoration.Underline
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 25.dp),
                    contentAlignment = Alignment.CenterEnd) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = sharedViewModel.locationToRentCar,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.gilroy
                                    )
                                )
                            ),
                            modifier = Modifier.padding(top = 10.dp, end = 15.dp),
                            color = Color(0xFF023E8A)
                        )
                        Text(
                            text = "Price Per Day",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.opensans
                                    )
                                )
                            ),
                            modifier = Modifier.padding(top = 10.dp, end = 15.dp),
                            color = Color(0xFF023E8A)
                        )
                        Text(
                            text = "${sharedViewModel.listOfCars[index].price.doubleValue} €",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                fonts = listOf(
                                    Font(
                                        resId = R.font.lato
                                    )
                                )
                            ),
                            modifier = Modifier.padding(top = 3.dp, bottom = 20.dp, end = 15.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF023E8A)
                        )
                        Button(
                            onClick = {
                                if(sharedViewModel.listOfCars[index].price.doubleValue != 0.0) {
                                    if(!sharedViewModel.listOfButtonsCars[index].value) {
                                        sharedViewModel.listOfButtonsCars.forEach { carButton ->
                                            carButton.value = false
                                        }
                                        sharedViewModel.listOfButtonsCars[index].value = true
                                        carsViewModel.totalPriceForCar = sharedViewModel.listOfCars[index].price.doubleValue
                                    }
                                    else {
                                        sharedViewModel.listOfButtonsCars[index].value = false
                                        carsViewModel.totalPriceForCar = 0.0
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0096c7)
                            ),
                            contentPadding = PaddingValues(
                                start = 30.dp,
                                end = 30.dp
                            ),
                            modifier = Modifier
                                .padding(bottom = 25.dp, end = 15.dp)
                                .height(25.dp)
                        ) {
                            Text(
                                text = if(sharedViewModel.listOfButtonsCars[index].value) "Selected" else "Select",
                                fontSize = 17.sp,
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
        }
    }
}