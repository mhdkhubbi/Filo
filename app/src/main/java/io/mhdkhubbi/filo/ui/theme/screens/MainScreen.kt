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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    homeViewModel: HomeScreenViewModel = viewModel(),
    fileViewModel: FileScreenViewModel = viewModel()

) {
    val backStack = rememberNavBackStack(HomeScreen)

    val onNavigation: (NavKey) -> Unit = { nav ->
        backStack.add(nav)
    }

    val currentScreen = backStack.last()
    val beforeHomeScreen = currentScreen is HomeScreen
    val uiState by fileViewModel.uiState.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {


        NavDisplay(
            backStack = backStack, onBack = {
                if (uiState.selectedPaths.isNotEmpty()) {
                    // First back press → clear selection
                    fileViewModel.clearSelection()
                } else {
                    // Second back press → navigate back
                    if (beforeHomeScreen) {
                        fileViewModel.cancelPendingOperation()
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

                // ---------------- HOME SCREEN ----------------

                entry<HomeScreen> {


                    HomeScreen(onNavigation = onNavigation)


                }

                // ---------------- FILE SCREEN ----------------

                entry<FileScreen> { entry ->


                    FileScreen(
                        path = entry.path,
                        onNavigation = onNavigation,
                        backStack = backStack,

                        // ViewModel actions
                        loadFiles = fileViewModel::loadFiles,
                        uiState = uiState,

                        onFolderNameChange = fileViewModel::folderNameChange,
                        onAddFolder = fileViewModel::addingFolder,

                        onExecutePendingOperation = fileViewModel::executePendingOperation,

                        onSelectAll = fileViewModel::selectAll,
                        onClearSelection = fileViewModel::clearSelection,
                        onToggleSelection = fileViewModel::toggleSelection,

                        onCopyItem = fileViewModel::onCopyItem,
                        onMoveItem = fileViewModel::onMoveItem,

                        onDeleteOne = fileViewModel::deleteOne,
                        onDeleteSelect = fileViewModel::deleteSelected,

                        onCopy = fileViewModel::onCopy,
                        onMove = fileViewModel::onMove,

                        onShowDialogChange = fileViewModel::ShowDialogChange,
                        onCreateFolderDialogChange = fileViewModel::CreateFolderDialogChange,

                        onResetNavigation = fileViewModel::resetNavigationTo,
                        onClearNavigationResetFlag = fileViewModel::clearNavigationResetFlag,

                        onChangeDestinationPath = fileViewModel::changedestinationPath
                    )
                }


            }
        )
    }

}


