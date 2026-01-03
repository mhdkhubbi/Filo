package io.mhdkhubbi.filo.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mhdkhubbi.filo.ui.theme.components.SearchField
import io.mhdkhubbi.filo.ui.theme.components.StorageInfo


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),

        ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("File Manager", fontSize = 30.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(40.dp))
            SearchField()
        }
        Spacer(Modifier.height(25.dp))
        StorageInfo()
        Spacer(Modifier.height(17.dp))
        StorageInfo()
        Text(modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            text = "Favorites",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )

    }
}
