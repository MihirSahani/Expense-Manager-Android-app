package com.example.financemanager.ui.composable.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financemanager.database.entity.User
import com.example.financemanager.viewmodel.UserVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserDetailsScreen(navController: NavController, viewModel: UserVM) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    // Initialize fields when user data is loaded
    LaunchedEffect(viewModel.user) {
        viewModel.user.value?.let {
            firstName = it.firstName
            lastName = it.lastName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    viewModel.updateUserDetails(
                        User(firstName = firstName, lastName = lastName, token = "")
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = firstName.isNotBlank() && lastName.isNotBlank()
            ) {
                Text("Save Changes")
            }
        }
    }
}
