package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.R
import io.mhdkhubbi.filo.domain.FileType
import io.mhdkhubbi.filo.domain.FsEntry
import io.mhdkhubbi.filo.ui.theme.Gray500
import io.mhdkhubbi.filo.ui.theme.screens.FileScreen

@Composable
fun FileList(
    onNavigation: (NavKey) -> Unit,
    files: List<FsEntry>,
    isLoading: Boolean,
    selectedPaths: Set<String>,
    toggleSelection: (String) -> Unit,
    clearSelection: () -> Unit,
    onCopy: (String) -> Unit,
    onMove: (String) -> Unit,
    onDelete: (String) -> Unit,

    ) {
    val listState = rememberLazyListState()


    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        files.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("This folder is empty")
            }
        }

        else ->
            LazyColumn(state = listState) {
                items(files, key = { it.fullPath }) { fileName ->
                    val isSelected = selectedPaths.contains(fileName.fullPath)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                if (isSelected) Color.LightGray
                                else
                                    MaterialTheme.colorScheme.background
                            )
                            .combinedClickable(
                                onClick = {
                                    if (fileName.isDirectory) {
                                        clearSelection()
                                        onNavigation(FileScreen(fileName.fullPath))
                                    }
                                },
                                onLongClick = {
                                    toggleSelection(fileName.fullPath)
                                }

                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        FileIcon(fileName)
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Text(
                                fileName.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 15.sp
                            )
                            Row(Modifier.padding(top = 4.dp)) {
                                Text(
                                    text = if (fileName.isDirectory) fileName.itemCount.toString()
                                            + " items." else "",
                                    fontSize = 10.sp,
                                    color = Gray500,
                                    lineHeight = 10.sp
                                )
                                Text(
                                    text = fileName.sizeMega,
                                    fontSize = 10.sp,
                                    color = Gray500,
                                    lineHeight = 10.sp
                                )
                            }
                        }


                        OverflowMenu(path = fileName.fullPath,
                            onCopy = { path -> onCopy(path) },
                            onMove = { path -> onMove(path) },
                            onDelete = { path -> onDelete(path) }


                        )


                    }
                    Spacer(Modifier.height(5.dp))
                }
            }
    }
}

@Composable
fun OverflowMenu(
    path: String,
    onCopy: (String) -> Unit,
    onMove: (String) -> Unit,
    onDelete: (String) -> Unit,

    ) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box (
    ){

        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            offset = DpOffset(
                x = (-35).dp,
                y = (-35).dp
            )
        ) {
            DropdownMenuItem(
                text = { Text("Copy to") },
                onClick = {
                    menuExpanded = false
                    onCopy(path)
                }
            )

            DropdownMenuItem(
                text = { Text("Move to") },
                onClick = {
                    menuExpanded = false
                    onMove(path)
                }
            )

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    menuExpanded = false
                    onDelete(path)
                }
            )
        }


    }
}

@Composable
fun FileIcon(fileName: FsEntry) {

    when (fileName.type) {
        FileType.FOLDER -> Icon(
            painter = painterResource(R.drawable.folder),
            contentDescription = null,
            //  tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.APK -> Icon(
            Icons.Rounded.Android,
            contentDescription = null,
            //  tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.PDF -> Icon(
            Icons.Rounded.PictureAsPdf,
            contentDescription = null,
            //  tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.AUDIO -> Icon(
            Icons.Rounded.AudioFile,
            contentDescription = null,
            //  tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.IMAGE -> Icon(
            Icons.Rounded.Image,
            contentDescription = null,
            //   tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.VIDEO -> Icon(
            Icons.Rounded.VideoFile,
            contentDescription = null,
            //     tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.OTHER -> Icon(
            Icons.AutoMirrored.Rounded.InsertDriveFile,
            contentDescription = null,
            //    tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )
    }


}
