package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen

enum class AppScreen(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Squads("Squads", Icons.Default.AccountCircle),
    Map("Map", Icons.Default.Place),
    Profile("Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(onEventClick: (String) -> Unit) {
    var currentScreen by remember { mutableStateOf(AppScreen.Home) }
    val strings = LocalAppStrings.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(
        if (currentScreen == AppScreen.Map) Color.Transparent else MaterialTheme.colorScheme.background
    )) {
        val isWideScreen = maxWidth > 800.dp

        Row(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
            if (isWideScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    header = {
                        // Optional Logo or Menu Icon
                        Icon(
                            Icons.Default.Menu, 
                            contentDescription = "Menu", 
                            modifier = Modifier.padding(vertical = 24.dp),
                            tint = PrimaryGreen
                        )
                    }
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    AppScreen.values().forEach { screen ->
                        NavigationRailItem(
                            icon = { Icon(screen.icon, contentDescription = screen.getLabel(strings)) },
                            label = { Text(screen.getLabel(strings)) },
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = PrimaryGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Scaffold(
                containerColor = if (currentScreen == AppScreen.Map) Color.Transparent else MaterialTheme.colorScheme.background,
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            AppScreen.values().forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.getLabel(strings)) },
                                    label = { Text(screen.getLabel(strings)) },
                                    selected = currentScreen == screen,
                                    onClick = { currentScreen = screen },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = PrimaryGreen.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        }
                    }
                },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    when (currentScreen) {
                        AppScreen.Home -> HomeScreen(onEventClick = onEventClick)
                        AppScreen.Squads -> GroupScreen()
                        AppScreen.Map -> MapScreen()
                        AppScreen.Profile -> ProfileScreen()
                    }
                }
            }
        }
    }
}

fun AppScreen.getLabel(strings: AppStrings): String {
    return when(this) {
        AppScreen.Home -> strings.homeTab
        AppScreen.Squads -> strings.squadsTab
        AppScreen.Map -> strings.mapTab
        AppScreen.Profile -> strings.profileTab
    }
}
