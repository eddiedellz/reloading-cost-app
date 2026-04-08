package com.example.reloadcostcaluclator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reloadcostcaluclator.data.di.AppContainer
import com.example.reloadcostcaluclator.ui.screens.HomeScreen
import com.example.reloadcostcaluclator.ui.screens.LoadCostCalculatorScreen
import com.example.reloadcostcaluclator.ui.screens.loadrecipes.LoadCostSummaryScreen
import com.example.reloadcostcaluclator.ui.screens.components.AddEditBrassScreen
import com.example.reloadcostcaluclator.ui.screens.components.ComponentsScreen
import com.example.reloadcostcaluclator.ui.screens.components.AddEditBulletScreen
import com.example.reloadcostcaluclator.ui.screens.components.AddEditPowderScreen
import com.example.reloadcostcaluclator.ui.screens.components.AddEditPrimerScreen
import com.example.reloadcostcaluclator.ui.screens.components.BrassListScreen
import com.example.reloadcostcaluclator.ui.screens.components.BulletListScreen
import com.example.reloadcostcaluclator.ui.screens.components.PowderListScreen
import com.example.reloadcostcaluclator.ui.screens.components.PrimerListScreen
import com.example.reloadcostcaluclator.ui.screens.loadrecipes.AddEditLoadRecipeScreen
import com.example.reloadcostcaluclator.ui.screens.loadrecipes.LoadRecipeDetailScreen
import com.example.reloadcostcaluclator.ui.screens.loadrecipes.LoadRecipeListScreen

@Composable
fun AppNavHost(
    appContainer: AppContainer,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                loadRecipeRepository = appContainer.loadRecipeRepository,
                powderRepository = appContainer.powderRepository,
                primerRepository = appContainer.primerRepository,
                bulletRepository = appContainer.bulletRepository,
                brassRepository = appContainer.brassRepository,
                onComponentsClick = { navController.navigate(Routes.COMPONENTS) },
                onLoadsToolsClick = { navController.navigate(Routes.LOADS) },
                onCalculatorClick = { navController.navigate(Routes.CALCULATOR) },
                onLoadCostSummaryClick = { navController.navigate(Routes.LOAD_COST_SUMMARY) },
            )
        }
        composable(Routes.CALCULATOR) {
            LoadCostCalculatorScreen()
        }
        composable(Routes.COMPONENTS) {
            ComponentsScreen(
                onBackClick = { navController.popBackStack() },
                onPowdersClick = { navController.navigate(Routes.POWDER_LIST) },
                onPrimersClick = { navController.navigate(Routes.PRIMER_LIST) },
                onBulletsClick = { navController.navigate(Routes.BULLET_LIST) },
                onBrassClick = { navController.navigate(Routes.BRASS_LIST) },
            )
        }
        composable(Routes.LOAD_COST_SUMMARY) {
            LoadCostSummaryScreen(
                loadRecipeRepository = appContainer.loadRecipeRepository,
                powderRepository = appContainer.powderRepository,
                primerRepository = appContainer.primerRepository,
                bulletRepository = appContainer.bulletRepository,
                brassRepository = appContainer.brassRepository,
                onBackClick = { navController.popBackStack() },
            )
        }
        composable(Routes.LOADS) {
            LoadRecipeListScreen(
                repository = appContainer.loadRecipeRepository,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.ADD_EDIT_LOAD) },
                onRecipeClick = { recipeId -> navController.navigate("${Routes.LOAD_DETAIL}/$recipeId") },
            )
        }
        composable(Routes.ADD_EDIT_LOAD) {
            AddEditLoadRecipeScreen(
                loadRecipeRepository = appContainer.loadRecipeRepository,
                powderRepository = appContainer.powderRepository,
                primerRepository = appContainer.primerRepository,
                bulletRepository = appContainer.bulletRepository,
                brassRepository = appContainer.brassRepository,
                itemId = null,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Routes.ADD_EDIT_LOAD}/{${Routes.ITEM_ID_ARG}}",
            arguments = listOf(navArgument(Routes.ITEM_ID_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            AddEditLoadRecipeScreen(
                loadRecipeRepository = appContainer.loadRecipeRepository,
                powderRepository = appContainer.powderRepository,
                primerRepository = appContainer.primerRepository,
                bulletRepository = appContainer.bulletRepository,
                brassRepository = appContainer.brassRepository,
                itemId = backStackEntry.arguments?.getLong(Routes.ITEM_ID_ARG),
                onBackClick = { navController.popBackStack() },
                onSaved = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("updated", true)
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = "${Routes.LOAD_DETAIL}/{${Routes.ITEM_ID_ARG}}",
            arguments = listOf(navArgument(Routes.ITEM_ID_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong(Routes.ITEM_ID_ARG) ?: 0L
            LoadRecipeDetailScreen(
                recipeId = recipeId,
                loadRecipeRepository = appContainer.loadRecipeRepository,
                powderRepository = appContainer.powderRepository,
                primerRepository = appContainer.primerRepository,
                bulletRepository = appContainer.bulletRepository,
                brassRepository = appContainer.brassRepository,
                onBackClick = { navController.popBackStack() },
                onEditClick = { loadId -> navController.navigate("${Routes.ADD_EDIT_LOAD}/$loadId") },
            )
        }

        composable(Routes.POWDER_LIST) {
            PowderListScreen(
                repository = appContainer.powderRepository,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.ADD_EDIT_POWDER) },
                onEditClick = { powderId -> navController.navigate("${Routes.ADD_EDIT_POWDER}/$powderId") },
            )
        }
        composable(Routes.ADD_EDIT_POWDER) {
            AddEditPowderScreen(
                repository = appContainer.powderRepository,
                itemId = null,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Routes.ADD_EDIT_POWDER}/{${Routes.ITEM_ID_ARG}}",
            arguments = listOf(navArgument(Routes.ITEM_ID_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            AddEditPowderScreen(
                repository = appContainer.powderRepository,
                itemId = backStackEntry.arguments?.getLong(Routes.ITEM_ID_ARG),
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(Routes.PRIMER_LIST) {
            PrimerListScreen(
                repository = appContainer.primerRepository,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.ADD_EDIT_PRIMER) },
                onEditClick = { primerId -> navController.navigate("${Routes.ADD_EDIT_PRIMER}/$primerId") },
            )
        }
        composable(Routes.ADD_EDIT_PRIMER) {
            AddEditPrimerScreen(
                repository = appContainer.primerRepository,
                itemId = null,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Routes.ADD_EDIT_PRIMER}/{${Routes.ITEM_ID_ARG}}",
            arguments = listOf(navArgument(Routes.ITEM_ID_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            AddEditPrimerScreen(
                repository = appContainer.primerRepository,
                itemId = backStackEntry.arguments?.getLong(Routes.ITEM_ID_ARG),
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(Routes.BULLET_LIST) {
            BulletListScreen(
                repository = appContainer.bulletRepository,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.ADD_EDIT_BULLET) },
                onEditClick = { bulletId -> navController.navigate("${Routes.ADD_EDIT_BULLET}/$bulletId") },
            )
        }
        composable(Routes.ADD_EDIT_BULLET) {
            AddEditBulletScreen(
                repository = appContainer.bulletRepository,
                itemId = null,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Routes.ADD_EDIT_BULLET}/{${Routes.ITEM_ID_ARG}}",
            arguments = listOf(navArgument(Routes.ITEM_ID_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            AddEditBulletScreen(
                repository = appContainer.bulletRepository,
                itemId = backStackEntry.arguments?.getLong(Routes.ITEM_ID_ARG),
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(Routes.BRASS_LIST) {
            BrassListScreen(
                repository = appContainer.brassRepository,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.ADD_EDIT_BRASS) },
                onEditClick = { brassId -> navController.navigate("${Routes.ADD_EDIT_BRASS}/$brassId") },
            )
        }
        composable(Routes.ADD_EDIT_BRASS) {
            AddEditBrassScreen(
                repository = appContainer.brassRepository,
                itemId = null,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Routes.ADD_EDIT_BRASS}/{${Routes.ITEM_ID_ARG}}",
            arguments = listOf(navArgument(Routes.ITEM_ID_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            AddEditBrassScreen(
                repository = appContainer.brassRepository,
                itemId = backStackEntry.arguments?.getLong(Routes.ITEM_ID_ARG),
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
    }
}
