package com.example.flynow.ui.screens.book.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flynow.R
import com.example.flynow.ui.screens.book.BookViewModel

//component that shows the switch button and the checkboxes in book screen
@Composable
fun SwitchButtonAndCheckBox(
    bookViewModel: BookViewModel
) {
    //Switch button "Direct Flights"
    Row {
        Switch(
            checked = bookViewModel.checked,
            onCheckedChange = {
                bookViewModel.checked = it
                if(!bookViewModel.checked) {
                    bookViewModel.amChecked = false
                    bookViewModel.pmChecked = false
                }
            },
            modifier = Modifier
                .padding(top = 20.dp, start = 10.dp),
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = Color(0xFF00B4D8),
                uncheckedThumbColor = Color(0xFF00B4D8),
                checkedTrackColor = Color(0xFF00B4D8)
            )
        )
        Text(
            "Direct flights",
            fontSize = 16.sp,
            modifier = Modifier.padding(top= 32.dp,start = 5.dp),
            fontFamily = FontFamily(
                fonts = listOf(
                    Font(
                        resId = R.font.opensans
                    )
                )
            )
        )
        //Checkboxes "AM Flights" and "PM flights"
        Row(
            horizontalArrangement = Arrangement.End) {
            Column {
                Row {
                    Checkbox(checked = bookViewModel.amChecked,
                        onCheckedChange = { isChecked -> bookViewModel.amChecked = isChecked },
                        modifier = Modifier
                            .height(36.dp)
                            .width(50.dp)
                            .padding(top = 30.dp, start = 30.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00B4D8),
                            uncheckedColor = Color(0xFF00B4D8)
                        ),
                        enabled = bookViewModel.checked
                    )
                    Text(
                        "AM flights (00:00-11:59)",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp,top = 23.dp),
                        fontFamily = FontFamily(
                            fonts = listOf(
                                Font(
                                    resId = R.font.opensans
                                )
                            )
                        )
                    )
                }
                Row {
                    Checkbox(checked = bookViewModel.pmChecked,
                        onCheckedChange = { isChecked -> bookViewModel.pmChecked = isChecked },
                        modifier = Modifier
                            .height(30.dp)
                            .width(50.dp)
                            .padding(top = 10.dp, start = 30.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00B4D8),
                            uncheckedColor = Color(0xFF00B4D8)
                        ),
                        enabled = bookViewModel.checked
                    )
                    Text(
                        "PM flights (12:00-23:59)",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp,top = 9.dp),
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