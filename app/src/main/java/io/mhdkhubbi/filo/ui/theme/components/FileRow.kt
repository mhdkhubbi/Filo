package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FileRow(
    directoryFile: String,
    isShown: Boolean,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column {
        Row {
            Spacer(
                Modifier
                    .width(20.dp)
                    .weight(1f)
            )
            AnimatedVisibility(
                visible = isShown,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FileOptions(
                    isShown = isShown,
                    onSelectAll = onSelectAll,
                    onDeselectAll = onDeselectAll,
                    onCopy = onCopy,
                    onMove = onMove,
                    onDelete = onDelete
                )
            }


        }
        Text(
            directoryFile,
            modifier = Modifier.padding(start = 15.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )


    }

}

@Composable
fun FileOptions(
    isShown: Boolean,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onCopy: () -> Unit = {},
    onMove: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    if (isShown) {
        Row {
            Icon(
                Icons.Default.Delete, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { onDelete() }
            )
            Icon(
                Icons.AutoMirrored.Filled.DriveFileMove, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "DriveFileMove",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        onMove()
                    }
            )
            Icon(
                Icons.Default.ContentCopy, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "ContentCopy",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable { onCopy() }
            )
            Icon(
                Icons.Default.Deselect, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "Deselect",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        onDeselectAll()
                    }
            )
            Icon(
                Icons.Default.SelectAll, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "SelectAll",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(30.dp)
                    .clickable {
                        onSelectAll()
                    }
            )
        }
    }

}