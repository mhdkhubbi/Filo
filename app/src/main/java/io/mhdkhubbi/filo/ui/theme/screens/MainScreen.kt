package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.mhdkhubbi.filo.viewmodels.HomeScreenViewModel


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainScreen(modifier: Modifier = Modifier,
               homeViewModel: HomeScreenViewModel=viewModel() ,
               fileViewModel: FileScreenViewModel=viewModel()
) {

    val backStack = rememberNavBackStack(HomeScreen)
    val onNavigation: (NavKey) -> Unit = {
        backStack.add(it)

    }
    val currentScreen = backStack.last()
    val beforeHomeScreen = currentScreen is HomeScreen
    val uiStateState = fileViewModel.uiState.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp),
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (uiStateState.value.selectedPaths.isNotEmpty()) {
                    // First back press → clear selection only
                    fileViewModel.clearSelection()

                } else {
                    // Second back press → navigate back

                    if(beforeHomeScreen){
                        fileViewModel.cancelPendingOperation()
                      //  backStack.removeLastOrNull()
                    }
                    backStack.removeLastOrNull()
                }
            }
            ,
            entryProvider = entryProvider {
                entry<HomeScreen> {

                        HomeScreen(onNavigation = onNavigation)

                }
                entry<FileScreen> { entry ->
                   FileScreen(
                       path = entry.path,
                       onNavigation = onNavigation,
                       backStack = backStack,
                       loadFiles = fileViewModel::loadFiles,
                       uiState = uiStateState.value,
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
                       onChangedestinationPath = fileViewModel::changedestinationPath
                   )

                }

            }

        )
    }

}

