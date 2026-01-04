package io.mhdkhubbi.filo.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.ui.theme.components.FloderViewMode
import io.mhdkhubbi.filo.ui.theme.components.FileList


@Composable
fun ContentScreen(path: String, onNavigation: (NavKey) -> Unit,
                 ){


    Column {
        FloderViewMode()
        FileList(path,onNavigation)

    }


}
