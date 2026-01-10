package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenUiState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.domain.FsEntry
import io.mhdkhubbi.filo.ui.theme.Gray100
import io.mhdkhubbi.filo.ui.theme.Gray500
import io.mhdkhubbi.filo.ui.theme.components.FileList
import io.mhdkhubbi.filo.ui.theme.components.FileRow
import io.mhdkhubbi.filo.ui.theme.components.TopBar


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun FileScreen(
    path: String,
    onNavigation: (NavKey) -> Unit,
    backStack: NavBackStack<NavKey>,
    loadFiles: (String) -> Unit,
    uiState: FileScreenUiState,
    // keep callbacks for actions that mutate state
    onFolderNameChange: (String) -> Unit,
    onAddFolder: () -> Unit,
    onExecutePendingOperation: () -> Unit,
    onSelectAll: (List<FsEntry>) -> Unit,
    onClearSelection: () -> Unit,
    onToggleSelection: (String) -> Unit,
    onCopyItem: (String) -> Unit,
    onMoveItem: (String) -> Unit,
    onDeleteOne: (String) -> Unit,
    onDeleteSelect: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    onShowDialogChange: (Boolean) -> Unit,
    onCreateFolderDialogChange: (Boolean) -> Unit,
    onResetNavigation: (String, NavBackStack<NavKey>) -> Unit,
    onClearNavigationResetFlag: () -> Unit,
    onChangedestinationPath:(String?)->Unit,
) {
    val currentScreen = backStack.last()
    val isInFileScreen = currentScreen is FileScreen

    LaunchedEffect(path) {
        loadFiles(path)
        if (uiState.pendingOperation != null) {
            onChangedestinationPath( path)


        }
    }

    LaunchedEffect(uiState.shouldResetNavigation) {
        if (uiState.shouldResetNavigation) {
            onResetNavigation(uiState.currentPath, backStack)
            onClearNavigationResetFlag()
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { onCreateFolderDialogChange(true) }) {
                Text("Add Folder")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TopBar()
            Spacer(Modifier.height(20.dp))

            if (uiState.pendingOperation != null && isInFileScreen) {
                Row {
                    Button(
                        onClick = { onExecutePendingOperation() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text("Paste here")
                    }
                }
            }

            if (uiState.isProcessing) {
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = {},
                    dismissButton = {},
                    title = { Text("Processing") },
                    text = {
                        LinearProgressIndicator(
                            progress = { uiState.progress.coerceIn(0.01f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                        )
                    }
                )
            }

            if (uiState.createFolderDialog) {
                AlertDialog(
                    onDismissRequest = { onCreateFolderDialogChange(false) },
                    confirmButton = {
                        TextButton(onClick = {
                            onAddFolder()

                        }) { Text("Add") }
                    },
                    dismissButton = {
                        TextButton(onClick = { onCreateFolderDialogChange(false) }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Adding Folder") },
                    text = {
                        FolderField(
                            uiState.folderNameToAdd,
                            onFolderNameChange,
                            uiState.wrongName
                        )
                    }
                )
            }

            if (uiState.showDialog) {
                AlertDialog(
                    onDismissRequest = { onShowDialogChange(false) },
                    confirmButton = {
                        TextButton(onClick = {
                            if (uiState.deletedOne.isNotEmpty()) {
                                onDeleteOne(uiState.deletedOne)
                                onShowDialogChange(false)
                            } else {
                                onShowDialogChange(false)
                                onDeleteSelect()
                            }
                        }) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = { onShowDialogChange(false) }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Delete selected items?") },
                    text = { Text("This action cannot be undone.") }
                )
            }

            FileRow(
                directoryFile = uiState.fileName,
                isShown = uiState.selectedPaths.isNotEmpty(),
                onSelectAll = { onSelectAll(uiState.files) },
                onDeselectAll = { onClearSelection() },
                onCopy = {
                    onCopy()
                    onNavigation(HomeScreen)
                },
                onMove = {
                    onMove()
                    onNavigation(HomeScreen)
                },
                onDelete = { onShowDialogChange(true) }
            )

            FileList(
                onNavigation = onNavigation,
                files = uiState.files,
                isLoading = uiState.isLoading,
                selectedPaths = uiState.selectedPaths,
                toggleSelection = onToggleSelection,
                clearSelection = onClearSelection,
                onCopy = {
                    onCopyItem(it)
                    onNavigation(HomeScreen)
                },
                onMove = {
                    onMoveItem(it)
                    onNavigation(HomeScreen)
                },
                onDelete = {
                    onShowDialogChange(true)
                    onDeleteOne(it)
                }
            )
        }
    }
}
@Composable
fun FolderField(
    nameFolder: String="",
    onValueChange: (String) -> Unit={},
    wrongName:String
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .height(200.dp)
            .background(Gray100, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row( modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.AddBox,
                contentDescription = "Add folder",
                tint = Gray500
            )

            Spacer(Modifier.width(8.dp))
            Column{
                TextField(
                    value = nameFolder,
                    onValueChange = {onValueChange(it)},
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black)
                    ,

                    )
                if (wrongName=="wrong") {
                    Text(
                        text = "this name is already exist choose another name",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }
}



//@Composable
//fun FileScreen(
//    path: String,
//    onNavigation: (NavKey) -> Unit,
//    viewModel: FileScreenViewModel,
//    backStack: NavBackStack<NavKey>,
//    loadFiles:(String)->Unit,
//
//) {
//
//    val currentScreen = backStack.last()
//    val isInFileScreen = currentScreen is FileScreen
//    LaunchedEffect(path) {
//       loadFiles(path)
//
//        if (viewModel.pendingOperation != null) {
//            viewModel.destinationPath = path
//
//
//        }
//
//    }
//    LaunchedEffect(viewModel.shouldResetNavigation) {
//        if (viewModel.shouldResetNavigation) {
//            viewModel.resetNavigationTo(viewModel.currentPath, backStack)
//            viewModel.clearNavigationResetFlag()
//        }
//    }
//    Scaffold(floatingActionButton =  {
//        ExtendedFloatingActionButton(onClick = {
//            viewModel.createFolderDialog=true
//        }) { Text("Add Folder") }
//
//
//    }) {
//        Column {
//
//
//
//            TopBar()
//            Spacer(Modifier.height(20.dp))
//
//            if (viewModel.pendingOperation != null && isInFileScreen) {
//                Row {
//                    Button(
//                        onClick = {
//                            viewModel.executePendingOperation()
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(10.dp)
//                    ) {
//                        Text("Paste here")
//                    }
//                }
//            }
//            if (viewModel.isProcessing) {
//                AlertDialog(
//                    onDismissRequest = { },
//                    confirmButton = {},
//                    dismissButton = {},
//                    title = { Text("Processing") },
//                    text = {
//                        LinearProgressIndicator(
//                            progress = { viewModel.progress.coerceIn(0.01f, 1f) },
//                            modifier = Modifier.fillMaxWidth().height(6.dp)
//                        )
//                    }
//                )
//            }
//            if(viewModel.createFolderDialog){
//                AlertDialog(
//                    onDismissRequest = { viewModel.createFolderDialog = false },
//
//                    confirmButton = {
//                        TextButton(onClick = {
//                                viewModel.addingFolder()
//                            if(viewModel.wrongName=="right"){
//                                viewModel.createFolderDialog = false
//
//
//                            }
//
//
//
//                        }) {
//                            Text("Add")
//                        }
//                    },
//
//                    dismissButton = {
//                        TextButton(onClick = {
//                            viewModel.createFolderDialog = false
//                        }) {
//                            Text("Cancel")
//
//                        }
//                    },
//
//                    title = { Text("Adding Folder") },
//                    text = {
//                        FolderField(viewModel.folderNameToAdd,
//                            {viewModel.folderNameChange(it)},
//                            viewModel.wrongName)
//                    }
//                )
//            }
//            if (viewModel.showDialog) {
//                AlertDialog(
//                    onDismissRequest = { viewModel.showDialog = false },
//
//                    confirmButton = {
//                        TextButton(onClick = {
//                            if(viewModel.deletedOne.isNotEmpty()){
//                                viewModel.deleteOne(viewModel.deletedOne)
//                                viewModel.showDialog = false
//
//                            }else {
//                                viewModel.showDialog = false
//                                viewModel.deleteSelected()
//                            }
//                        }) {
//                            Text("Delete")
//                        }
//                    },
//
//                    dismissButton = {
//                        TextButton(onClick = {
//                            viewModel.showDialog = false
//                        }) {
//                            Text("Cancel")
//
//                        }
//                    },
//
//                    title = { Text("Delete selected items?") },
//                    text = { Text("This action cannot be undone.") }
//                )
//            }
//
//            FileRow(
//                directoryFile = viewModel.fileName,
//                isShown = viewModel.isShown,
//                onSelectAll = { viewModel.selectAll(viewModel.files) },
//                onDeselectAll = { viewModel.clearSelection() },
//                onCopy = {
//                    viewModel.onCopy()
//                    onNavigation(HomeScreen)
//
//                },
//                onMove = {
//                    viewModel.onMove()
//                    onNavigation(HomeScreen)
//                },
//                onDelete = {
//                    viewModel.showDialog = true
//                }
//            )
//
//            FileList(
//                onNavigation,
//                files = viewModel.files,
//                isLoading = viewModel.isLoading,
//                selectedPaths = viewModel.selectedPaths,
//                toggleSelection = { path -> viewModel.toggleSelection(path) },
//                clearSelection = { viewModel.clearSelection() },
//                onCopy = {
//                    viewModel.onCopyItem(it)
//                    onNavigation(HomeScreen)
//                },
//                onMove = {
//                    viewModel.onMoveItem(it)
//                    onNavigation(HomeScreen)
//                },
//                onDelete = {
//                    viewModel.showDialog=true
//                    viewModel.deletedOne=it
//
//                }
//
//            )
//
//        }
//    }
//
//
//}

