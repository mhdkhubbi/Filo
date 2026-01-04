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

@Composable
fun HomeScreen(modifier: Modifier = Modifier,onNavigation: (NavKey) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),


        ) {

        TopBar()
        Spacer(Modifier.height(25.dp))
        StorageInfo(
            onNavigation = { onNavigation(FolderScreen("/storage/emulated/0")) }
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