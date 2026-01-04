package io.mhdkhubbi.filo.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.R
import io.mhdkhubbi.filo.domain.formatSize
import io.mhdkhubbi.filo.ui.theme.Gray500
import io.mhdkhubbi.filo.ui.theme.screens.FolderScreen
import io.mhdkhubbi.filo.viewmodels.FolderViewModel

@Composable
fun FileList(
    path: String,
    onNavigation: (NavKey) -> Unit,
    viewModel: FolderViewModel = viewModel()
) {

    LaunchedEffect(path) {
        viewModel.loadFiles(path)
    }

    val files = viewModel.files

    LazyColumn {
        items(files) { fileName ->

            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .clickable {
                        if (fileName.isDirectory) {
                            onNavigation(FolderScreen(fileName.fullPath))
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.folder),
                    contentDescription = null,
                    tint = Color(0xFFFFCA28),
                    modifier = Modifier
                        .size(52.dp)

                )
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        fileName.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp
                    )
                    Row(Modifier.padding(top = 4.dp)) {
                        Text(
                            text = fileName.itemCount.toString() + " items.",
                            fontSize = 10.sp,
                            color = Gray500,
                            lineHeight = 10.sp
                        )
                        Text(
                            text = formatSize(fileName.sizeBytes),
                            fontSize = 10.sp,
                            color = Gray500,
                            lineHeight = 10.sp
                        )
                    }
                }

                Icon(
                    painter = painterResource(R.drawable.dots),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )


            }
        }
    }
}
