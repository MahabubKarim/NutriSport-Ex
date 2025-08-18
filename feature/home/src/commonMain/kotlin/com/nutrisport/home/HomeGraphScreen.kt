package com.nutrisport.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nutrisport.home.component.BottomBar
import com.nutrisport.home.model.BottomBarDestination
import com.nutrisport.shared.Surface
import com.nutrisport.shared.navigation.Screen

@Composable
fun HomeGraphScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()
    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()
            when {
                route.contains(BottomBarDestination.ProductsOverview.screen.toString()) -> BottomBarDestination.ProductsOverview
                route.contains(BottomBarDestination.Cart.screen.toString()) -> BottomBarDestination.Cart
                route.contains(BottomBarDestination.Categories.screen.toString()) -> BottomBarDestination.Categories
                else -> BottomBarDestination.ProductsOverview
            }
        }
    }

    Scaffold(
        containerColor = Surface
    ) { paddingValues ->
        Column (
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )

        ) {
            Spacer(modifier = Modifier.weight(1f))
            BottomBar(
                modifier = Modifier.padding(12.dp),
                customer = null,
                selected = selectedDestination,
                onSelect = { destination ->
                    navController.navigate(destination.screen) {
                        launchSingleTop = true
                        popUpTo<Screen.ProductsOverview> {
                            saveState = true
                            inclusive = false
                        }
                        restoreState = true
                    }
                },
            )
        }
    }
}