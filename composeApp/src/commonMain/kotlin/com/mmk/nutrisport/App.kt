package com.mmk.nutrisport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.nutrisport.domain.repository.CustomerRepository
import com.nutrisport.navigation.SetupNavGraph
import com.nutrisport.shared.util.Constants
import com.nutrisport.shared.navigation.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val googleAuthProvider: GoogleAuthProvider = koinInject()
    val customerRepository = koinInject<CustomerRepository>()
    var appReady by remember { mutableStateOf(false) }
    val isUserAuthenticated = remember { customerRepository.getCurrentUserId() != null }
    val startDestination = remember {
        if (isUserAuthenticated) Screen.HomeGraph
        else Screen.Auth
    }

    MaterialTheme {
        LaunchedEffect(Unit) {
            googleAuthProvider
            appReady = true
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = appReady
        ) {
            SetupNavGraph(
                startDestination = startDestination
            )
        }
    }
}