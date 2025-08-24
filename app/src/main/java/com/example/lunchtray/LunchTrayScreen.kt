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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
enum class LunchTrayAppScreens(@StringRes title: Int){
    Start(title=R.string.app_name),
    EntreeMenu(title=R.string.choose_entree),
    SideDishMenu(title=R.string.choose_side_dish),
    AccompanimentMenu(title=R.string.choose_accompaniment),
    Checkout(title=R.string.order_checkout)
}

// TODO: AppBar
fun LunchTrayAppBar(){

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp(
    // Create ViewModel
    viewModel: OrderViewModel = viewModel() ,
    //Create Controller
    navController: NavHostController = rememberNavController()
) {
    //initialization of backstack entry
    val backStackEntry by navController.currentBackStackEntryAsState()



    Scaffold(
        topBar = {
            LunchTrayAppBar()
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // Navigation host
        NavHost(
            navController=navController,
            startDestination = LunchTrayAppScreens.Start.name,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = LunchTrayAppScreens.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {navController.navigate(LunchTrayAppScreens.EntreeMenu.name)}
                )
            }

            composable(route= LunchTrayAppScreens.EntreeMenu.name){
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { /* TODO: Implement  */},
                    onNextButtonClicked = {/* TODO: Implement  */},
                    onSelectionChanged = { viewModel.updateEntree(entree = it)}
                )
            }

            composable(route= LunchTrayAppScreens.SideDishMenu.name){
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { /* TODO: Implement  */},
                    onNextButtonClicked = {/* TODO: Implement  */},
                    onSelectionChanged = { viewModel.updateSideDish(sideDish = it)}
                )
            }

            composable(route=LunchTrayAppScreens.AccompanimentMenu.name){
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { /* TODO: Implement  */},
                    onNextButtonClicked = {/* TODO: Implement  */},
                    onSelectionChanged = {viewModel.updateAccompaniment(accompaniment = it)}
                )
            }

            composable(route=LunchTrayAppScreens.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { /* TODO: Implement  */},
                    onNextButtonClicked = {/* TODO: Implement  */},
                )
            }
        }
    }
}
