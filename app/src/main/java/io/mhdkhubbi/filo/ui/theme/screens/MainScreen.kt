package io.mhdkhubbi.filo.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay


@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(HomeScreen)
    val onNavigation: (NavKey) -> Unit = {
        backStack.add(it)
    }
    val onClearBackStack: () -> Unit = {
        while (backStack.size > 1) {
            backStack.removeLastOrNull()

        }

    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeScreen> {
                HomeScreen(onNavigation=onNavigation)
            }
            entry<FolderScreen> { entry ->
                FileScreen(path = entry.path, onNavigation = onNavigation)
            }
        }

    )

}

