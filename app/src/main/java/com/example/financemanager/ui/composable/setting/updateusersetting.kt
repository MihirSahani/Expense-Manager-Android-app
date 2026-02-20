package com.example.financemanager.ui.composable.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.User
import com.example.financemanager.ui.composable.utils.MyInput
import com.example.financemanager.ui.composable.utils.MyText
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.UserVM

@Composable
fun UpdateUserDetailsScreen(navController: NavController, viewModel: UserVM) {
    val user by viewModel.user.collectAsState()

    UpdateUserDetailsContent(
        initialFirstName = user?.firstName ?: "",
        initialLastName = user?.lastName ?: "",
        onSaveClick = { firstName, lastName ->
            val updatedUser = user?.copy(firstName = firstName, lastName = lastName) ?: User(
                firstName = firstName,
                lastName = lastName,
                token = ""
            )
            viewModel.updateUserDetails(updatedUser)
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserDetailsContent(
    initialFirstName: String,
    initialLastName: String,
    onSaveClick: (String, String) -> Unit
) {
    var firstName by remember(initialFirstName) { mutableStateOf(initialFirstName) }
    var lastName by remember(initialLastName) { mutableStateOf(initialLastName) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        MyText.ScreenHeader("Update User Details")

        MyInput.TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )

        MyInput.TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name",
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        )

        Button(
            onClick = { onSaveClick(firstName, lastName) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            enabled = firstName.isNotBlank() && lastName.isNotBlank(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Save Changes")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateUserDetailsPreview() {
    FinanceManagerTheme {
        UpdateUserDetailsContent(
            initialFirstName = "John",
            initialLastName = "Doe",
            onSaveClick = { _, _ -> }
        )
    }
}
