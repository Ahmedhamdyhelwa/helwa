package com.helwa.lifemanager

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.helwa.lifemanager.ui.screens.AddEditTaskScreen
import com.helwa.lifemanager.ui.screens.AllTasksScreen
import com.helwa.lifemanager.ui.screens.DashboardScreen
import com.helwa.lifemanager.ui.screens.SettingsScreen
import com.helwa.lifemanager.ui.theme.LifeManagerTheme
import com.helwa.lifemanager.ui.viewmodel.SettingsViewModel
import com.helwa.lifemanager.ui.viewmodel.TaskViewModel
import com.helwa.lifemanager.work.DailySummaryWorker

class MainActivity : ComponentActivity() {

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

            // جدولة الملخص الصباحي عند فتح التطبيق
            androidx.compose.runtime.LaunchedEffect(settings.morningHour, settings.morningMinute) {
                DailySummaryWorker.schedule(applicationContext, settings.morningHour, settings.morningMinute)
            }

            LifeManagerTheme(themeMode = settings.themeMode) {
                // فرض اتجاه RTL في كل الواجهة
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppRoot(taskViewModel, settingsViewModel)
                }
            }
        }
    }
}

private sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Today : Tab("today", "اليوم", Icons.Filled.Today)
    data object All : Tab("all", "الكل", Icons.AutoMirrored.Filled.List)
    data object Settings : Tab("settings", "الإعدادات", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot(taskViewModel: TaskViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val tabs = listOf(Tab.Today, Tab.All, Tab.Settings)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBars = currentRoute in tabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    val currentDestination = backStackEntry?.destination
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Tab.Today.route || currentRoute == Tab.All.route) {
                FloatingActionButton(onClick = { navController.navigate("edit/0") }) {
                    Icon(Icons.Filled.Add, contentDescription = "إضافة مهمة")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Today.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Tab.Today.route) {
                DashboardScreen(
                    taskViewModel = taskViewModel,
                    settingsViewModel = settingsViewModel,
                    onTaskClick = { id -> navController.navigate("edit/$id") }
                )
            }
            composable(Tab.All.route) {
                AllTasksScreen(
                    viewModel = taskViewModel,
                    onTaskClick = { id -> navController.navigate("edit/$id") }
                )
            }
            composable(Tab.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
            composable("edit/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull() ?: 0L
                AddEditTaskScreen(
                    taskId = id,
                    viewModel = taskViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
