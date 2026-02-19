package com.example.expensemanagement.composable.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitleBar(title: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxWidth().padding(5.dp).then(modifier)) {
        Text(
            modifier = Modifier
                .padding(start = 20.dp, top = 6.dp, bottom = 5.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        // HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
    }

}