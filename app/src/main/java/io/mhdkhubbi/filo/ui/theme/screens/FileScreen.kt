package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.ui.theme.components.FileList
import io.mhdkhubbi.filo.ui.theme.components.FileRow
import io.mhdkhubbi.filo.ui.theme.components.TopBar


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun FileScreen(
    path: String,
    onNavigation: (NavKey) -> Unit,
    viewModel: FileScreenViewModel,
    backStack: NavBackStack<NavKey>
) {

    val currentScreen = backStack.last()
    val isInFileScreen = currentScreen is FileScreen
    LaunchedEffect(path) {
        viewModel.loadFiles(path)

        if (viewModel.pendingOperation != null) {
            viewModel.destinationPath = path


        }

    }
    LaunchedEffect(viewModel.shouldResetNavigation) {
        if (viewModel.shouldResetNavigation) {
            viewModel.resetNavigationTo(viewModel.currentPath, backStack)
            viewModel.clearNavigationResetFlag()
        }
    }
    Column {
        TopBar()
        Spacer(Modifier.height(20.dp))

        if (viewModel.pendingOperation != null && isInFileScreen) {
            Row {
                Button(
                    onClick = {
                        viewModel.executePendingOperation()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text("Paste here")
                }
            }
        }
        if (viewModel.isProcessing) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {},
                dismissButton = {},
                title = { Text("Processing") },
                text = {
                    LinearProgressIndicator(
                        progress = { viewModel.progress.coerceIn(0.01f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp)
                    )
                }
            )
        }

        if (viewModel.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showDialog = false },

                confirmButton = {
                    TextButton(onClick = {
                        viewModel.showDialog = false
                        viewModel.deleteSelected()
                    }) {
                        Text("Delete")
                    }
                },

                dismissButton = {
                    TextButton(onClick = {
                        viewModel.showDialog = false
                    }) {
                        Text("Cancel")

                    }
                },

                title = { Text("Delete selected items?") },
                text = { Text("This action cannot be undone.") }
            )
        }
        FileRow(
            directoryFile = viewModel.fileName,
            isShown = viewModel.isShown,
            onSelectAll = { viewModel.selectAll(viewModel.files) },
            onDeselectAll = { viewModel.clearSelection() },
            onCopy = {
                viewModel.onCopy()
                onNavigation(HomeScreen)

            },
            onMove = {
                viewModel.onMove()
                onNavigation(HomeScreen)
            },
            onDelete = {
                viewModel.showDialog = true
            }
        )

        FileList(
            onNavigation,
            files = viewModel.files,
            isLoading = viewModel.isLoading,
            selectedPaths = viewModel.selectedPaths,
            toggleSelection = { path -> viewModel.toggleSelection(path) },
            clearSelection = {viewModel.clearSelection()},
            onCopy = {viewModel.onCopyItem(it)
                onNavigation(HomeScreen)
                     },
            onMove = {viewModel.onMoveItem(it)
                onNavigation(HomeScreen)
                     },
            onDelete = {
                viewModel.deleteOne(it)
            }

        )

    }


}


