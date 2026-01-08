package io.mhdkhubbi.filo.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import io.mhdkhubbi.filo.ui.theme.components.StorageInfo
import io.mhdkhubbi.filo.ui.theme.components.TopBar
import io.mhdkhubbi.filo.viewmodels.HomeScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit,
    viewModel: HomeScreenViewModel = HomeScreenViewModel()
) {
    Column(
        modifier = modifier
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

        Text(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            text = "Favorites",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )


    }
}