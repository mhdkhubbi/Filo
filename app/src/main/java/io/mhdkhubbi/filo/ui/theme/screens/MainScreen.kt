package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.mhdkhubbi.filo.ui.theme.components.TopBar
import io.mhdkhubbi.filo.viewmodels.HomeScreenViewModel


@Composable
fun MainScreen(modifier: Modifier = Modifier, homeViewModel: HomeScreenViewModel = viewModel(),
               fileViewModel: FileScreenViewModel = viewModel()
) {

    val backStack = rememberNavBackStack(HomeScreen)
    val onNavigation: (NavKey) -> Unit = {
        backStack.add(it)

    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp),
    ) {

        TopBar()
        Spacer(Modifier.height(20.dp))
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (fileViewModel.selectedPaths.isNotEmpty()) {
                    // First back press → clear selection only
                    fileViewModel.clearSelection()
                } else {
                    // Second back press → navigate back
                    backStack.removeLastOrNull()
                }
            }
            ,
            entryProvider = entryProvider {
                entry<HomeScreen> {
                    HomeScreen(onNavigation = onNavigation)
                }
                entry<FolderScreen> { entry ->
                    FileScreen(path = entry.path, onNavigation = onNavigation,fileViewModel)
                }
            }

        )
    }

}

