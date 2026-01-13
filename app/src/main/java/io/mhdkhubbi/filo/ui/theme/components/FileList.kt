package io.mhdkhubbi.filo.ui.theme.components

import Gray500
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
import androidx.compose.material.icons.filled.MoreVert
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
import io.mhdkhubbi.filo.domain.FileEntry
import io.mhdkhubbi.filo.domain.FileType
import io.mhdkhubbi.filo.ui.theme.screens.FileScreen

@Composable
fun FileList(
    onNavigation: (NavKey) -> Unit,
    files: List<FileEntry>,
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
            LazyColumn(state = listState,modifier=Modifier.padding(start=2.dp),) {
                items(files, key = { it.path }) { fileName ->
                    val isSelected = selectedPaths.contains(fileName.path)
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
                                    if (fileName.isFolder) {
                                        clearSelection()
                                        onNavigation(FileScreen(fileName.path))
                                    }
                                },
                                onLongClick = {

                                        toggleSelection(fileName.path)

                                }

                            )
                            .padding(10.dp,end=0.dp,top=10.dp,bottom=10.dp),
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
                                    text = if (fileName.isFolder) fileName.childrenCount.toString()
                                            + " items." else "",
                                    fontSize = 10.sp,
                                    lineHeight = 10.sp
                                )
                                Text(
                                    text = fileName.formattedSize,
                                    fontSize = 10.sp,
                                    lineHeight = 10.sp
                                )
                            }
                        }


                        OverflowMenu(path = fileName.path,
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
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box {

        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Gray500)
        }
        DropdownMenu(containerColor = MaterialTheme.colorScheme.onPrimary,
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
fun FileIcon(fileName: FileEntry, modifier: Modifier = Modifier) {

    when (fileName.type) {
        FileType.FOLDER -> Icon(
            painter = painterResource(R.drawable.folder),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.APK -> Icon(
            painter = painterResource(R.drawable.apk),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.PDF -> Icon(
            painter = painterResource(R.drawable.pdf),
            contentDescription = null,
              tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.AUDIO -> Icon(
            painter = painterResource(R.drawable.audio),
            contentDescription = null,
              tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.IMAGE -> Icon(
            painter = painterResource(R.drawable.image),
            contentDescription = null,
              tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.VIDEO -> Icon(
            painter = painterResource(R.drawable.video),
            contentDescription = null,
               tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )

        FileType.OTHER -> Icon(
            painter = painterResource(R.drawable.folder),
            contentDescription = null,
            //    tint = Color.Unspecified,
            modifier = Modifier
                .size(52.dp)

        )


    }


}
