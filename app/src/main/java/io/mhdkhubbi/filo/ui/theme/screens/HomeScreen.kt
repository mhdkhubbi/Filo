package io.mhdkhubbi.filo.ui.theme.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.ui.theme.components.StorageInfo
import io.mhdkhubbi.filo.ui.theme.components.TopBar
import io.mhdkhubbi.filo.viewmodels.HomeScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit,
    viewModel: HomeScreenViewModel = viewModel()
) {
//    val context = LocalContext.current
//    val viewModelMedia = remember { MediaFoldersViewModel(context) }
//    val paths by viewModelMedia.paths.collectAsState()
//    LaunchedEffect(Unit) {
//        viewModelMedia.load(MediaType.IMAGES)
//    }


    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        TopBar()
        StorageInfo(
            onNavigation = { onNavigation(FileScreen("/storage/emulated/0")) },
            { viewModel.getUsagePercent() },
            viewModel.formateUsagePercent(),
            viewModel.getUsageLabel()
        )
        Spacer(Modifier.height(17.dp))
       // println("opeas ${paths.first()}")
//        Categories(
//            onNavigation = { onNavigation(FileScreen(paths.first())) },
//            path = paths.first(),
//
//        )



    }
}