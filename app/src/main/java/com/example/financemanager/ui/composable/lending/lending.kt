package com.example.financemanager.ui.composable.lending

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financemanager.database.entity.Lending
import com.example.financemanager.ui.composable.utils.ListOfItems
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.composable.utils.MyText.timeRemaining
import com.example.financemanager.ui.composable.utils.MyText.toStringDate
import com.example.financemanager.viewmodel.LendingVM

@Composable
fun Lending(viewModel: LendingVM) {
    val lendings = viewModel.lendings.collectAsState()
    var isArchived by remember { mutableStateOf(false) }

    LendingContent(
        isArchived = isArchived,
        toggleIsArchived = { isArchived = !isArchived },
        lendings = lendings.value,
        onMarkPaid = { id, value -> viewModel.markPaid(id, value) }
    )
}

@Composable
fun LendingContent(
    isArchived: Boolean,
    toggleIsArchived: () -> Unit,
    lendings: List<Lending>,
    onMarkPaid: (Int, Boolean) -> Unit
) {
    val lendingGrouped = remember(lendings) {
        lendings
            .sortedByDescending { it.returnDate }
            .groupBy { it.isGiven }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyText.ScreenHeader(if(isArchived) "Archived Lendings" else "Lendings")
            IconButton(onClick = { toggleIsArchived() } ) {
                Icon(
                    imageVector = if (isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                    contentDescription = if (isArchived) "Show Active" else "Show Archived",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (lendings.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MyText.Body(text = "No lendings yet")
                }
            } else {
                val archLendingsGrouped = when (isArchived) {
                    true -> lendingGrouped.mapValues { (_, list) -> list.filter { it.isPaid } }
                    false -> lendingGrouped.mapValues { (_, list) -> list.filter { !it.isPaid } }
                }
                ListOfItems(
                    items = archLendingsGrouped,
                    headerContent = { isGiven ->
                        val type = if (isGiven) "Lent" else "Borrowed"
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MyText.Body(type)
                            HorizontalDivider(
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                ) {
                    LendingItem(it, onMarkPaid, isArchived)
                }
                // ListOfItems(
                //     if(isArchived) lendings.filter { it.isPaid }
                //     else lendings.filter { !it.isPaid }
                // ) { lending ->
                //     LendingItem(lending, onMarkPaid)
                // }
            }
        }
    }
}

@Composable
fun LendingItem(lending: Lending, onMarkPaid: (Int, Boolean) -> Unit, isArchived: Boolean) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val type = if (lending.isGiven) "Lent" else "Borrowed"
                MyText.Header1(text = lending.payee)
                // MyText.Body(text = "\t($type)")
            }
            MyText.TransactionAmount(
                amount = lending.amount,
                type = if (lending.isGiven) "income" else "expense"
            )
        }
        if (isExpanded) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    MyText.Body(
                        "Transaction: ${lending.transactionDate.toStringDate(pattern = "dd MMM yyyy")}"
                    )
                    if(!isArchived) MyText.Body(lending.returnDate.timeRemaining())
                }
                val text = if(lending.isPaid) "Mark as Unpaid" else "Mark as Paid"
                MyInput.Button(text, onClick = { onMarkPaid(lending.id, !lending.isPaid) })
            }
        } else {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if(!isArchived) MyText.Body(lending.returnDate.timeRemaining())
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LendingPreview() {
    LendingContent(
        isArchived = false,
        toggleIsArchived = {},
        lendings = listOf(
            Lending(id = 1, isGiven = true, payee = "Amit", amount = 1000.0, isPaid = false, transactionDate = System.currentTimeMillis(), returnDate = System.currentTimeMillis().plus(1000 * 60 * 60 * 24 * 7)),
            Lending(id = 2, isGiven = true, payee = "Shubham", amount = 50.0, isPaid = true, transactionDate = System.currentTimeMillis(), returnDate = 0L),
            Lending(id = 3, isGiven = false, payee = "Munshi", amount = 200.0, isPaid = false, transactionDate = System.currentTimeMillis(), returnDate = 0L)
        ),
        onMarkPaid = {_, _ ->},
    )
}