package com.example.expensemanagement.composable.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SwipeToAction(
    onSwipeToRight: () -> Unit,
    onSwipeToLeft: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
             when(it) {
                 SwipeToDismissBoxValue.StartToEnd -> onSwipeToRight()
                 SwipeToDismissBoxValue.EndToStart -> onSwipeToLeft()
                 else -> {}
             }
            true
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            when(swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.fillMaxSize().background(Color.Red).wrapContentSize(Alignment.CenterStart).padding(12.dp))
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.fillMaxSize().background(Color.Red).wrapContentSize(Alignment.CenterStart).padding(12.dp))
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {
        content()
    }
}