package io.mhdkhubbi.filo.ui.theme.screens

import FileScreenViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.ui.theme.components.FileList
import io.mhdkhubbi.filo.ui.theme.components.FloderViewMode


@Composable
fun FileScreen(path: String,
               onNavigation: (NavKey) -> Unit,
               viewModel: FileScreenViewModel = viewModel(),
                 ){


    Column {
        FloderViewMode()
        LaunchedEffect(path) {
              viewModel.loadFiles(path)
                  }
        FileList(onNavigation,viewModel.files,viewModel.isLoading)

    }


}
