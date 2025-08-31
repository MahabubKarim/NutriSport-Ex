package com.nutrisport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nutrisport.adminpanel.AdminPanelScreen
import com.nutrisport.auth.AuthScreen
import com.nutrisport.home.HomeGraphScreen
import com.nutrisport.manageproduct.ManageProductScreen
import com.nutrisport.manageproduct.ManageProductViewModel
import com.nutrisport.profile.ProfileScreen
import com.nutrisport.shared.navigation.Screen
import com.nutrisport.shared.util.PreferenceUtils
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupNavGraph(startDestination: Screen = Screen.Auth) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val preferenceUtils = koinInject<PreferenceUtils>()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                navigateToHome = {
                    navController.navigate(Screen.HomeGraph) {
                        popUpTo<Screen.Auth> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<Screen.HomeGraph> {
            HomeGraphScreen(
                navigateToAuth = {
                    navController.navigate(Screen.Auth) {
                        popUpTo<Screen.HomeGraph> {
                            inclusive = true
                        }
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                navigateToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                }
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.AdminPanel> {
            AdminPanelScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToManageProduct = { productId ->
                    navController.navigate(Screen.ManageProduct(productId))
                }
            )
        }
        composable<Screen.ManageProduct> { backStackEntry ->
            // val viewModel: ManageProductViewModel = koinViewModel()

            /** The library serializes Screen.ManageProduct into a route string
            (e.g. /manageProduct?id=123) and pushes it onto the back stack.
            toRoute<T>() deserializes the arguments back into your Screen.ManageProduct data class.
            That’s where the id becomes available again.*/
            val id = backStackEntry.toRoute<Screen.ManageProduct>().id
            ManageProductScreen(
                id = id,
                navigateBack = {
                    navController.navigateUp()
                },
            )
        }
    }
}