package com.example.financemanager.ui.composable.initial

import android.Manifest
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.financemanager.ui.composable.Screen
import com.example.financemanager.viewmodel.InitialVM

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Permission(navController: NavController, viewModel: InitialVM) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    val permissionsToRequest = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.POST_NOTIFICATIONS
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.all { it.value }) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Permissions.route) { inclusive = true }
                }
            }
        }
    )

    var showRationale by remember {
        mutableStateOf(permissionsToRequest.any {
            activity?.let { act -> ActivityCompat.shouldShowRequestPermissionRationale(act, it) } == true
        })
    }

    // Check if permissions are already granted on start
    LaunchedEffect(Unit) {
        val allGranted = viewModel.hasOptionalPermission(context) && viewModel.hasMandatoryPermissions(context)
        if (allGranted) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Permissions.route) { inclusive = true }
            }
        } else if (!showRationale) {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val permissionRequestText = if (showRationale) {
            "The app cannot function without the permission, the app only reads bank transactions from your SMS. If uncomfortable with permissions, please uninstall the app."
        } else {
            "We require SMS permissions to automatically track your bank transactions from your SMS messages."
        }

        Text(
            text = permissionRequestText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { requestPermissionLauncher.launch(permissionsToRequest) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Permissions")
        }
    }
}
