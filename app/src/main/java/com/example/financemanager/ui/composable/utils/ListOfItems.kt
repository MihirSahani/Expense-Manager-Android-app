package com.example.financemanager.ui.composable.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun <K, V> ListOfItems(
    items: Map<K, V>,
    modifier: Modifier = Modifier,
    content: @Composable (Pair<K, V>) -> Unit
) {
    ListOfItemsBase(items.toList(), modifier, content)
}

@Composable
fun <T> ListOfItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    ListOfItemsBase(items, modifier, content)
}

@Composable
private fun <T> ListOfItemsBase(
    items: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {

    LazyColumn(
        modifier = modifier
            // .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        itemsIndexed(items) { idx, item ->
            if (idx != 0) {
                HorizontalDivider(
                    thickness = 1.dp,
                    // color = LightGray,
                    modifier = Modifier.padding(4.dp)
                )
            }
            content(item)
        }
    }
}
