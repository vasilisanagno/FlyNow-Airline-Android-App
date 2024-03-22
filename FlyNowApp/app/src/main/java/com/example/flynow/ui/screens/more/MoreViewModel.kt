package com.example.flynow.ui.screens.more

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//view model class that keeps the indication classes to hide
//the highlight of the click event in the cards of the more screen
@HiltViewModel
class MoreViewModel @Inject constructor(): ViewModel() {
    val highlightIndication by mutableStateOf(MyHighlightIndication())
    val interactionSource by mutableStateOf(MutableInteractionSource())

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
}