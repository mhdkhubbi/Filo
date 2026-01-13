package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenUiState
import FileScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.domain.FileEntry
import io.mhdkhubbi.filo.domain.MediaType
import io.mhdkhubbi.filo.ui.theme.components.FileList
import io.mhdkhubbi.filo.ui.theme.components.FileRow
import io.mhdkhubbi.filo.ui.theme.components.TopBar

@Composable
fun FileScreen(
    fileViewModel: FileScreenViewModel,
    uiState: FileScreenUiState,
    path: String,
    backStack: NavBackStack<NavKey>,
    onNavigation: (NavKey) -> Unit
) {
    FileScreen(
        path = path,
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

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun FileScreen(
    path: String,
    onNavigation: (NavKey) -> Unit,
    load: (MediaType) -> Unit,
    backStack: NavBackStack<NavKey>,
    loadFiles: (String) -> Unit,
    uiState: FileScreenUiState,
    // keep callbacks for actions that mutate state
    onChangeDeletedOne: (String) -> Unit,
    onFolderNameChange: (String) -> Unit,
    onAddFolder: () -> Unit,
    onExecutePendingOperation: () -> Unit,
    onSelectAll: (List<FileEntry>) -> Unit,
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
    onChangeDestinationPath: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScreen = backStack.last()
    val isInFileScreen = currentScreen is FileScreen


    LaunchedEffect(path) {
        when (path) {
            "media_images_root" -> {
                load(MediaType.IMAGES)


            }

            "media_videos_root" -> {
                load(MediaType.VIDEOS)

            }

            "media_music_root" -> {
                load(MediaType.AUDIO)

            }

            "media_document_root" -> {
                load(MediaType.DOCUMENTS)

            }

            "media_download_root" -> {
                load(MediaType.DOWNLOADS)

            }

            else -> {

                loadFiles(path)
            }
        }

        if (uiState.activeOperation != null) {
            onChangeDestinationPath(path)


        }
    }


    LaunchedEffect(uiState.shouldSyncNavigation) {
        if (uiState.shouldSyncNavigation) {
            onResetNavigation(uiState.currentPath, backStack)
            onClearNavigationResetFlag()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (path != "media_images_root" && path != "media_videos_root" &&
                path != "media_music_root" && path != "media_document_root" &&
                path != "media_download_root"
            ) {
                ExtendedFloatingActionButton(onClick = { onCreateFolderDialogChange(true) }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Filled.CreateNewFolder,

                        contentDescription = "Add Folder"
                    )

                }
            }
        }

    ) { padding ->
        Column {
            TopBar(onNagivation={onNavigation(it)})
            Spacer(Modifier.height(8.dp))
            if (uiState.activeOperation != null && isInFileScreen && path != "media_images_root"
                && path != "media_videos_root" &&
                path != "media_music_root" && path != "media_document_root" &&
                path != "media_download_root"
            ) {

                Row {
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { onExecutePendingOperation() },
                        modifier = Modifier
                            .padding(end=15.dp).width(120.dp).height(35.dp)
                    ) {
                        Text("Paste here")
                    }


                }
            }

            if (uiState.isFileActionInProgress) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    onDismissRequest = { },
                    confirmButton = {},
                    dismissButton = {},
                    title = { Text("Processing") },
                    text = {
                        LinearProgressIndicator(
                            progress = { uiState.actionProgress.coerceIn(0.01f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                    }
                )
            }

            if (uiState.isCreateFolderDialogVisible) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
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
                            nameFolder = uiState.newFolderName,
                            onValueChange = onFolderNameChange,
                            wrongName = uiState.folderNameError
                        )
                    }
                )
            }

            if (uiState.isDeletionDialogVisible) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    onDismissRequest = { onShowDialogChange(false) },
                    confirmButton = {
                        TextButton(onClick = {
                            if (uiState.targetDeletionPath.isNotEmpty()) {
                                onDeleteOne(uiState.targetDeletionPath)
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
                directoryFile = uiState.directoryName,
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
                    onChangeDeletedOne(it)

                }
            )
        }
    }
}

@Composable
fun FolderField(
    wrongName: String,
    modifier: Modifier = Modifier,
    nameFolder: String = "",
    onValueChange: (String) -> Unit = {}
) {


    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 15.dp)
                .height(50.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                Modifier.padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.CreateNewFolder,
                    contentDescription = "Add folder",
                    modifier = Modifier.size(30.dp),

                    )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = nameFolder,
                    onValueChange = { onValueChange(it) },
                    singleLine = true,

                    decorationBox = { innerTextField ->
                        if (nameFolder.isEmpty()) {
                            Text(
                                "Enter name of folder", color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )

            }

        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(Modifier
            .height(15.dp)
            .padding(start = 5.dp)) {
            if (wrongName == "wrong") {
                Text(
                    text = "this name is already exist",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }


    }

}






