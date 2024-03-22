package com.example.flynow.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.flynow.R

//component that makes the style of the text field that is used throughout the app
@Composable
fun FlyNowTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    readOnly: Boolean,
    onTextChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = {},
    supportingText: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        label = { Text(label, fontSize = 16.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = Color(0xFF023E8A),
            focusedBorderColor = Color(0xFF023E8A),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFF00B4D8),
            errorContainerColor = Color.White,
            cursorColor = Color(0xFF023E8A)
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        placeholder = placeholder,
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
        readOnly = readOnly,
        isError = isError,
        enabled = enabled
    )
}