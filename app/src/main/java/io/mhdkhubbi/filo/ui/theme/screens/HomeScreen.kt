package io.mhdkhubbi.filo.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.domain.MediaType
import io.mhdkhubbi.filo.domain.StorageVolume
import io.mhdkhubbi.filo.ui.theme.components.Categories
import io.mhdkhubbi.filo.ui.theme.components.StorageInfo
import io.mhdkhubbi.filo.ui.theme.components.TopBar

@Composable
fun HomeScreen(
    onNavigation: (NavKey) -> Unit,
    storageState: List<StorageVolume>,
    sizeStorage: (StorageVolume) -> Float,
    percentUsage: (StorageVolume) -> String,
    label: (StorageVolume) -> String,
    load: (MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopBar()
        Spacer(Modifier.height(15.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                for (e in storageState) {
                    StorageInfo(
                        onNavigation = { onNavigation(FileScreen(e.rootPath)) },
                        sizeStorage = sizeStorage(e),
                        percentUsage = percentUsage(e),
                        label = label(e),
                        name = if (!e.isExternal) "Internal Storage" else "CD Card"

                    )
                }
                Spacer(Modifier.height(15.dp))
            }

            item { Categories(onNavigation, load) }


        }
    }
}