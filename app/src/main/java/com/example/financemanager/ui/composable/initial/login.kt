package com.example.financemanager.ui.composable.initial

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.ui.theme.FinanceManagerTheme
import com.example.financemanager.viewmodel.InitialVM

@Composable
fun LoginScreen(navController: NavController, viewModel: InitialVM) {
    val isUserLoaded by viewModel.isUserLoaded.collectAsState()
    val user by viewModel.user.collectAsState()

    if (isUserLoaded) {
        if (user == null) {
            SignUpContent(onSignUp = { first, last -> viewModel.signUp(first, last) })
        } else {
            LaunchedEffect(Unit) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun SignUpContent(onSignUp: (String, String) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Finance Manager",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Button(
            onClick = { 
                if (firstName.isNotBlank() && lastName.isNotBlank()) {
                    onSignUp(firstName, lastName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firstName.isNotBlank() && lastName.isNotBlank()
        ) {
            Text("Create Account")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading your data...", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    FinanceManagerTheme {
        SignUpContent(onSignUp = { _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    FinanceManagerTheme {
        LoadingScreen()
    }
}
