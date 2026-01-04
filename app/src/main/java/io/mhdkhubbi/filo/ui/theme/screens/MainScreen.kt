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
import io.mhdkhubbi.filo.ui.theme.components.FolderComose
import io.mhdkhubbi.filo.ui.theme.components.StorageInfo
import io.mhdkhubbi.filo.ui.theme.components.TopBar


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),


        ) {

        TopBar()
        Spacer(Modifier.height(25.dp))
        StorageInfo()
        Spacer(Modifier.height(17.dp))
        StorageInfo()
        Text(modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            text = "Favorites",
            fontWeight = FontWeight.SemiBold,
            fontSize = 21.sp,
        )
        FolderComose()

    }
}
