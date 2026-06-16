package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    Scaffold(
        containerColor = Slate950,
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in listOf("play_home", "levels", "account")) {
                NavigationBar(
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                    containerColor = Slate900.copy(alpha = 0.9f),
                    contentColor = Slate500
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "levels",
                        onClick = { navController.navigate("levels") { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Default.List, contentDescription = "Levels") },
                        label = { Text("LEVELS", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Indigo400,
                            selectedTextColor = Indigo400,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500,
                            indicatorColor = Indigo500.copy(alpha = 0.2f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "play_home",
                        onClick = { navController.navigate("play_home") { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Play") },
                        label = { Text("PLAY", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Indigo400,
                            selectedTextColor = Indigo400,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500,
                            indicatorColor = Indigo500.copy(alpha = 0.2f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "account",
                        onClick = { navController.navigate("account") { launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                        label = { Text("PROFILE", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Indigo400,
                            selectedTextColor = Indigo400,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500,
                            indicatorColor = Indigo500.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = "play_home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("play_home") {
                PlayHomeScreen(viewModel, onStartLevel = { level ->
                    navController.navigate("puzzle/${level}")
                })
            }
            composable("levels") {
                LevelsScreen(viewModel, onLevelClick = { level ->
                    navController.navigate("puzzle/${level}")
                })
            }
            composable("account") {
                AccountScreen(viewModel)
            }
            composable(
                "puzzle/{levelId}",
                arguments = listOf(navArgument("levelId") { type = NavType.IntType })
            ) { backStackEntry ->
                val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
                PuzzleScreen(
                    viewModel = viewModel, 
                    levelNumber = levelId,
                    onBack = { navController.popBackStack() },
                    onComplete = { stars, timeTaken, hints ->
                        // Finish level and goto complete screen
                        viewModel.finishLevel(levelId, stars, timeTaken, hints)
                        navController.navigate("complete/${levelId}/${stars}") {
                            popUpTo("play_home") // pop up back so back button works nicely
                        }
                    }
                )
            }
            composable(
                "complete/{levelId}/{stars}",
                arguments = listOf(
                    navArgument("levelId") { type = NavType.IntType },
                    navArgument("stars") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
                val stars = backStackEntry.arguments?.getInt("stars") ?: 0
                LevelCompleteScreen(
                    levelNumber = levelId,
                    stars = stars,
                    onNext = { 
                        navController.navigate("puzzle/${levelId + 1}") {
                            popUpTo("play_home")
                        } 
                    },
                    onReplay = {
                        navController.navigate("puzzle/${levelId}") {
                            popUpTo("play_home")
                        } 
                    },
                    onHome = {
                        navController.navigate("play_home") {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}
