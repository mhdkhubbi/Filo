package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.ui.theme.Gray400

@Composable
fun FileRow(directoryFile: String,
            isShown:Boolean,
            onSelectAll: () -> Unit,
            onDeselectAll: () -> Unit,
            onCopy: () -> Unit,
            onMove: () -> Unit,
            onDelete: () -> Unit,) {

    Row(Modifier.fillMaxWidth()) {

        Text(
            directoryFile,
            modifier = Modifier.padding(start = 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
        Spacer(Modifier.weight(1f))
        FileOptions(isShown,onSelectAll,onDeselectAll,onCopy,onMove,onDelete)
    }

}

@Composable
fun FileOptions(isShown:Boolean,
                onSelectAll: () -> Unit,
                onDeselectAll: () -> Unit,
                onCopy: () -> Unit={},
                onMove: () -> Unit={},
                onDelete: () -> Unit={},
){
    if(isShown) {
        Row {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Search",
                tint = Gray400,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(24.dp)
                    .clickable{onDelete()}
            )
            Icon(
                Icons.AutoMirrored.Filled.DriveFileMove,
                contentDescription = "Search",
                tint = Gray400,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(24.dp)
                    .clickable{
                        onMove()
                    }
            )
            Icon(
                Icons.Default.ContentCopy,
                contentDescription = "Search",
                tint = Gray400,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(24.dp)
                    .clickable{ onCopy()}
            )
            Icon(
                Icons.Default.Deselect,
                contentDescription = "Search",
                tint = Gray400,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(24.dp)
                    .clickable{
                        onDeselectAll()
                    }
            )
            Icon(
                Icons.Default.SelectAll,
                contentDescription = "Search",
                tint = Gray400,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(24.dp)
                    .clickable{
                        onSelectAll()
                    }
            )
        }
    }

}