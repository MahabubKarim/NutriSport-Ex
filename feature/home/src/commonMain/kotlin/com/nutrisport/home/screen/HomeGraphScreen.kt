package com.nutrisport.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nutrisport.home.component.CustomDrawer
import com.nutrisport.home.navigation.BottomBarItem
import com.nutrisport.shared.SurfaceLighter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()
    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()
            when {
                route.contains(BottomBarItem.ProductsOverview.screen.toString()) -> BottomBarItem.ProductsOverview

                route.contains(BottomBarItem.Cart.screen.toString()) -> BottomBarItem.Cart

                route.contains(BottomBarItem.Categories.screen.toString()) -> BottomBarItem.Categories

                else -> BottomBarItem.ProductsOverview
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLighter)
    ) {
        CustomDrawer(
            customer = null,
            onProfileClick = {},
            onContactUsClick = {},
            onSignOutClick = {},
            onAdminPanelClick = {}
        )
        /* Scaffold(
             containerColor = Surface,
             topBar = {
                 CenterAlignedTopAppBar(
                     title = {
                         AnimatedContent(
                             targetState = selectedDestination
                         ) { destination ->
                             Text(
                                 text = destination.title,
                                 fontFamily = BebasNeueFont(),
                                 fontSize = FontSize.LARGE,
                                 color = TextPrimary
                             )
                         }
                     },
                     navigationIcon = {
                         IconButton(onClick = {}) {
                             Icon(
                                 painter = painterResource(Resources.Icon.Menu),
                                 contentDescription = "Menu Icon",
                                 tint = IconPrimary
                             )
                         }
                     },
                     colors = centerAlignedTopAppBarColors(
                         containerColor = Surface,
                         scrolledContainerColor = Surface,
                         navigationIconContentColor = IconPrimary,
                         titleContentColor = TextPrimary,
                         actionIconContentColor = IconPrimary
                     )
                 )
             }
         ) { paddingValues ->
             Column(
                 modifier = Modifier.fillMaxWidth()
                     .padding(
                         top = paddingValues.calculateTopPadding(),
                         bottom = paddingValues.calculateBottomPadding()
                     )
             ) {
                 NavHost(
                     navController = navController,
                     startDestination = Screen.ProductsOverview
                 ) {
                     composable<Screen.ProductsOverview> {}
                     composable<Screen.Cart> {}
                     composable<Screen.Categories> {}
                 }
                 Spacer(modifier = Modifier.weight(1f))
                 Box(
                     modifier = Modifier
                         .padding(all = 12.dp)
                 ) {
                     BottomBar(
                         customer = null,
                         selected = selectedDestination,
                         onSelect = { destination ->
                             navController.navigate(destination.screen) {
                                 // launchSingleTop = true
                                 popUpTo<Screen.ProductsOverview> {
                                     saveState = true
                                     // inclusive = false
                                 }
                                 restoreState = true
                             }
                         }
                     )
                 }
             }
         }*/
    }
}