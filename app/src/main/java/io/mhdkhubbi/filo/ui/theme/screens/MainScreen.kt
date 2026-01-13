package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenViewModel
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.mhdkhubbi.filo.viewmodels.HomeScreenViewModel

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,

) {
    val backStack = rememberNavBackStack(HomeScreen)

    val onNavigation: (NavKey) -> Unit = { nav ->
        backStack.add(nav)
    }

    val currentScreen = backStack.last()
    val beforeHomeScreen = currentScreen is HomeScreen
    val context = LocalContext.current
    val fileViewModel = remember { FileScreenViewModel(context) }
    val uiState by fileViewModel.uiState.collectAsStateWithLifecycle()

    val homeViewModel: HomeScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HomeScreenViewModel(context) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {

        NavDisplay(
            backStack = backStack, onBack = {
                if (uiState.selectedPaths.isNotEmpty()) {
                    // First back press → clear selection
                    fileViewModel.clearSelection()
                } else {
                    // Second back press → navigate back
                    if (beforeHomeScreen) {
                        fileViewModel.cancelActiveOperation()
                    }
                    backStack.removeLastOrNull()
                }

            }, transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = LinearOutSlowInEasing
                    )
                ) togetherWith ExitTransition.None

            },

            popTransitionSpec = {
                EnterTransition.None togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutLinearInEasing
                    )
                )

            },

            predictivePopTransitionSpec = {
                EnterTransition.None togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutLinearInEasing
                    )
                )

            }
            ,
            entryProvider = entryProvider {
                entry<HomeScreen> {
                    HomeScreen(
                        onNavigation = onNavigation,
                        storageState = homeViewModel.stats,
                        sizeStorage = homeViewModel::getUsagePercent,
                        percentUsage = homeViewModel::formatUsagePercent,
                        label = homeViewModel::getUsageLabel,
                        load = fileViewModel::loadMediaFolders,
                    )
                }

                entry<FileScreen> { entry ->
                    FileScreen(
                        path = entry.path,
                        onNavigation = onNavigation,
                        backStack = backStack,
                        load = fileViewModel::loadMediaFolders,
                        // ViewModel actions
                        loadFiles = fileViewModel::loadFiles,
                        uiState = uiState,
                        onChangeDeletedOne = fileViewModel::deleteItemChange,
                        onFolderNameChange = fileViewModel::folderNameChange,
                        onAddFolder = fileViewModel::addingFolder,

                        onExecutePendingOperation = fileViewModel::executeActiveOperation,

                        onSelectAll = fileViewModel::selectAll,
                        onClearSelection = fileViewModel::clearSelection,
                        onToggleSelection = fileViewModel::toggleSelection,

                        onCopyItem = fileViewModel::copyItemFlag,
                        onMoveItem = fileViewModel::moveItemFlag,

                        onDeleteOne = fileViewModel::deleteItem,
                        onDeleteSelect = fileViewModel::deleteAll,

                        onCopy = fileViewModel::copyAllFlag,
                        onMove = fileViewModel::moveAllFlag,

                        onShowDialogChange = fileViewModel::showDialogFlag,
                        onCreateFolderDialogChange = fileViewModel::folderDialogFlag,

                        onResetNavigation = fileViewModel::resetNavigationTo,
                        onClearNavigationResetFlag = fileViewModel::clearNavigationResetFlag,

                        onChangeDestinationPath = fileViewModel::targetPathChange

                    )
                }
                entry<InfoScreen> {
                    InfoScreen()
                }


            }
        )
    }

}


