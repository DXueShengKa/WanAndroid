package com.google.accompanist.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder:@Composable ()->Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.body1,
    cursorBrush:SolidColor = SolidColor(MaterialTheme.colors.primary)
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value,
        onValueChange,
        modifier,
        textStyle = textStyle,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush
    ) {
        it()
        if (!interactionSource.collectIsFocusedAsState().value && value.isEmpty()) {
            CompositionLocalProvider(
                LocalTextStyle provides textStyle.copy(MaterialTheme.colors.primaryVariant)
            ) {
                placeholder()
            }
        }
    }
}