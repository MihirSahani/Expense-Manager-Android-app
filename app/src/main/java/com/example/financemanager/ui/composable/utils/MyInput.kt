package com.example.financemanager.ui.composable.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object MyInput {
    @Composable
    fun TextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        placeholder: String = "",
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        isError: Boolean = false,
        errorMessage: String = "",
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        singleLine: Boolean = true,
        readOnly: Boolean = false,
        enabled: Boolean = true
    ) {
        Column(modifier = modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = label,
                        modifier = Modifier.graphicsLayer {
                            scaleX = 0.7f
                            scaleY = 0.7f
                            transformOrigin = TransformOrigin(0.3f, 0.3f)
                        }
                    )
                },
                placeholder = { Text(placeholder) },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isError = isError,
                singleLine = singleLine,
                readOnly = readOnly,
                enabled = enabled,
                keyboardOptions = keyboardOptions,
               shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,

                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,

                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                    cursorColor = MaterialTheme.colorScheme.primary,

                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorLeadingIconColor = MaterialTheme.colorScheme.error,
                    errorTrailingIconColor = MaterialTheme.colorScheme.error,
                    errorCursorColor = MaterialTheme.colorScheme.error,

                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (isError && errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }

    @Composable
    fun Switch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = modifier.scale(0.7f),
            colors = SwitchDefaults.colors(
                // Checked (ON) state
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedBorderColor = Color.Transparent,
                checkedIconColor = MaterialTheme.colorScheme.primary,

                // Unchecked (OFF) state
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                uncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,

                // Disabled state
                disabledCheckedThumbColor = Color.White.copy(alpha = 0.38f),
                disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                disabledUncheckedThumbColor = Color.White.copy(alpha = 0.38f),
                disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
            )
        )
    }

    @Composable
    fun Button(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null
    ) {
        // Button(
        //     onClick = onClick,
        //     enabled = enabled,
        //     shape = RoundedCornerShape(20),
        //     colors = ButtonDefaults.buttonColors(
        //         contentColor = MaterialTheme.colorScheme.onPrimary,
        //         disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        //         disabledContentColor = Color.White.copy(alpha = 0.6f)
        //     ),
        //     elevation = ButtonDefaults.buttonElevation(
        //         defaultElevation = 0.dp,
        //         pressedElevation = 0.dp
        //     ),
        //     // contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
        //     modifier = modifier.padding((-10).dp)
        // ) {
        //     // Optional icon + text layout
        //     if (leadingIcon != null) {
        //         leadingIcon()
        //         Spacer(modifier = Modifier.width(8.dp))
        //     }
        //     Text(
        //         text = text,
        //         fontSize = 16.sp,
        //         fontWeight = FontWeight.SemiBold,
        //         letterSpacing = 0.3.sp
        //     )
        // }

        Box(
            modifier
                .padding(2.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20))
                .clickable { onClick() }
                .padding(8.dp)
        ) {
            MyText.Header1(text, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
