package com.example.financemanager.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanager.ui.LOGIN_ROUTE
import com.example.financemanager.viewmodel.LoginViewModel

// Assuming you are using a ViewModel provider in a real app, 
// but sticking to your pattern for now.
val loginViewModel = LoginViewModel()

@Composable
fun LoginScreen(navController: NavController) {
    val isUserLoaded by loginViewModel.isUserLoaded.collectAsState()
    
    LaunchedEffect(isUserLoaded) {
        if (isUserLoaded && loginViewModel.userManager.user != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(LOGIN_ROUTE) { inclusive = true }
            }
        }
    }

    if (!isUserLoaded) {
        LoadingScreen()
    } else if (loginViewModel.userManager.user == null) {
        SignUpContent(navController)
    }
}

@Composable
fun SignUpContent(navController: NavController) {
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Button(
            onClick = { 
                if (firstName.isNotBlank() && lastName.isNotBlank()) {
                    loginViewModel.signUp(firstName, lastName)
                    navController.navigate(Screen.Home.route)
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
