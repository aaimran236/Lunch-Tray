/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// Screen enum
enum class LunchTrayScreens(@StringRes val title: Int){
    Start(title=R.string.app_name),
    EntreeMenu(title=R.string.choose_entree),
    SideDishMenu(title=R.string.choose_side_dish),
    AccompanimentMenu(title=R.string.choose_accompaniment),
    Checkout(title=R.string.order_checkout)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar(
    lunchTrayScreens: LunchTrayScreens,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = lunchTrayScreens.title)) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


@Composable
fun LunchTrayApp(
    // Create ViewModel
    viewModel: OrderViewModel = viewModel() ,
    //Create Controller
    navController: NavHostController = rememberNavController()
) {
    //initialization of backstack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen= LunchTrayScreens.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreens.Start.name
    )

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                lunchTrayScreens = currentScreen,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // Navigation host
        NavHost(
            navController = navController,
            startDestination = LunchTrayScreens.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = LunchTrayScreens.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = { navController.navigate(LunchTrayScreens.EntreeMenu.name) },
                    modifier = Modifier.fillMaxSize())
            }

            composable(route= LunchTrayScreens.EntreeMenu.name){
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { onCancelButtonClickedAndNavigateToStart(viewModel=viewModel,navController=navController)},
                    onNextButtonClicked = {navController.navigate(LunchTrayScreens.SideDishMenu.name)},
                    onSelectionChanged = { viewModel.updateEntree(entree = it)},
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(route= LunchTrayScreens.SideDishMenu.name){
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { onCancelButtonClickedAndNavigateToStart(viewModel=viewModel,navController=navController)},
                    onNextButtonClicked = {navController.navigate(LunchTrayScreens.AccompanimentMenu.name)},
                    onSelectionChanged = { viewModel.updateSideDish(sideDish = it)},
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(route=LunchTrayScreens.AccompanimentMenu.name){
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { onCancelButtonClickedAndNavigateToStart(viewModel=viewModel,navController=navController)},
                    onNextButtonClicked = {navController.navigate(LunchTrayScreens.Checkout.name)},
                    onSelectionChanged = {viewModel.updateAccompaniment(accompaniment = it)},
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(route=LunchTrayScreens.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { onCancelButtonClickedAndNavigateToStart(viewModel=viewModel,navController=navController)},
                    onNextButtonClicked = {navController.navigate(LunchTrayScreens.Start.name)},
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

private fun onCancelButtonClickedAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
){
    viewModel.resetOrder()
    /*
    inclusive: A Boolean value that, if true, also pops (removes) the specified route. If false,
    popBackStack() will remove all destinations on top of—but not including—the start destination,
    leaving it as the topmost screen visible to the user.
    */
    navController.popBackStack(LunchTrayScreens.Start.name, inclusive = false)
}
